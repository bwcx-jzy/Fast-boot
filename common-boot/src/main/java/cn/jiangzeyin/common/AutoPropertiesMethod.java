package cn.jiangzeyin.common;

import java.lang.annotation.*;

/**
 * 调用方法
 *
 * @author jiangzeyin
 * @date 2018/5/8
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoPropertiesMethod {
}
