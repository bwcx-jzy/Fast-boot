package cn.jiangzeyin.pool;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiangzeyin on 2017/12/2.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PoolConfig {
    // 线程池核心数
    int value() default 0;

    // 线程池最大线程数
    int maximumPoolSize() default Integer.MAX_VALUE;

    // 线程空闲多久将销毁
    long keepAliveTime() default 60L;

    // 时间单位
    TimeUnit UNIT() default TimeUnit.SECONDS;

    // 线程池拒绝执行处理策略
    PolicyHandler HANDLER() default PolicyHandler.Caller;
}
