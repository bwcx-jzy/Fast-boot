package cn.jiangzeyin.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存类配置
 * Created by jiangzeyin on 2017/12/1.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheConfig {
    /**
     * 缓存时间
     *
     * @return int
     */
    int value() default ObjectCache.DEFAULT_CACHE_TIME;

    /**
     * 时间格式
     *
     * @return timeUnit
     */
    TimeUnit UNIT() default TimeUnit.SECONDS;
}
