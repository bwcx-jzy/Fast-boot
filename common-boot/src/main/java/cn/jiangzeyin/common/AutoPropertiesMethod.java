package cn.jiangzeyin.common;

import java.lang.annotation.*;

/**
 * 调用方法
 * Created by jiangzeyin on 2018/5/8.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoPropertiesMethod {
}
