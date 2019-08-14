package cn.jiangzeyin.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.jiangzeyin.common.spring.SpringUtil;
import cn.jiangzeyin.common.validator.ValidatorConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 默认的controller  方法参数解析器
 *
 * @author jiangzeyin
 * @date 2018/8/24.
 */
public class DefaultHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    protected final ConfigurableApplicationContext context = (ConfigurableApplicationContext) SpringUtil.getApplicationContext();

    protected final RequestParamMethodArgumentResolver requestParamMethodArgumentResolver = new RequestParamMethodArgumentResolver(context.getBeanFactory(), false);


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }

    /**
     * 使用默认解析规则
     *
     * @param parameter     parameter
     * @param mavContainer  mavContainer
     * @param webRequest    webRequest
     * @param binderFactory binderFactory
     * @return object
     * @throws Exception Exception
     * @see AbstractNamedValueMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     */
    protected Object resolveDefaultArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object object = null;
        try {
            object = requestParamMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        } catch (IllegalStateException | MethodArgumentTypeMismatchException ignored) {
            // 参数解析异常忽略
        }
        if (object != null) {
            return object;
        }
        boolean basicType = ClassUtil.isBasicType(parameter.getParameterType());
        if (!basicType) {
            try {
                object = ServletUtil.toBean(BaseCallbackController.getRequestAttributes().getRequest(), parameter.getParameterType(), false);
                boolean empty = BeanUtil.isEmpty(object);
                if (empty) {
                    return null;
                }
                return object;
            } catch (Exception ignored) {
            }
        }
        return object;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object object = resolveDefaultArgument(parameter, mavContainer, webRequest, binderFactory);
        if (object != null) {
            return object;
        }
        ValidatorConfig validatorConfig = parameter.getParameterAnnotation(ValidatorConfig.class);
        if (null == validatorConfig) {
            return null;
        }
        String val = null;
        // 自定义参数
        String name = validatorConfig.name();
        if (StrUtil.isNotEmpty(name)) {
            val = webRequest.getParameter(name);
        }
        // 默认值
        if (val == null) {
            val = validatorConfig.defaultVal();
            if (ValueConstants.DEFAULT_NONE.equals(val)) {
                return null;
            }
        }
        return Convert.convert(parameter.getParameterType(), val);
    }
}
