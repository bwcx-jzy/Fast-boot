package cn.jiangzeyin.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiangzeyin on 2017/12/2.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigField {
    int value() default 5 * 60 * 1000;

    int containerMaxSize() default 100;

    TimeUnit UNIT() default TimeUnit.SECONDS;
}
