package cn.jiangzeyin.common.interceptor;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.ApplicationBuilder;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.spring.SpringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 拦截器控制器
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/4.
 */
@Configuration
@EnableWebMvc
public class InterceptorControl extends WebMvcConfigurerAdapter {
    @Value("${" + CommonPropertiesFinal.INTERCEPTOR_INIT_PACKAGE_NAME + ":}")
    private String loadPath;
    /**
     * 加载成功
     */
    private static final List<Class> LOAD_OK = new ArrayList<>();
    private InterceptorRegistry registry;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        this.registry = registry;
        init();
    }

    /**
     * 预加载包中
     */
    private void init() {
        //  加载application 注入
        loadApplicationInterceptor();
        // 用户添加的级别最低
        if (StrUtil.isNotEmpty(loadPath)) {
            Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation(loadPath, InterceptorPattens.class);
            loadClass(classSet);
        }
    }

    private void loadApplicationInterceptor() {
        Set<Class<? extends BaseInterceptor>> interceptorClass = ApplicationBuilder.getInstance().getInterceptorClass();
        if (interceptorClass == null) {
            return;
        }
        Class<?>[] cls = interceptorClass.toArray(new Class[0]);
        Set<Class<?>> newSet = new HashSet<>(Arrays.asList(cls));
        loadClass(newSet);
    }

    private void loadClass(Set<Class<?>> set) {
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
    private static List<Map.Entry<Class, Integer>> splitClass(Set<Class<?>> list) {
        HashMap<Class, Integer> sortMap = new HashMap<>(10);
        for (Class item : list) {
            boolean isAbstract = Modifier.isAbstract(item.getModifiers());
            if (isAbstract) {
                continue;
            }
            if (!BaseInterceptor.class.isAssignableFrom(item)) {
                DefaultSystemLog.LOG().info("加载拦截器异常: {} 没有继承 {}", item, BaseInterceptor.class);
                continue;
            }
            InterceptorPattens interceptorPattens = (InterceptorPattens) item.getAnnotation(InterceptorPattens.class);
            sortMap.put(item, interceptorPattens.sort());
        }
        List<Map.Entry<Class, Integer>> newList = null;
        if (sortMap.size() > 0) {
            newList = new ArrayList<>(sortMap.entrySet());
            newList.sort(Comparator.comparing(Map.Entry::getValue));
        }
        return newList;
    }


    private void loadInterceptor(Class itemCls, InterceptorRegistry registry) {
        if (LOAD_OK.contains(itemCls) && !ApplicationBuilder.isRestart()) {
            DefaultSystemLog.LOG().info("重复注入拦截器" + itemCls);
            return;
        }
        InterceptorPattens interceptorPattens = (InterceptorPattens) itemCls.getAnnotation(InterceptorPattens.class);
        BaseInterceptor handlerInterceptor;
        try {
            handlerInterceptor = (BaseInterceptor) itemCls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            DefaultSystemLog.ERROR().error("加载拦截器错误", e);
            return;
        }
        String[] patterns = interceptorPattens.value();
        // 注册
        InterceptorRegistration registration = registry.addInterceptor(handlerInterceptor);
        registration.addPathPatterns(patterns);
        // 排除
        String[] exclude = interceptorPattens.exclude();
        if (exclude.length > 0) {
            registration.excludePathPatterns(exclude);
        }
        LOAD_OK.add(itemCls);
        DefaultSystemLog.LOG().info("加载拦截器：{} {} {} {}", itemCls, Arrays.toString(patterns), Arrays.toString(exclude), interceptorPattens.sort());
    }

    /**
     * 静态资源配置
     *
     * @param registry 注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceHandler = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.INTERCEPTOR_RESOURCE_HANDLER);
        ResourceHandlerRegistration resourceHandlerRegistration;
        if (StrUtil.isNotBlank(resourceHandler)) {
            String[] handler = ArrayUtil.toArray(StrUtil.splitTrim(resourceHandler, ","), String.class);
            resourceHandlerRegistration = registry.addResourceHandler(handler);
            // 资源文件路径
            String resourceLocation = SpringUtil.getEnvironment().getProperty(CommonPropertiesFinal.INTERCEPTOR_RESOURCE_LOCATION);
            if (resourceHandlerRegistration != null && StrUtil.isNotBlank(resourceLocation)) {
                String[] location = ArrayUtil.toArray(StrUtil.splitTrim(resourceLocation, ","), String.class);
                resourceHandlerRegistration.addResourceLocations(location);
            }
        }
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        Set<HttpMessageConverter<?>> httpMessageConverters = ApplicationBuilder.getInstance().getHttpMessageConverters();
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
    @SuppressWarnings("unchecked")
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        if (StrUtil.isNotEmpty(loadPath)) {
            Set<Class<?>> classSet = ClassUtil.scanPackageBySuper(loadPath, HandlerMethodArgumentResolver.class);
            if (classSet == null) {
                return;
            }
            for (Class<?> cls : classSet) {
                Object methodArgumentResolver = Singleton.get(cls);
                argumentResolvers.add((HandlerMethodArgumentResolver) methodArgumentResolver);
                DefaultSystemLog.LOG().info("参数解析器：" + cls);
            }
        }
    }
}
