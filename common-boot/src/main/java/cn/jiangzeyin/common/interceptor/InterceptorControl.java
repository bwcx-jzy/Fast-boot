package cn.jiangzeyin.common.interceptor;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.util.PackageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

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
    private boolean isHash = false;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        init(registry);
    }

    /**
     * @param registry 注册
     */
    private void init(InterceptorRegistry registry) {
        if (loadPath == null || loadPath.length() <= 0) {
            loadDefault(registry);
            return;
        }
        List<String> list;
        try {
            list = PackageUtil.getClassName(loadPath);
        } catch (IOException e) {
            DefaultSystemLog.ERROR().error("加载拦截器异常", e);
            loadDefault(registry);
            return;
        }
        if (list == null) {
            loadDefault(registry);
            return;
        }
        for (String item : list) {
            Class classItem;
            try {
                classItem = Class.forName(item);
            } catch (ClassNotFoundException e) {
                DefaultSystemLog.ERROR().error("加载拦截器错误", e);
                continue;
            }
            if (classItem == null)
                continue;
            boolean isAbstract = Modifier.isAbstract(classItem.getModifiers());
            if (isAbstract)
                continue;
            if (!BaseInterceptor.class.isAssignableFrom(classItem))
                continue;
            loadInterceptor(classItem, registry);
        }
        loadDefault(registry);
    }

    private void loadDefault(InterceptorRegistry registry) {
        if (isHash)
            return;
        DefaultSystemLog.LOG().info("加载默认拦截器");
        loadInterceptor(DefaultInterceptor.class, registry);
    }

    private void loadInterceptor(Class itemCls, InterceptorRegistry registry) {
        InterceptorPattens interceptorPattens = (InterceptorPattens) itemCls.getAnnotation(InterceptorPattens.class);
        if (interceptorPattens == null)
            return;
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
        if (exclude.length > 0)
            registration.excludePathPatterns(exclude);
        DefaultSystemLog.LOG().info("加载拦截器：" + itemCls + "  " + Arrays.toString(patterns) + "  " + Arrays.toString(exclude));
        isHash = true;
    }
}
