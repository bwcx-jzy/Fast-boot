package cn.jiangzeyin.common.interceptor;

import java.lang.annotation.*;

/**
 * 拦截器注解
 *
 * @author jiangzeyin
 * @date 2017/7/8
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

    /**
     * 拦截器排序
     *
     * @return 值越小 先执行
     */
    int sort() default 0;
}
