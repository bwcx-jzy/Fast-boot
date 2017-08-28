package cn.jiangzeyin.common;

import cn.jiangzeyin.system.SystemBean;
import org.springframework.boot.SpringApplication;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/1/10.
 */
public class BaseApplication extends SpringApplication {

    /**
     * @param sources sources
     */
    public BaseApplication(Object... sources) {
        super(sources);
        setBanner((environment, sourceClass, out) -> {
            SystemBean.SYSTEM_TAG = environment.getProperty("server.tag", "");
            String dev = environment.getProperty("spring.profiles.active", "dev");
            SystemBean.Active active = SystemBean.Active.valueOf(dev);
            out.println("优客创想 " + SystemBean.SYSTEM_TAG + " 系统启动中:" + active.getTip());
        });
    }
}
