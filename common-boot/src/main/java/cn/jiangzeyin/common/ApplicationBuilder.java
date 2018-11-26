package cn.jiangzeyin.common;

import cn.hutool.core.util.ClassUtil;
import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.interceptor.BaseInterceptor;
import cn.jiangzeyin.common.spring.event.ApplicationEventClient;
import cn.jiangzeyin.common.spring.event.ApplicationEventLoad;
import cn.jiangzeyin.common.validator.ParameterInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Boot 启动控制
 *
 * @author jiangzeyin
 * data 2018/4/13
 */
public class ApplicationBuilder extends SpringApplicationBuilder {
    /**
     * 程序全局控制对象
     */
    private volatile static ApplicationBuilder applicationBuilder;
    /**
     * 程序主类
     */
    private Class applicationClass;

    private SpringApplication application;
    /**
     * 程序配置变量
     */
    private Environment environment;
    /**
     * 程序监听事件
     */
    private Set<ApplicationEventClient> applicationEventClients;

    /**
     * 程序加载完成
     */
    private Set<ApplicationEventLoad> applicationEventLoads;
    /**
     *
     */
    private Set<Class<? extends BaseInterceptor>> interceptorClass;
    /**
     *
     */
    private Set<HttpMessageConverter<?>> httpMessageConverters;

    /**
     * 创建启动对象
     *
     * @param sources sources
     * @return Builder
     * @throws NoSuchFieldException   e
     * @throws IllegalAccessException e
     */
    public static ApplicationBuilder createBuilder(Object... sources) throws Exception {
        return new ApplicationBuilder(sources);
    }

    protected ApplicationBuilder(Object... sources) throws Exception {
        super(sources);
        this.application = application();
        if (applicationBuilder != null) {
            if (!isRestart()) {
                throw new IllegalArgumentException("duplicate create");
            }
        }
        this.applicationClass = this.application.getMainApplicationClass();
        // 重写banner
        banner((environment, sourceClass, out) -> {
            // 最早获取配置信息
            ApplicationBuilder.this.environment = environment;
            String msg = environment.getProperty(CommonPropertiesFinal.BANNER_MSG, "boot Application starting");
            out.println(msg);
        });
        EnableCommonBoot enableCommonBoot = (EnableCommonBoot) this.applicationClass.getAnnotation(EnableCommonBoot.class);
        if (enableCommonBoot == null) {
            addLoadPage("cn.jiangzeyin");
        } else {
            if (enableCommonBoot.parameterValidator()) {
                addInterceptor(ParameterInterceptor.class);
            }
        }
        //loadProperties();
        ApplicationBuilder.applicationBuilder = this;
    }

    public static ApplicationBuilder getInstance() {
        if (applicationBuilder == null) {
            throw new RuntimeException("Application not start");
        }
        return applicationBuilder;
    }

    public Environment getEnvironment() {
        if (environment == null) {
            throw new RuntimeException("Application not start");
        }
        return environment;
    }

    public Set<ApplicationEventClient> getApplicationEventClients() {
        return applicationEventClients;
    }

    public Set<ApplicationEventLoad> getApplicationEventLoads() {
        return applicationEventLoads;
    }

    public Set<Class<? extends BaseInterceptor>> getInterceptorClass() {
        return interceptorClass;
    }

    public Set<HttpMessageConverter<?>> getHttpMessageConverters() {
        return httpMessageConverters;
    }

    /**
     * 添加响应转换器
     *
     * @param httpMessageConverter converter
     * @return this
     */
    public ApplicationBuilder addHttpMessageConverter(HttpMessageConverter<?> httpMessageConverter) {
        Objects.requireNonNull(httpMessageConverter);
        if (httpMessageConverters == null) {
            this.httpMessageConverters = new HashSet<>();
        }
        this.httpMessageConverters.add(httpMessageConverter);
        return this;
    }

    /**
     * 添加默认拦截器
     *
     * @param cls cls
     * @return this
     */
    public ApplicationBuilder addInterceptor(Class<? extends BaseInterceptor> cls) {
        Objects.requireNonNull(cls);
        if (interceptorClass == null) {
            this.interceptorClass = new HashSet<>();
        }
        this.interceptorClass.add(cls);
        return this;
    }

    /**
     * 添加容器启动监听
     *
     * @param applicationEventLoad 监听接口
     * @return this
     */
    public ApplicationBuilder addApplicationEventLoad(ApplicationEventLoad applicationEventLoad) {
        Objects.requireNonNull(applicationEventLoad);
        if (applicationEventLoads == null) {
            this.applicationEventLoads = new HashSet<>();
        }
        this.applicationEventLoads.add(applicationEventLoad);
        return this;
    }

    /**
     * 添加程序事件监听
     *
     * @param applicationEventClient 监听接口回调
     * @return this
     */
    public ApplicationBuilder addApplicationEventClient(ApplicationEventClient applicationEventClient) {
        Objects.requireNonNull(applicationEventClient);
        if (applicationEventClients == null) {
            applicationEventClients = new HashSet<>();
        }
        applicationEventClients.add(applicationEventClient);
        return this;
    }

    /**
     * 给程序添加默认包
     *
     * @param packageName 包名
     * @return this
     * @throws NoSuchFieldException   e
     * @throws IllegalAccessException e
     */
    @SuppressWarnings("unchecked")
    public ApplicationBuilder addLoadPage(String packageName) throws NoSuchFieldException, IllegalAccessException {
        if (StringUtils.isEmpty(packageName)) {
            throw new IllegalArgumentException("packageName");
        }
        Object proxy;
        String fliedName;
        ComponentScan componentScan = (ComponentScan) applicationClass.getAnnotation(ComponentScan.class);
        if (componentScan == null) {
            SpringBootApplication springBootApplication = (SpringBootApplication) applicationClass.getAnnotation(SpringBootApplication.class);
            if (springBootApplication == null) {
                throw new IllegalArgumentException("please add " + SpringBootApplication.class);
            } else {
                proxy = springBootApplication;
                fliedName = "scanBasePackages";
            }
        } else {
            proxy = componentScan;
            fliedName = "value";
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(proxy);
        Field value = invocationHandler.getClass().getDeclaredField("memberValues");
        value.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
        String[] values = (String[]) memberValues.get(fliedName);
        String[] newValues = new String[]{packageName};
        newValues = StringUtils.mergeStringArrays(values, newValues);
        memberValues.put(fliedName, newValues);
        return this;
    }

    /**
     * 判断是否已热部署
     *
     * @return true 热部署
     */
    public static boolean isRestart() {
        String name = Thread.currentThread().getName();
        return "restartedMain".equalsIgnoreCase(name);
    }

    /**
     * 加载配置
     *
     * @param packageName 指定包名
     * @throws Exception e
     */
    @SuppressWarnings("unchecked")
    public void loadProperties(String packageName) throws Exception {
        Set<Class<?>> list = ClassUtil.scanPackageByAnnotation(packageName, AutoPropertiesClass.class);
        for (Class cls : list) {
            Method[] methods = cls.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {
                    AutoPropertiesMethod autoPropertiesMethod = method.getAnnotation(AutoPropertiesMethod.class);
                    if (autoPropertiesMethod == null) {
                        continue;
                    }
                    method.setAccessible(true);
                    ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
                    Type type = parameterizedType.getRawType();
                    Class retCls = (Class) type;
                    int modifiers = method.getModifiers();
                    Type[] parameters = method.getParameterTypes();
                    if (parameters.length <= 0 && Map.class == retCls && Modifier.isStatic(modifiers) && Modifier.isPrivate(modifiers)) {
                        Map<String, Object> map = (Map<String, Object>) method.invoke(null);
                        if (map != null) {
                            properties(map);
                        }
                    } else {
                        throw new IllegalArgumentException(cls + "  " + method + "  " + PreLoadMethod.class + " must use empty parameters static Map private");
                    }
                }
            }
        }
    }
}
