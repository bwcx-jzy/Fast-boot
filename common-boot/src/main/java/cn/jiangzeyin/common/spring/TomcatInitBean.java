//package cn.jiangzeyin.common.spring;
//
//import cn.jiangzeyin.CommonPropertiesFinal;
//import org.springframework.boot.web.servlet.ServletContextInitializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * tomcat
// *
// * @author jiangzeyin
// * @date 2017/3/31.
// */
//@Configuration
//public class TomcatInitBean {
//
//    private static Integer timOut;
//
//    public static Integer getTimOut() {
//        return timOut;
//    }
//
//    /**
//     * session cookie 名称
//     *
//     * @return servletContext
//     */
//    @Bean
//    public ServletContextInitializer servletContextInitializer() {
//        return servletContext -> {
//            String name = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.TOMCAT_SESSION_COOKIE_NAME);
//            if (name != null && name.length() > 0) {
//                servletContext.getSessionCookieConfig().setName(name);
//            }
//            timOut = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.TOMCAT_SESSION_TIME_OUT, Integer.class);
//            if (timOut != null) {
//                servletContext.setSessionTimeout(timOut);
//            }
//        };
//    }
//}
