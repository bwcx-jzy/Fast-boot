package cn.jiangzeyin.common.interceptor;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.util.PackageUtil;
import cn.jiangzeyin.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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
    @Value("${" + CommonPropertiesFinal.INTERCEPTOR_INIT_PACKAGE_NAME + ":}")
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
            DefaultSystemLog.ERROR().error("加载拦截器异常", e);
            return;
        }
        if (list == null)
            return;
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
            InterceptorPattens interceptorPattens = (InterceptorPattens) classItem.getAnnotation(InterceptorPattens.class);
            if (interceptorPattens == null)
                continue;
            BaseInterceptor handlerInterceptor;
            try {
                handlerInterceptor = (BaseInterceptor) classItem.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                DefaultSystemLog.ERROR().error("加载拦截器错误", e);
                continue;
            }
            String[] patterns = interceptorPattens.value();
            registry.addInterceptor(handlerInterceptor).addPathPatterns(patterns);
            DefaultSystemLog.LOG().info("加载拦截器：" + classItem + "  " + patterns[0]);
        }
    }
}
