package cn.jiangzeyin.common.spring;

import cn.jiangzeyin.CommonPropertiesFinal;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                container.setSessionTimeout(timOut);
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
}
