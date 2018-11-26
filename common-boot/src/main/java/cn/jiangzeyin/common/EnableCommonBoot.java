package cn.jiangzeyin.common;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启Common boot
 * Created by jiangzeyin on 2018/8/13.
 *
 * @author jiangzeyin
 * @since 1.1.22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(CommonInitPackage.class)
public @interface EnableCommonBoot {
    /**
     * 是否开启参数验证拦截器
     *
     * @return 默认开启
     */
    boolean parameterValidator() default false;
}
