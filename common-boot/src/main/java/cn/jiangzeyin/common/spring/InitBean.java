package cn.jiangzeyin.common.spring;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/10.
 */
@Configuration
public class InitBean {


    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //factory.setLocation("file:/ztoutiao/tempsss");
        factory.setMaxFileSize("500MB");
        factory.setMaxRequestSize("1000MB");
        return factory.createMultipartConfig();
    }
}
