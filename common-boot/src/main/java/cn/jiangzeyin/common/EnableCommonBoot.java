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
@Import(CommonInitPackage.class)
public @interface EnableCommonBoot {
}
