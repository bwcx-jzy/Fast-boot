package cn.jiangzeyin.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.interceptor.BaseInterceptor;
import cn.jiangzeyin.common.spring.SpringUtil;
import cn.jiangzeyin.common.spring.event.ApplicationEventClient;
import cn.jiangzeyin.common.spring.event.ApplicationEventLoad;
import cn.jiangzeyin.common.validator.ParameterInterceptor;
import org.springframework.boot.Banner;
import org.springframework.boot.ImageBanner;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Boot 启动控制
 *
 * @author jiangzeyin
 * @date 2018/4/13
 */
public class ApplicationBuilder extends SpringApplicationBuilder {

    private final static List<ApplicationBuilder> APPLICATION_BUILDER = new ArrayList<>();

    static final String[] IMAGE_EXTENSION = new String[]{"gif", "jpg", "png"};
    /**
     * 程序主类
     */
    protected Class<?> applicationClass;

    protected SpringApplication application;
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
     *
     */
    private Set<Class<? extends HandlerMethodArgumentResolver>> handlerMethodArgumentResolvers;

    /**
     * 系统预置参数  ，方便多模块间调用
     */
    private static final ConcurrentHashMap<String, Object> PRESET_PARAMETER = new ConcurrentHashMap<>();

    /**
     * 添加预置参数
     *
     * @param key   参数的名称
     * @param value 值
     */
    public static void put(String key, Object value) {
        PRESET_PARAMETER.put(key, value);
    }

    /**
     * 根据参数名 获取值
     *
     * @param key 参数名
     * @return Object
     */
    public static Object get(String key) {
        return PRESET_PARAMETER.get(key);
    }

    /**
     * 创建启动对象
     *
     * @param sources sources
     * @return Builder
     * @throws NoSuchFieldException   e
     * @throws IllegalAccessException e
     */
    public static ApplicationBuilder createBuilder(Class<?>... sources) throws Exception {
        return new ApplicationBuilder(sources);
    }

    protected ApplicationBuilder(Class<?>... sources) throws Exception {
        super(sources);
        this.application = application();
        this.applicationClass = this.application.getMainApplicationClass();
        // 重写banner
        banner((environment, sourceClass, out) -> {
            // 最早获取配置信息
            ApplicationBuilder.this.environment = environment;
            String msg = environment.getProperty(CommonPropertiesFinal.BANNER_MSG, "boot Application starting");
            //带路径的可能是banner文件或banner图片
            if (msg.contains("classpath")) {
                String suffixName = msg.substring(msg.indexOf(".")+1);
                for (String s : IMAGE_EXTENSION) {
                    if(s.equals(suffixName)){
                        Banner imageBanner = getImageBanner(environment, msg);
                        if (imageBanner != null) {
                            imageBanner.printBanner(environment, sourceClass, out);
                            return;
                        }
                    }
                }

                Banner textBanner = getTextBanner(environment, msg);
                if (textBanner != null) {
                    textBanner.printBanner(environment, sourceClass, out);
                    return;
                }
            }
            out.println(msg);
        });
        EnableCommonBoot enableCommonBoot = this.applicationClass.getAnnotation(EnableCommonBoot.class);
        if (enableCommonBoot == null) {
            addLoadPage("cn.jiangzeyin");
        } else {
            if (enableCommonBoot.parameterValidator()) {
                addInterceptor(ParameterInterceptor.class);
            }
        }
        loadProperties("cn.jiangzeyin");
        //
        APPLICATION_BUILDER.add(this);
    }

    private Banner getImageBanner(Environment environment, String location) {
        Resource resource = new DefaultResourceLoader(ClassUtils.getDefaultClassLoader()).getResource(location);
        return resource.exists() ? new ImageBanner(resource) : null;

    }

    private Banner getTextBanner(Environment environment, String location) {
        Resource resource = new DefaultResourceLoader(ClassUtils.getDefaultClassLoader()).getResource(location);
        return resource.exists() ? new ResourceBanner(resource) : null;
    }

    public static Environment getEnvironment() {
        if (CollUtil.isEmpty(APPLICATION_BUILDER)) {
            return SpringUtil.getBean(Environment.class);
        }
        return getActiveApplication(applicationBuilder -> applicationBuilder.environment);
    }


    public static <R> R getActiveApplication(Function<ApplicationBuilder, R> function) {
        if (CollUtil.isEmpty(APPLICATION_BUILDER)) {
            return null;
        }
        ApplicationBuilder applicationBuilder = ApplicationBuilder.APPLICATION_BUILDER.get(ApplicationBuilder.APPLICATION_BUILDER.size() - 1);
        return function.apply(applicationBuilder);
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

    public Set<Class<? extends HandlerMethodArgumentResolver>> getHandlerMethodArgumentResolvers() {
        return handlerMethodArgumentResolvers;
    }

    public Set<ApplicationEventClient> getApplicationEventClients() {
        return applicationEventClients;
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
            this.httpMessageConverters = new LinkedHashSet<>();
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
            this.interceptorClass = new LinkedHashSet<>();
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
            this.applicationEventLoads = new LinkedHashSet<>();
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
            applicationEventClients = new LinkedHashSet<>();
        }
        this.applicationEventClients.add(applicationEventClient);
        return this;
    }


    /**
     * 添加参数解析器
     *
     * @param cls cls
     * @return this
     */
    public ApplicationBuilder addHandlerMethodArgumentResolver(Class<? extends HandlerMethodArgumentResolver> cls) {
        Objects.requireNonNull(cls);
        if (handlerMethodArgumentResolvers == null) {
            handlerMethodArgumentResolvers = new HashSet<>();
        }
        this.handlerMethodArgumentResolvers.add(cls);
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
    public ApplicationBuilder addLoadPage(String packageName) throws NoSuchFieldException, IllegalAccessException {
        if (StringUtils.isEmpty(packageName)) {
            throw new IllegalArgumentException("packageName");
        }
        Object proxy;
        String fliedName;
        ComponentScan componentScan = applicationClass.getAnnotation(ComponentScan.class);
        if (componentScan == null) {
            SpringBootApplication springBootApplication = applicationClass.getAnnotation(SpringBootApplication.class);
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
        @SuppressWarnings("unchecked")
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

    private final HashSet<Class> cacheLoadProperties = new HashSet<>();

    /**
     * 加载配置
     *
     * @param packageName 指定包名
     * @return this
     * @throws Exception e
     */
    @SuppressWarnings("unchecked")
    public ApplicationBuilder loadProperties(String packageName) throws Exception {
        Set<Class<?>> list = ClassUtil.scanPackageByAnnotation(packageName, AutoPropertiesClass.class);
        for (Class cls : list) {
            if (cacheLoadProperties.contains(cls)) {
                continue;
            }
            Method[] methods = cls.getDeclaredMethods();
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
                        super.properties(map);
                    }
                } else {
                    throw new IllegalArgumentException(cls + "  " + method + "  " + PreLoadMethod.class + " must use empty parameters static Map private");
                }
            }
            cacheLoadProperties.add(cls);
        }
        return this;
    }
}
