package cn.jiangzeyin.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by jiangzeyin on 2017/12/1.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigClass {
    int value() default 5 * 60 * 1000;

    int containerMaxSize() default 100;

    TimeUnit UNIT() default TimeUnit.SECONDS;
}
