package cn.jiangzeyin.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存静态字段配置注解
 *
 * @author jiangzeyin
 * @date 2017/12/13
 * @see RedisCacheConfig#loadClass(java.lang.Class)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheConfigWildcardField {
    int value();

    long time() default ObjectCache.DEFAULT_CACHE_TIME;

    TimeUnit UNIT() default TimeUnit.SECONDS;
}
