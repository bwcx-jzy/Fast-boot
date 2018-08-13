package cn.jiangzeyin.common.spring;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.request.XssFilter;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * tomcat
 * Created by jiangzeyin on 2017/3/31.
 *
 * @author jiangzeyin
 */
@Configuration
public class TomcatInitBean {

    /**
     * session 超时
     *
     * @return embedded
     */
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> {
            Integer timOut = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.TOMCAT_SESSION_TIME_OUT, Integer.class);
            if (timOut != null) {
                //单位为分钟
                container.setSessionTimeout(timOut, TimeUnit.MINUTES);
            }
        };
    }

    /**
     * session cookie 名称
     *
     * @return servletContext
     */
    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            String name = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.TOMCAT_SESSION_COOKIE_NAME);
            if (name != null && name.length() > 0) {
                servletContext.getSessionCookieConfig().setName(name);
            }
        };
    }

    /**
     * 编码拦截器
     *
     * @return xss
     */
    @Bean
    public XssFilter characterEncodingFilter() {
        XssFilter characterEncodingFilter = new XssFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }
}
