package cn.jiangzeyin.common;

import java.lang.annotation.*;

/**
 * 调用配置class
 *
 * @author jiangzeyin
 * @date 2018/5/8
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoPropertiesClass {
}
