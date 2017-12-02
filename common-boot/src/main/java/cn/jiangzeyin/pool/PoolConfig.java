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
    int value() default 0;

    int maximumPoolSize() default Integer.MAX_VALUE;

    long keepAliveTime() default 60L;

    TimeUnit UNIT() default TimeUnit.SECONDS;

    PolicyHandler HANDLER() default PolicyHandler.Caller;
}
