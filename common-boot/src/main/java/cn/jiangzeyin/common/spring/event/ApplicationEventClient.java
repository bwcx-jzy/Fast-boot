package cn.jiangzeyin.common.spring.event;

import org.springframework.context.ApplicationEvent;

/**
 * 系统启动监听
 * Created by jiangzeyin on 2017/11/2.
 *
 * @author jiangzeyin
 */
public interface ApplicationEventClient {
    /**
     * 启动完成
     *
     * @param event 对应事件
     */
    void onApplicationEvent(ApplicationEvent event);
}
