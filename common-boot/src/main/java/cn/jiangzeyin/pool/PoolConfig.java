package cn.jiangzeyin.pool;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置注解
 * Created by jiangzeyin on 2017/12/2.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PoolConfig {
    /**
     * 线程池核心数
     *
     * @return 默认0
     */
    int value() default 0;

    /**
     * 线程池最大线程数
     *
     * @return 默认 int 的最大值
     */
    int maximumPoolSize() default Integer.MAX_VALUE;

    /**
     * 线程空闲多久将销毁
     *
     * @return 默认60
     */
    long keepAliveTime() default 60L;

    /**
     * 时间单位
     *
     * @return 默认秒
     */
    TimeUnit UNIT() default TimeUnit.SECONDS;

    /**
     * 线程池拒绝执行处理策略
     *
     * @return 默认立即执行
     */
    PolicyHandler HANDLER() default PolicyHandler.Caller;

    /**
     * 线程队列数的最大值
     *
     * @return 默认0  不判断
     */
    int queueMaxSize() default 0;
}
