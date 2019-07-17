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
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
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
 * 配置方法
 *
 * @author jiangzeyin
 * @date 2018/8/21.
 * @see cn.jiangzeyin.common.ApplicationBuilder#addInterceptor(java.lang.Class)
 * @see EnableCommonBoot#parameterValidator()
 */
@InterceptorPattens(sort = -100)
public class ParameterInterceptor extends BaseInterceptor {
    /**
     * int 类型的数字输入最大长度  防止数据库字段溢出
     */
    public static int INT_MAX_LENGTH = 7;

    private static volatile Interceptor interceptor = new DefaultInterceptor();

    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 设置参数拦截器 拦截响应接口
     *
     * @param interceptor 接口
     */
    public static void setInterceptor(Interceptor interceptor) {
        ParameterInterceptor.interceptor = interceptor;
    }

    /**
     * 获取值
     *
     * @param validatorConfig 验证规则
     * @param request         req
     * @param name            name
     * @param item            item
     * @return val
     */
    private String getValue(ValidatorConfig validatorConfig, HttpServletRequest request, String name, MethodParameter item) {
        // 获取值
        String value;
        // 指定name
        String configName = null;
        if (validatorConfig != null) {
            configName = validatorConfig.name();
        }
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
        if (validatorConfig != null && !ValueConstants.DEFAULT_NONE.equals(validatorConfig.defaultVal())) {
            if (value == null && !validatorConfig.strEmpty()) {
                value = validatorConfig.defaultVal();
            }
            if (StrUtil.isEmpty(value) && validatorConfig.strEmpty()) {
                value = validatorConfig.defaultVal();
            }
        }
        if (value == null && null != requestParam && !ValueConstants.DEFAULT_NONE.equals(requestParam.defaultValue())) {
            //   默认值
            value = requestParam.defaultValue();
        }
        return value;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.preHandle(request, response, handler);
        // 没有实现拦截器响应
        if (interceptor == null) {
            return true;
        }
        HandlerMethod handlerMethod = getHandlerMethod();
        if (handlerMethod == null) {
            return true;
        }
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        if (methodParameters == null) {
            return true;
        }
        for (MethodParameter item : methodParameters) {
            ValidatorItem[] validatorItems;
            ValidatorConfig validatorConfig = item.getParameterAnnotation(ValidatorConfig.class);
            if (validatorConfig == null) {
                ValidatorItem validatorItem = item.getParameterAnnotation(ValidatorItem.class);
                if (validatorItem == null) {
                    continue;
                } else {
                    validatorItems = new ValidatorItem[]{validatorItem};
                }
            } else {
                validatorItems = validatorConfig.value();
            }
            String name = item.getParameterName();
            if (name == null) {
                item.initParameterNameDiscovery(PARAMETER_NAME_DISCOVERER);
                name = item.getParameterName();
                if (name == null) {
                    continue;
                }
            }
            String value = getValue(validatorConfig, request, name, item);
            // 验证每一项
            int errorCount = 0;
            for (int i = 0, len = validatorItems.length; i < len; i++) {
                ValidatorItem validatorItem = validatorItems[i];
                if (validatorItem.unescape()) {
                    value = HtmlUtil.unescape(value);
                }
                if (validatorConfig != null && validatorItem.value() == ValidatorRule.CUSTOMIZE) {
                    if (!customize(handlerMethod, item, validatorConfig, validatorItem, name, value, request, response)) {
                        return false;
                    }
                    // 自定义条件只识别一次
                    break;
                }
                boolean error = validator(validatorItem, value);
                if (validatorConfig == null) {
                    //错误
                    interceptor.error(request, response, name, value, validatorItem);
                    return false;
                } else {
                    if (validatorConfig.errorCondition() == ErrorCondition.AND) {
                        if (!error) {
                            //错误
                            interceptor.error(request, response, name, value, validatorItem);
                            return false;
                        }
                    }
                    if (validatorConfig.errorCondition() == ErrorCondition.OR) {
                        if (error) {
                            break;
                        } else {
                            errorCount++;
                            if (i < len - 1) {
                                continue;
                            }
                            // 最后一项
                            if (i == len - 1 && errorCount == len) {
                                //错误
                                interceptor.error(request, response, name, value, validatorItem);
                                return false;
                            }
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
     * @param methodParameter 参数对象
     * @param name            参数名
     * @param value           值
     * @return true 通过效验
     * @throws InvocationTargetException 反射异常
     * @throws IllegalAccessException    反射异常
     */
    private boolean customize(HandlerMethod handlerMethod, MethodParameter methodParameter, ValidatorConfig validatorConfig, ValidatorItem validatorItem, String name, String value,
                              HttpServletRequest request, HttpServletResponse response
    ) throws InvocationTargetException, IllegalAccessException {
        // 自定义验证
        Method method;
        try {
            method = ReflectUtil.getMethod(handlerMethod.getBeanType(), validatorConfig.customizeMethod(), MethodParameter.class, String.class);
        } catch (SecurityException s) {
            // 没有权限访问 直接拦截
            DefaultSystemLog.ERROR().error(s.getMessage(), s);
            interceptor.error(request, response, name, value, validatorItem);
            return false;
        }
        if (method == null) {
            // 没有配置对应方法
            DefaultSystemLog.ERROR().error(handlerMethod.getBeanType() + "未配置验证方法：" + validatorConfig.customizeMethod());
            interceptor.error(request, response, name, value, validatorItem);
            return false;
        }
        Object obj = method.invoke(handlerMethod.getBean(), methodParameter, value);
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

    /**
     * 拆分验证范围
     *
     * @param range 范围字符串
     * @return 数组
     */
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

    private boolean validator(final ValidatorItem validatorItem, String value) {
        ValidatorRule validatorRule = validatorItem.value();
        switch (validatorRule) {
            case EMPTY:
                if (Validator.isNotEmpty(value)) {
                    return false;
                }
                break;
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
                if (value.length() > INT_MAX_LENGTH) {
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
    private boolean validatorNumber(final ValidatorItem validatorItem, String value) {
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

    /**
     * 普通的验证规则
     *
     * @param validatorItem 规则item
     * @param value         值
     * @return true通过
     */
    private boolean validator2(final ValidatorItem validatorItem, String value) {
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
            DefaultSystemLog.LogCallback logCallback = DefaultSystemLog.getLogCallback();
            if (logCallback == null) {
                DefaultSystemLog.LOG(DefaultSystemLog.LogType.REQUEST).info("{} {} {} {} {}", request.getRequestURI(), parameterName, value, validatorItem.value(), jsonMessage);
            } else {
                logCallback.log(DefaultSystemLog.LogType.REQUEST, request.getRequestURI(), parameterName, value, validatorItem.value(), jsonMessage);
            }
            ServletUtil.write(response, jsonMessage.toString(), MediaType.APPLICATION_JSON_UTF8_VALUE);
        }

        @Override
        public String getParameter(HttpServletRequest request, String parameterName) {
            return null;
        }
    }
}
