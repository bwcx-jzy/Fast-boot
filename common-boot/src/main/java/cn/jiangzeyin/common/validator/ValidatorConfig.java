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

    /**
     * 判断参数为空 是字符串空
     * 如果为false
     *
     * @return 默认true
     */
    boolean strEmpty() default true;

    /**
     * 错误条件
     * <p>
     * or  一项正确返回正确，所有错误抛出错误
     * <p>
     * and 一项错误 抛出错误并结束整个判断
     *
     * @return 默认or
     */
    ErrorCondition errorCondition() default ErrorCondition.AND;
}
