package cn.jiangzeyin.common.request;

import cn.jiangzeyin.CommonPropertiesFinal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 编码和xss 拦截器  如果不需要请配置排除加载  或者配置属性
 *
 * @author jiangzeyin
 * @date 2018/8/21.
 * @see CommonPropertiesFinal#REQUEST_PARAMETER_XSS
 * @see CommonPropertiesFinal#REQUEST_PARAMETER_TRIM_ALL
 * @see CommonPropertiesFinal#REQUEST_LOG
 */
@Configuration
public class XssFilterBean {
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
