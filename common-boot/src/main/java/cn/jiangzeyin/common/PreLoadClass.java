package cn.jiangzeyin.common;

import java.lang.annotation.*;

/**
 * 标记类需要初始化
 *
 * @author jiangzeyin
 * @date 2017/10/24
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PreLoadClass {

    int value() default 0;
}
