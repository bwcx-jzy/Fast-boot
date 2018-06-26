package cn.jiangzeyin.pool;

/**
 * 线程池拒绝策略
 * Created by jiangzeyin on 2017/12/2.
 */
public enum PolicyHandler {
    Caller,
    Abort,
    Discard,
    DiscardOldest
}
