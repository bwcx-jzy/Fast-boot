package cn.jiangzeyin.common.spring;

import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.CommonInitPackage;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.SpringApplicationBuilder;
import cn.jiangzeyin.common.spring.event.ApplicationEventClient;
import cn.jiangzeyin.common.spring.event.ApplicationEventLoad;
import cn.jiangzeyin.pool.ThreadPoolService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import java.util.List;
import java.util.Objects;

/**
 * 通用的Spring Context util
 *
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/5.
 */
@Configuration
public class SpringUtil implements ApplicationListener, ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 容器加载完成
     *
     * @param applicationContext application
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtil.applicationContext = applicationContext;
        DefaultSystemLog.init();
        List<ApplicationEventLoad> applicationEventLoads = SpringApplicationBuilder.getInstance().getApplicationEventLoads();
        if (applicationEventLoads != null) {
            for (ApplicationEventLoad applicationEventLoad : applicationEventLoads)
                applicationEventLoad.applicationLoad();
        }
    }

    /**
     * 启动完成
     *
     * @param event event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationFailedEvent) {
            System.err.println("启动失败");
            ApplicationFailedEvent applicationFailedEvent = (ApplicationFailedEvent) event;
            applicationFailedEvent.getException().printStackTrace();
            return;
        }
        List<ApplicationEventClient> applicationEventClients = SpringApplicationBuilder.getInstance().getApplicationEventClients();
        if (applicationEventClients != null) {
            for (ApplicationEventClient applicationEventClient : applicationEventClients)
                applicationEventClient.onApplicationEvent(event);
        }
        if (event instanceof ApplicationReadyEvent) {// 启动最后的预加载
            CommonInitPackage.init();
            DefaultSystemLog.LOG().info("common-boot 启动完成");
            return;
        }
        if (event instanceof ContextClosedEvent) { // 应用关闭
            DefaultSystemLog.LOG().info("common-boot 关闭程序");
            ThreadPoolService.shutdown();
            return;
        }
        if (event instanceof ServletRequestHandledEvent) {
            ServletRequestHandledEvent servletRequestHandledEvent = (ServletRequestHandledEvent) event;
            if (!servletRequestHandledEvent.wasFailure()) {
                DefaultSystemLog.LOG(DefaultSystemLog.LogType.REQUEST).info(servletRequestHandledEvent.toString());
            } else {
                DefaultSystemLog.LOG(DefaultSystemLog.LogType.REQUEST).info("error:" + servletRequestHandledEvent.toString());
            }
        }
    }

    /**
     * 获取applicationContext
     *
     * @return application
     */
    public static ApplicationContext getApplicationContext() {
        Assert.notNull(applicationContext, "application is null");
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name 名称
     * @return 对象
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz class
     * @param <T>   对象
     * @return 对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  名称
     * @param clazz class
     * @param <T>   对象
     * @return 对象
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 获取配置文件信息
     *
     * @return environment
     */
    public static Environment getEnvironment() {
        return SpringApplicationBuilder.getInstance().getEnvironment();
    }

    /**
     * 获取程序id
     *
     * @return id
     */
    public static String getApplicationId() {
        return getEnvironment().getProperty(CommonPropertiesFinal.APPLICATION_ID);
    }

    /**
     * 动态注入class
     *
     * @param tClass class
     * @param <T>    t
     * @return obj
     */
    @SuppressWarnings("unchecked")
    public static <T> T registerSingleton(Class<T> tClass) {
        Objects.requireNonNull(tClass);
        AutowireCapableBeanFactory autowireCapableBeanFactory = getApplicationContext().getAutowireCapableBeanFactory();
        T obj = autowireCapableBeanFactory.createBean(tClass);
        String beanName = StringUtil.captureName(tClass.getSimpleName());
        AnnotationConfigApplicationContext applicationContext = (AnnotationConfigApplicationContext) getApplicationContext().getParentBeanFactory();
        applicationContext.getBeanFactory().registerSingleton(beanName, obj);
        return obj;
    }
}

