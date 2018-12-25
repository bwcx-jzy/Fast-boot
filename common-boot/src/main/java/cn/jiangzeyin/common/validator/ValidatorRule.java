package cn.jiangzeyin.common.validator;

/**
 * 验证规则
 * Created by jiangzeyin on 2018/8/21.
 *
 * @author jiangzeyin
 */
public enum ValidatorRule {
    /**
     * 不为空
     */
    NOT_EMPTY,
    /**
     * 空
     */
    EMPTY,
    /**
     * 不为空白
     */
    NOT_BLANK,
    /**
     * 手机号码
     */
    MOBILE,
    /**
     * 邮箱
     */
    EMAIL,
    /**
     * 英文字母 、数字和下划线
     */
    GENERAL,
    /**
     * url
     */
    URL,
    /**
     * 汉字
     */
    CHINESE,
    /**
     * 是否是字母（包括大写和小写字母）
     */
    WORD,
    /**
     * 小数
     *
     * @see ParameterInterceptor#validatorNumber(cn.jiangzeyin.common.validator.ValidatorItem, java.lang.String)
     */
    DECIMAL,
    /**
     * 数字
     *
     * @see ParameterInterceptor#validatorNumber(cn.jiangzeyin.common.validator.ValidatorItem, java.lang.String)
     */
    NUMBERS,
    /**
     * 非零 正整数  最大长度7位
     */
    NON_ZERO_INTEGERS,
    /**
     * 正整数  包括0  最大长度7位
     */
    POSITIVE_INTEGER,
    /**
     * 自定义验证
     */
    CUSTOMIZE
}
