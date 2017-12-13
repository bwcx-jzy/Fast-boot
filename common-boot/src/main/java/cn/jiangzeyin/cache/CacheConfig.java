package cn.jiangzeyin.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiangzeyin on 2017/12/1.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheConfig {
    int value() default ObjectCache.DEFAULT_CACHE_TIME;

    TimeUnit UNIT() default TimeUnit.SECONDS;
}
