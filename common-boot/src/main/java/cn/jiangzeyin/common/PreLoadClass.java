package cn.jiangzeyin.common;

import java.lang.annotation.*;

/**
 * Created by jiangzeyin on 2017/10/24.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreLoadClass {
    int value() default 0;
}
