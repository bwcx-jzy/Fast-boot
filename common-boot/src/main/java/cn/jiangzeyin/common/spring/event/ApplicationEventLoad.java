package cn.jiangzeyin.common.spring.event;

/**
 * Spring 容器监听
 *
 * @author jiangzeyin
 * @date 2018/4/13.
 */
public interface ApplicationEventLoad {
    /**
     * 初始化完成
     */
    void applicationLoad();
}
