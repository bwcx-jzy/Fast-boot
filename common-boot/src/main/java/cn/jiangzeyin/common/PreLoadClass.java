package cn.jiangzeyin.common;

import java.lang.annotation.*;

/**
 * 标记类需要初始化
 * Created by jiangzeyin on 2017/10/24.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreLoadClass {
    int value() default 0;
}
