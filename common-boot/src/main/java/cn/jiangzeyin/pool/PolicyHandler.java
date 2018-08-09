package cn.jiangzeyin.pool;

/**
 * 线程池拒绝策略
 *
 * @author jiangzeyin
 * data 2017/12/2
 */
public enum PolicyHandler {
    /**
     * 取消
     */
    Caller,
    Abort,
    Discard,
    /**
     * 丢弃
     */
    DiscardOldest
}
