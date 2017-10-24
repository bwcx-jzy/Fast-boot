package cn.jiangzeyin.common;

import java.lang.annotation.*;

/**
 * Created by jiangzeyin on 2017/10/24.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreLoadMethod {
    int value() default 0;
}
