package cn.jiangzeyin.common;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.spring.ApplicationEventClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Boot 启动控制
 * Created by jiangzeyin on 2018/4/13.
 */
public class SpringApplicationBuilder extends org.springframework.boot.builder.SpringApplicationBuilder {
    /**
     * 程序全局控制对象
     */
    private volatile static SpringApplicationBuilder applicationBuilder;
    /**
     * 程序主类
     */
    private Class applicationClass;
    /**
     * 程序配置变量
     */
    private Environment environment;
    /**
     * 程序监听事件
     */
    private List<ApplicationEventClient> applicationEventClients;

    /**
     * 创建启动对象
     *
     * @param sources sources
     * @return Builder
     * @throws NoSuchFieldException   e
     * @throws IllegalAccessException e
     */
    public static SpringApplicationBuilder createBuilder(Object... sources) throws NoSuchFieldException, IllegalAccessException {
        return new SpringApplicationBuilder(sources);
    }

    protected SpringApplicationBuilder(Object... sources) throws NoSuchFieldException, IllegalAccessException {
        super(sources);
        if (applicationBuilder != null)
            throw new IllegalArgumentException("duplicate create");
        if (sources == null || sources.length <= 0)
            throw new IllegalArgumentException("please set sources");
        Object object = sources[0];
        if (!(object instanceof Class)) {
            throw new IllegalArgumentException("sources index 0  must with class");
        }
        this.applicationClass = (Class) object;
        banner((environment, sourceClass, out) -> {
            SpringApplicationBuilder.this.environment = environment;
            String msg = environment.getProperty(CommonPropertiesFinal.BANNER_MSG, "boot Application starting");
            out.println(msg);
        });
        addLoadPage("cn.jiangzeyin");
        SpringApplicationBuilder.applicationBuilder = this;
    }

    public static SpringApplicationBuilder getInstance() {
        if (applicationBuilder == null)
            throw new RuntimeException("Application not start");
        return applicationBuilder;
    }

    public Environment getEnvironment() {
        if (environment == null)
            throw new RuntimeException("Application not start");
        return environment;
    }

    public List<ApplicationEventClient> getApplicationEventClients() {
        return applicationEventClients;
    }

    /**
     * 添加程序事件监听
     *
     * @param applicationEventClient 监听接口回调
     * @return this
     */
    public SpringApplicationBuilder addApplicationEventClient(ApplicationEventClient applicationEventClient) {
        Objects.requireNonNull(applicationEventClient);
        if (applicationEventClients == null)
            applicationEventClients = new ArrayList<>();
        applicationEventClients.add(applicationEventClient);
        return this;
    }

    /**
     * 给程序添加默认包
     *
     * @param packageName 包名
     * @throws NoSuchFieldException   e
     * @throws IllegalAccessException e
     */
    public void addLoadPage(String packageName) throws NoSuchFieldException, IllegalAccessException {
        if (StringUtils.isEmpty(packageName))
            throw new IllegalArgumentException("packageName");
        ComponentScan componentScan = (ComponentScan) applicationClass.getAnnotation(ComponentScan.class);
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
