//package cn.jiangzeyin.system;
//
//import cn.jiangzeyin.common.spring.SpringUtil;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
///**
// * @author jiangzeyin
// * Created by jiangzeyin on 2017/1/5.
// */
//@Configuration
//public class SystemBean implements EnvironmentAware {
//    private volatile static SystemBean systemBean;
//
//
//    @Override
//    public void setEnvironment(Environment environment) {
//        this.environment = environment;
//    }
//
//    /**
//     * 检测请求超时记录时间
//     */
//    @Value("${request_timeout_log:3000}")
//    public Long request_timeout_log;
//
//    /**
//     * tomcat 路径
//     */
//    @Value("${server.tomcat.basedir:}")
//    private String tomcatBaseDir;
//
//
//    /**
//     * 系统配置对象
//     *
//     * @return systemBean
//     */
//    public static SystemBean getInstance() {
//        if (systemBean == null) {
//            synchronized (SystemBean.class) {
//                if (systemBean == null) {
//                    systemBean = SpringUtil.getBean(SystemBean.class);
//                }
//            }
//        }
//        return systemBean;
//    }
//
//    public Environment getEnvironment() {
//        return environment;
//    }
//
//
//    public String getTomcatBaseDir() {
//        return tomcatBaseDir;
//    }
//
//}
