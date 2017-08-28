package cn.jiangzeyin.common.interceptor;

import cn.jiangzeyin.system.log.SystemLog;
import cn.jiangzeyin.util.util.PackageUtil;
import cn.jiangzeyin.util.util.ReflectUtil;
import cn.jiangzeyin.util.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.io.IOException;
import java.lang.reflect.Modifier;
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
    //com.yoke.common.interceptor
    @Value("${interceptor.package:}")
    private String loadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        init(registry);
    }

    /**
     * @param registry 注册
     */
    private void init(InterceptorRegistry registry) {
        if (StringUtil.isEmpty(loadPath))
            return;

        List<String> list;
        try {
            list = PackageUtil.getClassName(loadPath);
        } catch (IOException e) {
            SystemLog.ERROR().error("加载拦截器异常", e);
            return;
        }
        if (list == null)
            return;
        for (String item : list) {
            Class classItem = null;
            try {
                classItem = Class.forName(item);
            } catch (ClassNotFoundException e) {
                SystemLog.ERROR().error("加载拦截器错误", e);
            }
            if (classItem == null)
                continue;
            boolean isAbstract = Modifier.isAbstract(classItem.getModifiers());
            if (isAbstract)
                continue;
            if (!ReflectUtil.isSuperclass(classItem, HandlerInterceptorAdapter.class))
                continue;
            HandlerInterceptor handlerInterceptor;
            try {
                handlerInterceptor = (HandlerInterceptor) classItem.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                SystemLog.ERROR().error("加载拦截器错误", e);
                continue;
            }
            InterceptorPattens interceptorPattens = (InterceptorPattens) classItem.getAnnotation(InterceptorPattens.class);
            if (interceptorPattens == null)
                continue;
            String[] patterns = interceptorPattens.value();
            registry.addInterceptor(handlerInterceptor).addPathPatterns(patterns);
            SystemLog.LOG().info("加载拦截器：" + classItem + "  " + patterns[0]);
        }
    }
}
