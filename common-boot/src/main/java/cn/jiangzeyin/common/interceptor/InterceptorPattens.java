package cn.jiangzeyin.common.interceptor;

import java.lang.annotation.*;

/**
 * 拦截器注解
 * Created by jiangzeyin on 2017/7/8.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptorPattens {
    /**
     * 拦截目录
     *
     * @return 数组
     */
    String[] value() default {"/**"};

    /**
     * 排除目录
     *
     * @return 数组
     */
    String[] exclude() default {};
}
