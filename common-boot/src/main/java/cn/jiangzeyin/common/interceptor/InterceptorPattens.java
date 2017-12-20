package cn.jiangzeyin.common.interceptor;

import java.lang.annotation.*;

/**
 * Created by jiangzeyin on 2017/7/8.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptorPattens {
    String[] value() default {"/**"};

    String[] exclude() default {};
}
