package cn.jiangzeyin.common.validator;

import org.springframework.web.bind.annotation.ValueConstants;

import java.lang.annotation.*;

/**
 * 字段验证配置
 * Created by jiangzeyin on 2018/8/21.
 *
 * @author jiangzeyin
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidatorConfig {
    /**
     * 需要验证的规则
     *
     * @return ValidatorItem
     */
    ValidatorItem[] value() default
            {
                    @ValidatorItem(value = ValidatorRule.NOT_EMPTY)
            };

    /**
     * 自动参数值
     *
     * @return url 参数
     */
    String name() default "";

    /**
     * 默认值
     *
     * @return 默认
     */
    String defaultVal() default ValueConstants.DEFAULT_NONE;

    /**
     * 自定义验证 Controller 中方法名
     *
     * @return 默认 customizeValidator
     */
    String customizeMethod() default "customizeValidator";
}
