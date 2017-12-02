package cn.jiangzeyin.pool;

import java.lang.annotation.*;

/**
 * Created by jiangzeyin on 2017/12/2.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigClass {
    int value() default 0;

    int maximumPoolSize() default Integer.MAX_VALUE;

    long keepAliveTime() default 60L;
}
