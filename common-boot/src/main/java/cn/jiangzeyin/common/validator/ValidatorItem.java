package cn.jiangzeyin.common.validator;

import java.lang.annotation.*;

/**
 * 验证规则
 *
 * @author jiangzeyin
 * @date 2018/8/21
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidatorItem {
    /**
     * 规则
     *
     * @return ValidatorRule
     */
    ValidatorRule value() default ValidatorRule.NOT_EMPTY;

    /**
     * 还原html 转义字符
     * 一般用户字符串长度验证统一性
     *
     * @return 默认不还原
     */
    boolean unescape() default false;

    /**
     * 响应码
     *
     * @return 默认400
     */
    int code() default 400;

    /**
     * 错误信息
     *
     * @return msg
     */
    String msg() default "输入参数不正确";

    /**
     * 数字类型的范围
     * 配置错误将不判断
     * <p>
     * 范围写反也将不判断
     * <p>
     * 逻辑判断符 是 &gt; 或者 &lt;
     * <p>
     * 当 ValidatorRule 为 CUSTOMIZE 时此参数无效
     *
     * @return 1.2:2  或者 1  或者 1.2:2.5[1]
     * @see cn.jiangzeyin.common.validator.ValidatorRule#CUSTOMIZE
     */
    String range() default "";
}
