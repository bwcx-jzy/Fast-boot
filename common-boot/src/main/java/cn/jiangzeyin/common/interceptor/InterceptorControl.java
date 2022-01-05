package cn.jiangzeyin.common.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.ApplicationBuilder;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.resource.EncodedResourceResolver;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.*;

/**
 * 拦截器控制器
 *
 * @author jiangzeyin
 * @date 2017/2/4.
 */
@Configuration
@EnableWebMvc
@ConfigurationProperties(prefix = CommonPropertiesFinal.INTERCEPTOR)
public class InterceptorControl implements WebMvcConfigurer {
    //@Value("${" + CommonPropertiesFinal.INTERCEPTOR_INIT_PACKAGE_NAME + ":}")
    /**
     * @see CommonPropertiesFinal#INTERCEPTOR_INIT_PACKAGE_NAME
     */
    private String initPackageName;

    //    @Value("${" + CommonPropertiesFinal.INTERCEPTOR_RESOURCE_HANDLER_MAP + ":}")
    private Map<String, String> resourceHandlerMap;
    /**
     * 加载成功
     */
    private static final List<Class> LOAD_OK = new ArrayList<>();
    private InterceptorRegistry registry;

    public void setInitPackageName(String initPackageName) {
        this.initPackageName = initPackageName;
    }

    public void setResourceHandlerMap(Map<String, String> resourceHandlerMap) {
        this.resourceHandlerMap = resourceHandlerMap;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        this.registry = registry;
        //  加载application 注入
        Set<Class<?>> def = loadApplicationInterceptor();
        // 用户添加的
        if (StrUtil.isNotEmpty(initPackageName)) {
            String[] paths = StrUtil.splitToArray(initPackageName, StrUtil.COMMA);
            Collection<Class<?>> newClassSet = CollUtil.union(def, new ArrayList<>());
            for (String item : paths) {
                Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation(item, InterceptorPattens.class);
                // 合并
                newClassSet = CollUtil.union(newClassSet, classSet);
            }
            loadClass(newClassSet);
        } else if (def != null) {
            loadClass(def);
        }
    }

    private Set<Class<?>> loadApplicationInterceptor() {
        Set<Class<? extends BaseInterceptor>> interceptorClass = ApplicationBuilder.getActiveApplication(ApplicationBuilder::getInterceptorClass);
        if (interceptorClass == null) {
            return null;
        }
        Class<?>[] cls = interceptorClass.toArray(new Class[0]);
        return new HashSet<>(Arrays.asList(cls));
    }

    private void loadClass(Collection<Class<?>> set) {
        if (null == set) {
            return;
        }
        List<Map.Entry<Class, Integer>> newList = splitClass(set);
        if (newList != null) {
            for (Map.Entry<Class, Integer> entry : newList) {
                loadInterceptor(entry.getKey(), registry);
            }
        }
    }

    /**
     * 排序class
     *
     * @param list list
     * @return 排序后的
     */
    private static List<Map.Entry<Class, Integer>> splitClass(Collection<Class<?>> list) {
        HashMap<Class, Integer> sortMap = new HashMap<>(10);
        for (Class item : list) {
            boolean isAbstract = Modifier.isAbstract(item.getModifiers());
            if (isAbstract) {
                continue;
            }
            if (!HandlerInterceptor.class.isAssignableFrom(item)) {
                DefaultSystemLog.getLog().error("load interceptor: {} Not implements {}", item, HandlerInterceptor.class);
                continue;
            }
            InterceptorPattens interceptorPattens = (InterceptorPattens) item.getAnnotation(InterceptorPattens.class);
            sortMap.put(item, interceptorPattens.sort());
        }
        List<Map.Entry<Class, Integer>> newList = null;
        if (sortMap.size() >= 1) {
            newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Comparator.comparing(Map.Entry::getValue));
        }
        return newList;
    }


    private void loadInterceptor(Class<?> itemCls, InterceptorRegistry registry) {
//        if (LOAD_OK.contains(itemCls) && !ApplicationBuilder.isRestart()) {
//            DefaultSystemLog.getLog().warn("重复注入拦截器" + itemCls);
//            return;
//        }
        InterceptorPattens interceptorPattens = itemCls.getAnnotation(InterceptorPattens.class);
        Object handlerInterceptor = Singleton.get(itemCls);
        String[] patterns = interceptorPattens.value();
        // 注册
        InterceptorRegistration registration = registry.addInterceptor((HandlerInterceptor) handlerInterceptor);
        registration.addPathPatterns(patterns);
        // 排除
        String[] exclude = interceptorPattens.exclude();
        if (exclude.length > 0) {
            registration.excludePathPatterns(exclude);
        }
        LOAD_OK.add(itemCls);
        DefaultSystemLog.getLog().debug("load interceptor：{} {} {} {}", itemCls, Arrays.toString(patterns), Arrays.toString(exclude), interceptorPattens.sort());
    }

    /**
     * 静态资源配置
     *
     * @param registry 注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceHandler = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.INTERCEPTOR_RESOURCE_HANDLER);
        // 统一配置
        if (StrUtil.isNotBlank(resourceHandler)) {
            String[] handler = ArrayUtil.toArray(StrUtil.splitTrim(resourceHandler, StrUtil.COMMA), String.class);
            ResourceHandlerRegistration resourceHandlerRegistration = registry.addResourceHandler(handler);
            // 资源文件路径
            String resourceLocation = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.INTERCEPTOR_RESOURCE_LOCATION);
            if (StrUtil.isNotBlank(resourceLocation)) {
                String[] location = ArrayUtil.toArray(StrUtil.splitTrim(resourceLocation, StrUtil.COMMA), String.class);
                resourceHandlerRegistration.addResourceLocations(location);
            }
        }
        //
        if (resourceHandlerMap != null) {
            for (Map.Entry<?, ?> entry : resourceHandlerMap.entrySet()) {
                ResourceHandlerRegistration resourceHandlerRegistration = registry.addResourceHandler(String.valueOf(entry.getKey()));
                String value = (String) entry.getValue();
                String[] location = ArrayUtil.toArray(StrUtil.splitTrim(value, StrUtil.COMMA), String.class);
                resourceHandlerRegistration.addResourceLocations((String) ArrayUtil.get(location, 0));
                String cache = ArrayUtil.get(location, 1);
                if (StrUtil.isNotEmpty(cache)) {
                    try {
                        Duration duration = Duration.parse(cache);
                        resourceHandlerRegistration.setCacheControl(CacheControl.maxAge(duration).cachePrivate());
                    } catch (Exception ignored) {
                    }
                }
                String last = ArrayUtil.get(location, location.length - 1);
                if (StrUtil.equalsIgnoreCase(last, "gzip")) {
                    resourceHandlerRegistration
                            .resourceChain(false)
                            .addResolver(new EncodedResourceResolver());
                }
            }
        }
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Set<HttpMessageConverter<?>> httpMessageConverters = ApplicationBuilder.getActiveApplication(ApplicationBuilder::getHttpMessageConverters);
        if (httpMessageConverters != null) {
            converters.addAll(httpMessageConverters);
        }
    }

    /**
     * 参数解析器
     *
     * @param argumentResolvers arg
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        if (StrUtil.isNotEmpty(initPackageName)) {
            String[] paths = StrUtil.splitToArray(initPackageName, StrUtil.COMMA);
            Collection<Class<?>> newClassSet = null;
            for (String item : paths) {
                Set<Class<?>> classSet = ClassUtil.scanPackageBySuper(item, HandlerMethodArgumentResolver.class);
                // 合并
                newClassSet = CollUtil.union(newClassSet, classSet);
            }
            if (newClassSet != null) {
                for (Class<?> cls : newClassSet) {
                    addArgumentResolvers(argumentResolvers, cls);
                }
            }
        }
        // 加载默认注入
        boolean ext = false;
        Set<Class<? extends HandlerMethodArgumentResolver>> methodArgumentResolvers = ApplicationBuilder.getActiveApplication(ApplicationBuilder::getHandlerMethodArgumentResolvers);
        if (methodArgumentResolvers != null) {
            for (Class<? extends HandlerMethodArgumentResolver> methodArgumentResolver : methodArgumentResolvers) {
                addArgumentResolvers(argumentResolvers, methodArgumentResolver);
                if (!ext && ClassUtil.isAssignable(DefaultHandlerMethodArgumentResolver.class, methodArgumentResolver)) {
                    ext = true;
                }
            }
        }
        if (!ext) {
            addArgumentResolvers(argumentResolvers, DefaultHandlerMethodArgumentResolver.class);
        }
    }

    private void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers, Class<?> aClass) {
        if (Modifier.isAbstract(aClass.getModifiers())) {
            return;
        }
        Object methodArgumentResolver = Singleton.get(aClass);
        argumentResolvers.add((HandlerMethodArgumentResolver) methodArgumentResolver);
        DefaultSystemLog.getLog().debug("argument resolvers：" + aClass);
    }
}
