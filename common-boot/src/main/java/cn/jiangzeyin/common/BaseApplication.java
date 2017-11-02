package cn.jiangzeyin.common;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.spring.ApplicationEventClient;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/10.
 */

public class BaseApplication extends SpringApplication {

    private static Environment environment;
    private static ApplicationEventClient applicationEventClient;

    public static Environment getEnvironment() {
        Assert.notNull(environment, "environment is null");
        return environment;
    }

    public BaseApplication(ApplicationEventClient applicationEventClient, Object... sources) {
        super(sources);
        // 设置加载当前包
        try {
            setLoadPage((Class) sources[0]);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // 设置banner
        this.setBanner((environment, sourceClass, out) -> {
            BaseApplication.environment = environment;
            String msg = environment.getProperty(CommonPropertiesFinal.BANNER_MSG, "boot Application starting");
            out.println(msg);
        });
        BaseApplication.applicationEventClient = applicationEventClient;
    }

    public static ApplicationEventClient getApplicationEventClient() {
        return applicationEventClient;
    }

    protected void setApplicationEventClient(ApplicationEventClient applicationEventClient) {
        BaseApplication.applicationEventClient = applicationEventClient;
    }

    /**
     * @param sources sources
     */
    public BaseApplication(Object... sources) {
        this(null, sources);
    }

    private void setLoadPage(Class tclass) throws NoSuchFieldException, IllegalAccessException {
        ComponentScan componentScan = (ComponentScan) tclass.getAnnotation(ComponentScan.class);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(componentScan);
        Field value = invocationHandler.getClass().getDeclaredField("memberValues");
        value.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
        String[] values = (String[]) memberValues.get("value");
        String[] newValues = new String[values.length + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length - 1] = "cn.jiangzeyin";
        memberValues.put("value", newValues);
    }
}