package cn.jiangzeyin.common.spring.event;

/**
 * Spring 容器监听
 * Created by jiangzeyin on 2018/4/13.
 */
public interface ApplicationEventLoad {
    /**
     * 初始化完成
     */
    void applicationLoad();
}
