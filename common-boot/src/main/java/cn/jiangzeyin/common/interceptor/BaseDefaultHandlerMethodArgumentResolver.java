package cn.jiangzeyin.common.interceptor;

import cn.hutool.core.convert.Convert;
import cn.jiangzeyin.common.spring.SpringUtil;
import cn.jiangzeyin.common.validator.ValidatorConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 默认的controller  方法参数解析器
 * Created by jiangzeyin on 2018/8/24.
 *
 * @author jiangzeyin
 */
public abstract class BaseDefaultHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final ConfigurableApplicationContext context = (ConfigurableApplicationContext) SpringUtil.getApplicationContext();

    private final RequestParamMethodArgumentResolver requestParamMethodArgumentResolver = new RequestParamMethodArgumentResolver(context.getBeanFactory(), false);


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object object = requestParamMethodArgumentResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        if (object != null) {
            return object;
        }
        ValidatorConfig validatorConfig = parameter.getParameterAnnotation(ValidatorConfig.class);
        if (null == validatorConfig) {
            return null;
        }
        String defaultVal = validatorConfig.defaultVal();
        if (ValueConstants.DEFAULT_NONE.equals(defaultVal)) {
            return null;
        }
        return Convert.convert(parameter.getParameterType(), defaultVal);
    }
}
