//package cn.jiangzeyin.common;
//
//import cn.jiangzeyin.CommonPropertiesFinal;
//import cn.jiangzeyin.common.spring.ApplicationEventClient;
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.core.env.Environment;
//import org.springframework.util.Assert;
//import org.springframework.util.StringUtils;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Proxy;
//import java.util.Map;
//
///**
// * 程序启动辅助类
// *
// * @author jiangzeyin
// * Created by jiangzeyin on 2017/1/10.
// */
//public abstract class BaseApplication extends SpringApplication {
//
//
//
//    public static Environment getEnvironment() {
//        Assert.notNull(environment, "environment is null");
//        return environment;
//    }
//
//    /**
//     * 监听程序启动状态
//     *
//     * @param applicationEventClient 监听接口
//     * @param sources                source
//     */
//    public BaseApplication(ApplicationEventClient applicationEventClient, Object... sources) {
//
//        // 设置加载当前包
//        try {
//            addLoadPage((Class) object, "cn.jiangzeyin");
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//        // 设置banner
//        this.setBanner((environment, sourceClass, out) -> {
//
//        });
//        setApplicationEventClient(applicationEventClient);
//    }
//
//    /**
//     * 默认启动
//     *
//     * @param sources sources
//     */
//    public BaseApplication(Object... sources) {
//        this(null, sources);
//    }
//
//
//    public static ApplicationEventClient getApplicationEventClient() {
//        return applicationEventClient;
//    }
//
//    /**
//     * 设置程序启动监听
//     *
//     * @param applicationEventClient applicationEventClient
//     */
//    protected void setApplicationEventClient(ApplicationEventClient applicationEventClient) {
//        BaseApplication.applicationEventClient = applicationEventClient;
//    }
//
//
//
//}