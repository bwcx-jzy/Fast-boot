package cn.jiangzeyin.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存字段属性配置
 *
 * @author jiangzeyin
 * @date 2017/12/2
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheConfigField {
    int value() default ObjectCache.DEFAULT_CACHE_TIME;

    TimeUnit UNIT() default TimeUnit.SECONDS;
}
