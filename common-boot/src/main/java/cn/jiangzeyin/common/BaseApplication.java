package cn.jiangzeyin.common;

import cn.jiangzeyin.system.SystemBean;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

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
        // 设置加载当前包
        try {
            setLoadPage((Class) sources[0]);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        // 设置banner
        this.setBanner((environment, sourceClass, out) -> {
            SystemBean.SYSTEM_TAG = environment.getProperty("server.tag", "");
            String dev = environment.getProperty("spring.profiles.active", "dev");
            SystemBean.Active active = SystemBean.Active.valueOf(dev);
            out.println("优客创想 " + SystemBean.SYSTEM_TAG + " 系统启动中:" + active.getTip());
        });
    }

    private void setLoadPage(Class tclass) throws NoSuchFieldException, IllegalAccessException {
        ComponentScan componentScan = (ComponentScan) tclass.getAnnotation(ComponentScan.class);
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(componentScan);
        Field value = invocationHandler.getClass().getDeclaredField("memberValues");
        value.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) value.get(invocationHandler);
        String[] values = (String[]) memberValues.get("value");
        String[] newValues = new String[values.length + 1];
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[newValues.length - 1] = "cn.jiangzeyin";
        memberValues.put("value", newValues);
    }
}