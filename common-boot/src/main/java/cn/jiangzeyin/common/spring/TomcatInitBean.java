package cn.jiangzeyin.common.spring;

import cn.jiangzeyin.CommonPropertiesFinal;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by jiangzeyin on 2017/3/31.
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
            Integer timout = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.REQUEST_TIME_OUT, Integer.class);
            if (timout != null)
                container.setSessionTimeout(timout, TimeUnit.MINUTES);//单位为分钟
        };
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            String name = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.TOMCAT_SESSION_COOKIE_NAME);
            if (name != null && name.length() > 0)
                servletContext.getSessionCookieConfig().setName(name);
        };
    }
}
