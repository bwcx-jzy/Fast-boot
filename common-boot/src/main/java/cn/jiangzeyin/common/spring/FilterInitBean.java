package cn.jiangzeyin.common.spring;

import cn.jiangzeyin.common.request.XssFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by jiangzeyin on 2017/3/31.
 */
@Configuration
public class FilterInitBean {

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
