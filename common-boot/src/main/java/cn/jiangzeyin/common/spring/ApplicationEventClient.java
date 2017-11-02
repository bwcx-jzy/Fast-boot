package cn.jiangzeyin.common.spring;

import org.springframework.context.ApplicationEvent;

/**
 * Created by jiangzeyin on 2017/11/2.
 */
public interface ApplicationEventClient {
    void onApplicationEvent(ApplicationEvent event);

    void applicationLoad();
}
