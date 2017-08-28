package cn.jiangzeyin.system;

import cn.jiangzeyin.common.spring.SpringUtil;
import cn.jiangzeyin.util.system.interfaces.UtilSystemValueInterface;
import cn.jiangzeyin.util.system.util.UtilSystemCache;
import cn.jiangzeyin.util.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/5.
 */
@Configuration
public class SystemBean implements UtilSystemValueInterface, EnvironmentAware {
    private volatile static SystemBean systemBean;
    //public static final String DefaultKey = "server.defaultKey";
    /**
     * 系统标示
     */
    public static String SYSTEM_TAG = "";
    /**
     * before 系统密钥
     */
    public static String beforeDefaultKey;
    private Active active;
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 模板文件存放路径
     */
    @Value("${spring.velocity.resource-loader-path:}")
    public String VelocityPath;
    /**
     * 模板文件后缀
     */
    @Value("${spring.velocity.suffix:}")
    public String velocitySuffix;
//    /**
//     * 系统标示
//     */
//    @Value("${server.tag}")
//    public String systemTag;
    /**
     * 系统密钥
     */
    @Value("${server.defaultKey}")
    public String defaultKey;
    /**
     * 检测请求超时记录时间
     */
    @Value("${request_timeout_log:3000}")
    public Long request_timeout_log;

    /**
     * tomcat 路径
     */
    @Value("${server.tomcat.basedir:}")
    private String tomcatBaseDir;
    /**
     * 程序绑定域名
     */
    @Value("${server.domain:}")
    public String domain;
    /**
     * 程序运行模式
     */
    @Value("${spring.profiles.active:dev}")
    private String profiles_active;
    /**
     *
     */
    @Value("${server.api.token:}")
    public String systemApiToken;


    /**
     * 系统配置对象
     *
     * @return systemBean
     */
    public static SystemBean getInstance() {
        if (systemBean == null) {
            synchronized (SystemBean.class) {
                if (systemBean == null) {
                    systemBean = SpringUtil.getBean(SystemBean.class);
                    // EntitySystemCache.init(systemBean);
                    UtilSystemCache.init(systemBean);
                }
            }
        }
        return systemBean;
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public String getSystemTag() {
        return SYSTEM_TAG;
    }

    public String getTomcatBaseDir() {
        return tomcatBaseDir;
    }

    public Active getActive() {
        if (active == null) {
            active = Active.valueOf(profiles_active);
        }
        if (active == Active.prod && StringUtil.isEmpty(domain))
            throw new RuntimeException("生产模式请配置domain");
        return active;
    }

    public boolean isDebug() {
        return active == Active.dev;
    }

    public enum Active {
        dev("开发模式"),
        prod("生产环境");

        private String tip;

        public String getTip() {
            return tip;
        }

        Active(String tip) {
            this.tip = tip;
        }
    }
}
