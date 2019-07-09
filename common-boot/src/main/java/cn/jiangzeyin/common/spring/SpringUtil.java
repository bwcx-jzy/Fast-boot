package cn.jiangzeyin.common.spring;

import cn.hutool.core.util.StrUtil;
import cn.jiangzeyin.CommonPropertiesFinal;
import cn.jiangzeyin.common.ApplicationBuilder;
import cn.jiangzeyin.common.CommonInitPackage;
import cn.jiangzeyin.common.DefaultSystemLog;
import cn.jiangzeyin.common.spring.event.ApplicationEventClient;
import cn.jiangzeyin.common.spring.event.ApplicationEventLoad;
import cn.jiangzeyin.pool.ThreadPoolService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.web.context.support.ServletRequestHandledEvent;

import java.util.Objects;
import java.util.Set;

/**
 * 通用的Spring Context util
 *
 * @author jiangzeyin
 * @date 2017/1/5.
 */
@Configuration
public class SpringUtil implements ApplicationListener, ApplicationContextAware {

    private volatile static ApplicationContext applicationContext;

    private synchronized static void setApplicationContexts(ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * 容器加载完成
     *
     * @param applicationContext application
     * @throws BeansException 异常
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        SpringUtil.applicationContext = applicationContext;
        setApplicationContexts(applicationContext);
        DefaultSystemLog.init();
        Set<ApplicationEventLoad> applicationEventLoads = ApplicationBuilder.getInstance().getApplicationEventLoads();
        if (applicationEventLoads != null) {
            for (ApplicationEventLoad applicationEventLoad : applicationEventLoads) {
                applicationEventLoad.applicationLoad();
            }
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
            System.err.println("Common-Boot 启动失败");
            ApplicationFailedEvent applicationFailedEvent = (ApplicationFailedEvent) event;
            applicationFailedEvent.getException().printStackTrace();
            return;
        }
        //  通知子级
        Set<ApplicationEventClient> applicationEventClients = ApplicationBuilder.getInstance().getApplicationEventClients();
        if (applicationEventClients != null) {
            for (ApplicationEventClient applicationEventClient : applicationEventClients) {
                applicationEventClient.onApplicationEvent(event);
            }
        }
        // 启动最后的预加载
        if (event instanceof ApplicationReadyEvent) {
            CommonInitPackage.init();
            DefaultSystemLog.LOG().info("common-boot 启动完成");
            return;
        }
        // 应用关闭
        if (event instanceof ContextClosedEvent) {
            DefaultSystemLog.LOG().info("common-boot 关闭程序");
            ThreadPoolService.shutdown();
            return;
        }
        // 请求异常记录
        if (event instanceof ServletRequestHandledEvent) {
            ServletRequestHandledEvent servletRequestHandledEvent = (ServletRequestHandledEvent) event;
            if (servletRequestHandledEvent.wasFailure()) {
                DefaultSystemLog.LogCallback logCallback = DefaultSystemLog.getLogCallback();
                if (logCallback != null) {
                    logCallback.log(DefaultSystemLog.LogType.REQUEST_ERROR, "servletRequestHandledEvent", servletRequestHandledEvent);
                } else {
                    DefaultSystemLog.LOG(DefaultSystemLog.LogType.REQUEST).info("error:" + servletRequestHandledEvent.toString());
                }
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
        return ApplicationBuilder.getInstance().getEnvironment();
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
    public static <T> T registerSingleton(Class<T> tClass) {
        Objects.requireNonNull(tClass);
        // 创建bean
        AutowireCapableBeanFactory autowireCapableBeanFactory = getApplicationContext().getAutowireCapableBeanFactory();
        T obj = autowireCapableBeanFactory.createBean(tClass);
        String beanName = StrUtil.upperFirst(tClass.getSimpleName());
        registerSingleton(beanName, obj);
        return obj;
    }

    /**
     * 动态注入bean
     *
     * @param beanName beanName
     * @param object   值
     * @return 当前数量
     */
    public static int registerSingleton(String beanName, Object object) {
        // 注册
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) getApplicationContext();
        ConfigurableListableBeanFactory configurableListableBeanFactory = configurableApplicationContext.getBeanFactory();
        configurableListableBeanFactory.registerSingleton(beanName, object);
        return configurableListableBeanFactory.getSingletonCount();
    }

    public static void register(Class<?> tClass) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) getApplicationContext();
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(tClass);
        // 设置属性userService,此属性引用已经定义的bean:userService,这里userService已经被spring容器管理了.
        //        beanDefinitionBuilder.addPropertyReference("testService", "testService");
        // 注册bean
        String beanName = StrUtil.upperFirst(tClass.getSimpleName());
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
    }
}

