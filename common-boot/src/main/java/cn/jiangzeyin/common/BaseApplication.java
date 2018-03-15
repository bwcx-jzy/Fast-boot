package cn.jiangzeyin.common;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.spring.ApplicationEventClient;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 程序启动辅助类
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/10.
 */
public abstract class BaseApplication extends SpringApplication {

    private static Environment environment;
    private static ApplicationEventClient applicationEventClient;

    public static Environment getEnvironment() {
        Assert.notNull(environment, "environment is null");
        return environment;
    }

    /**
     * 监听程序启动状态
     *
     * @param applicationEventClient 监听接口
     * @param sources                source
     */
    public BaseApplication(ApplicationEventClient applicationEventClient, Object... sources) {
        super(sources);
        if (sources == null || sources.length <= 0)
            throw new IllegalArgumentException("please set sources");
        Object object = sources[0];
        if (!(object instanceof Class)) {
            throw new IllegalArgumentException("sources index 0  must with class");
        }
        // 设置加载当前包
        try {
            addLoadPage((Class) object, "cn.jiangzeyin");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // 设置banner
        this.setBanner((environment, sourceClass, out) -> {
            BaseApplication.environment = environment;
            String msg = environment.getProperty(CommonPropertiesFinal.BANNER_MSG, "boot Application starting");
            out.println(msg);
        });
        setApplicationEventClient(applicationEventClient);
    }

    /**
     * 默认启动
     *
     * @param sources sources
     */
    public BaseApplication(Object... sources) {
        this(null, sources);
    }


    public static ApplicationEventClient getApplicationEventClient() {
        return applicationEventClient;
    }

    /**
     * 设置程序启动监听
     *
     * @param applicationEventClient applicationEventClient
     */
    protected void setApplicationEventClient(ApplicationEventClient applicationEventClient) {
        BaseApplication.applicationEventClient = applicationEventClient;
    }


    /**
     * 给程序添加默认包
     *
     * @param tclass      注解类
     * @param packageName 包名
     * @throws NoSuchFieldException   e
     * @throws IllegalAccessException e
     */
    protected void addLoadPage(Class tclass, String packageName) throws NoSuchFieldException, IllegalAccessException {
        if (StringUtils.isEmpty(packageName))
            throw new IllegalArgumentException("packageName");
        ComponentScan componentScan = (ComponentScan) tclass.getAnnotation(ComponentScan.class);
        if (componentScan == null)
            throw new RuntimeException("please add ComponentScan");
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(componentScan);
        Field value = invocationHandler.getClass().getDeclaredField("memberValues");
        value.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
        String[] values = (String[]) memberValues.get("value");
        String[] newValues = new String[]{packageName};
        newValues = StringUtils.mergeStringArrays(values, newValues);
        memberValues.put("value", newValues);
    }
}