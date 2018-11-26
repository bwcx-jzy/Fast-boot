package cn.jiangzeyin.common.validator;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HtmlUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.EnableCommonBoot;
import cn.jiangzeyin.common.JsonMessage;
import cn.jiangzeyin.common.interceptor.BaseInterceptor;
import cn.jiangzeyin.common.interceptor.InterceptorPattens;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 参数拦截器  验证参数是否正确  排序号是：-100
 * <p>
 * 配置方法 @see
 * <p>
 * Created by jiangzeyin on 2018/8/21.
 *
 * @author jiangzeyin
 * @see cn.jiangzeyin.common.ApplicationBuilder#addInterceptor(java.lang.Class)
 * @see EnableCommonBoot#parameterValidator()
 */
@InterceptorPattens(sort = -100)
public class ParameterInterceptor extends BaseInterceptor {

    private static volatile Interceptor interceptor = new DefaultInterceptor();

    /**
     * 设置参数拦截器 拦截响应接口
     *
     * @param interceptor 接口
     */
    public static void setInterceptor(Interceptor interceptor) {
        ParameterInterceptor.interceptor = interceptor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.preHandle(request, response, handler);
        // 没有实现拦截器响应
        if (interceptor == null) {
            return true;
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            if (methodParameters != null) {
                for (MethodParameter item : methodParameters) {
                    ValidatorConfig validatorConfig = item.getParameterAnnotation(ValidatorConfig.class);
                    if (validatorConfig == null) {
                        continue;
                    }
                    ValidatorItem[] validatorItems = validatorConfig.value();
                    String name = item.getParameterName();
                    if (name == null) {
                        continue;
                    }
                    // 获取值
                    String value;
                    // 指定name
                    String configName = validatorConfig.name();
                    if (StrUtil.isNotEmpty(configName)) {
                        value = request.getParameter(configName);
                    } else {
                        value = request.getParameter(name);
                    }
                    //
                    RequestParam requestParam = item.getParameterAnnotation(RequestParam.class);
                    if (requestParam != null && StrUtil.isNotEmpty(requestParam.name())) {
                        value = request.getParameter(requestParam.name());
                    }
                    // 自定义
                    if (value == null && interceptor != null) {
                        value = interceptor.getParameter(request, name);
                    }
                    // 默认值
                    if (value == null && !ValueConstants.DEFAULT_NONE.equals(validatorConfig.defaultVal())) {
                        value = validatorConfig.defaultVal();
                    }
                    if (value == null && null != requestParam && !ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
                        //   默认值
                        value = requestParam.defaultValue();
                    }
                    // 验证每一项
                    for (ValidatorItem validatorItem : validatorItems) {
                        if (validatorItem.unescape()) {
                            value = HtmlUtil.unescape(value);
                        }
                        if (validatorItem.value() == ValidatorRule.CUSTOMIZE) {
                            if (!customize(handlerMethod, validatorConfig, validatorItem, name, value)) {
                                return false;
                            }
                            // 自定义条件只识别一次
                            break;
                        }
                        if (!validator(validatorItem, value)) {
                            //错误
                            interceptor.error(request, response, name, value, validatorItem);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 自定义参数效验
     *
     * @param handlerMethod   method
     * @param validatorConfig config
     * @param validatorItem   效验规则
     * @param name            参数名
     * @param value           值
     * @return true 通过效验
     * @throws InvocationTargetException 反射异常
     * @throws IllegalAccessException    反射异常
     */
    private boolean customize(HandlerMethod handlerMethod, ValidatorConfig validatorConfig, ValidatorItem validatorItem, String name, String value) throws InvocationTargetException, IllegalAccessException {
        // 自定义验证
        Method method;
        try {
            method = ReflectUtil.getMethod(handlerMethod.getBeanType(), validatorConfig.customizeMethod(), String.class, String.class);
        } catch (SecurityException sE) {
            // 没有权限访问 直接拦截
            DefaultSystemLog.ERROR().error(sE.getMessage(), sE);
            interceptor.error(request, response, name, value, validatorItem);
            return false;
        }
        if (method == null) {
            // 没有配置对应方法
            DefaultSystemLog.ERROR().error(handlerMethod.getBeanType() + "未配置验证方法：" + validatorConfig.customizeMethod());
            interceptor.error(request, response, name, value, validatorItem);
            return false;
        }
        Object obj = method.invoke(handlerMethod.getBean(), name, value);
        if (!Convert.toBool(obj, false)) {
            interceptor.error(request, response, name, value, validatorItem);
            return false;
        }
        return true;
    }

    /**
     * 获取长度范围
     *
     * @param range 范围
     * @return int数组
     */
    private int[] spiltRange(String range) {
        if (StrUtil.isEmpty(range)) {
            return null;
        }
        if (range.contains(StrUtil.COLON)) {
            // 范围
            String[] ranges = StrUtil.split(range, StrUtil.COLON);
            if (ranges != null && ranges.length == 2) {
                int start = Convert.toInt(ranges[0]);
                int end = Convert.toInt(ranges[1]);
                return new int[]{start, end};
            }
        } else {

            // 具体某个值
            int len = Convert.toInt(range);
            return new int[]{len};
        }
        return null;
    }

    private Double[] spiltRangeDouble(String range) {
        if (StrUtil.isEmpty(range)) {
            return null;
        }
        Double[] doubles = new Double[3];
        if (range.contains(StrUtil.BRACKET_START) && range.endsWith(StrUtil.BRACKET_END)) {
            int start = range.indexOf(StrUtil.BRACKET_START);
            int end = range.indexOf(StrUtil.BRACKET_END);
            int len = Convert.toInt(range.substring(start + 1, end));
            doubles[2] = (double) len;
            range = range.substring(0, start);
        }
        if (range.contains(StrUtil.COLON)) {
            String[] ranges = StrUtil.split(range, StrUtil.COLON);
            if (ranges != null && ranges.length == 2) {
                doubles[0] = Convert.toDouble(ranges[0]);
                doubles[1] = Convert.toDouble(ranges[1]);
            }
        } else {
            doubles[0] = Convert.toDouble(range);
        }
        return doubles;
    }

    private boolean validator(final ValidatorItem validatorItem, final String value) {
        ValidatorRule validatorRule = validatorItem.value();
        switch (validatorRule) {
            case NOT_EMPTY:
            case NOT_BLANK: {
                if (validatorRule == ValidatorRule.NOT_EMPTY) {
                    if (Validator.isEmpty(value)) {
                        return false;
                    }
                } else {
                    if (StrUtil.isBlank(value)) {
                        return false;
                    }
                }
                if (value == null) {
                    return false;
                }
                int valLen = value.length();
                int[] ranges = spiltRange(validatorItem.range());
                if (ranges != null) {
                    if (ranges.length == 1) {
                        if (ranges[0] != valLen) {
                            return false;
                        }
                    } else {
                        if (valLen < ranges[0] || valLen > ranges[1]) {
                            return false;
                        }
                    }
                }
            }
            break;
            case GENERAL: {
                int[] ranges = spiltRange(validatorItem.range());
                if (ranges == null) {
                    if (!Validator.isGeneral(value)) {
                        return false;
                    }
                } else if (ranges.length == 1) {
                    if (!Validator.isGeneral(value, ranges[0])) {
                        return false;
                    }
                } else {
                    if (!Validator.isGeneral(value, ranges[0], ranges[1])) {
                        return false;
                    }
                }
            }
            break;
            case DECIMAL:
            case NUMBERS:
                if (!validatorNumber(validatorItem, value)) {
                    return false;
                }
                break;
            case POSITIVE_INTEGER:
            case NON_ZERO_INTEGERS:
                String reg = validatorRule == ValidatorRule.POSITIVE_INTEGER ? "^\\+?[0-9]*$" : "^\\+?[1-9][0-9]*$";
                if (!Validator.isMactchRegex(reg, value)) {
                    return false;
                }
                // 强制现在整数不能超过7位
                if (value.length() > 7) {
                    return false;
                }
                if (!validatorNumber(validatorItem, value)) {
                    return false;
                }
                break;
            default:
                break;
        }
        return validator2(validatorItem, value);
    }

    /**
     * 数字类型的
     *
     * @param validatorItem 规则
     * @param value         值
     * @return true 正确的
     */
    private boolean validatorNumber(final ValidatorItem validatorItem, final String value) {
        Double[] douRange = spiltRangeDouble(validatorItem.range());
        if (douRange != null && douRange[2] != null) {
            int len = douRange[2].intValue();
            // 小数
            if (!Validator.isMactchRegex("\\d+\\.\\d{" + len + "}$", value)) {
                return false;
            }
        } else if (!Validator.isNumber(value)) {
            return false;
        }
        if (douRange != null) {
            if (douRange[1] == null && douRange[0] != null) {
                // 具体某个值
                Double doubleVal = Convert.toDouble(value);
                return douRange[0].equals(doubleVal);
            } else if (douRange[1] != null && douRange[0] != null) {
                // 范围
                if (douRange[0] <= douRange[1]) {
                    Double doubleVal = Convert.toDouble(value);
                    return doubleVal <= douRange[1] && doubleVal >= douRange[0];
                }
            }
        }
        return true;
    }


    private boolean validator2(final ValidatorItem validatorItem, final String value) {
        ValidatorRule validatorRule = validatorItem.value();
        switch (validatorRule) {
            case EMAIL:
                if (!Validator.isEmail(value)) {
                    return false;
                }
                break;
            case MOBILE:
                if (!Validator.isMobile(value)) {
                    return false;
                }
                break;
            case URL:
                if (!Validator.isUrl(value)) {
                    return false;
                }
                break;
            case WORD:
                if (!Validator.isWord(value)) {
                    return false;
                }
                break;
            case CHINESE:
                if (!Validator.isChinese(value)) {
                    return false;
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 验证拦截器
     */
    public interface Interceptor {
        /**
         * 拦截到
         *
         * @param request       ree
         * @param response      res
         * @param parameterName 参数名
         * @param value         值
         * @param validatorItem 验证规则
         */
        void error(final HttpServletRequest request, final HttpServletResponse response, final String parameterName, final String value, final ValidatorItem validatorItem);

        /**
         * 获取参数
         *
         * @param request       req
         * @param parameterName 参数名
         * @return 值
         */
        String getParameter(final HttpServletRequest request, final String parameterName);
    }

    /**
     * 默认的参数拦截
     */
    public static class DefaultInterceptor implements Interceptor {
        @Override
        public void error(HttpServletRequest request, HttpServletResponse response, String parameterName, String value, ValidatorItem validatorItem) {
            JsonMessage jsonMessage = new JsonMessage(validatorItem.code(), validatorItem.msg());
            DefaultSystemLog.LOG().info("{} {} {} {} {}", request.getRequestURI(), parameterName, value, validatorItem.value(), jsonMessage);
            ServletUtil.write(response, jsonMessage.toString(), MediaType.APPLICATION_JSON_UTF8_VALUE);
        }

        @Override
        public String getParameter(HttpServletRequest request, String parameterName) {
            return null;
        }
    }
}
