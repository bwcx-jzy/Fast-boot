package cn.jiangzeyin;

/**
 * 配置属性字段
 *
 * @author jiangzeyin
 * data 2017/8/25
 */
public final class CommonPropertiesFinal {
    /**
     * 程序启动banner 文字信息
     */
    public static final String BANNER_MSG = "banner.msg";
    /**
     * 当前程序定义一个id
     */
    public static final String APPLICATION_ID = "spring.application.name";
    /**
     * 请求的相关配置
     */
    private static final String REQUEST = "request";
    /**
     * 拦截器中记录超时请求时长
     */
    public static final String REQUEST_TIME_OUT = REQUEST + ".timeout";
    /**
     * 是否记录日志  默认记录  true false
     */
    public static final String REQUEST_LOG = REQUEST + ".log";
    /**
     * 参数xss 提前过滤
     */
    public static final String REQUEST_PARAMETER_XSS = REQUEST + ".parameterXss";
    /**
     * 参数去掉前后空格
     */
    public static final String REQUEST_PARAMETER_TRIM_ALL = REQUEST + ".trimAll";

    /**
     * tomcat 配置
     */
    private static final String TOMCAT = "tomcat";
    /**
     * 自定义浏览器中存储session id cookie 名称
     */
    public static final String TOMCAT_SESSION_COOKIE_NAME = TOMCAT + ".sessionCookieName";
    /**
     * 定义session 过期时间 单位秒
     */
    public static final String TOMCAT_SESSION_TIME_OUT = TOMCAT + ".sessionTimeOut";
    /**
     * 自定义外部代理中已经获取到的ip header 信息名称（比如nginx 中代理）
     */
    public static final String IP_DEFAULT_HEADER_NAME = "ip.defaultHeaderName";
    /**
     * 拦截器配置
     */
    private static final String INTERCEPTOR = "interceptor";
    /**
     * 加载指定包名下的拦截器
     */
    public static final String INTERCEPTOR_INIT_PACKAGE_NAME = INTERCEPTOR + ".initPackageName";
    /**
     * 拦截器静态资源url路径
     */
    public static final String INTERCEPTOR_RESOURCE_HANDLER = INTERCEPTOR + ".resourceHandler";
    /**
     * 拦截器静态资源文件路径
     */
    public static final String INTERCEPTOR_RESOURCE_LOCATION = INTERCEPTOR + ".resourceLocation";
    /**
     * 预加载
     */
    private static final String PRELOAD = "preload";
    /**
     * 预加载指定包下面的class
     */
    public static final String PRELOAD_PACKAGE_NAME = PRELOAD + ".packageName";
    // 预加载class的 方法名
    //public static final String PRELOAD_METHOD_NAME = PRELOAD + ".methodName";
}
