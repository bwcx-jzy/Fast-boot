package cn.jiangzeyin.common.spring;

import org.springframework.context.ApplicationEvent;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

/**
 * 系统启动监听
 * Created by jiangzeyin on 2017/11/2.
 */
public interface ApplicationEventClient {
    /**
     * 启动完成
     *
     * @param event 对应事件
     */
    void onApplicationEvent(ApplicationEvent event);

    /**
     * 初始化完成
     */
    void applicationLoad();

    /**
     * 获取系统默认的拦截器
     *
     * @return 拦截器数组
     */
    List<Class> getApplicationInterceptor();

    /**
     * 消息转换器
     *
     * @return 装换器
     */
    List<HttpMessageConverter<?>> getMessageConverters();
}
