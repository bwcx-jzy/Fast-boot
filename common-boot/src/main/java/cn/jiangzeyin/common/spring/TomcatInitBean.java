package cn.jiangzeyin.common.spring;

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
            container.setSessionTimeout(1800, TimeUnit.MINUTES);//单位为S
        };
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            servletContext.getSessionCookieConfig().setName("_JSESSIONID");
        };
    }
}
