//package cn.jiangzeyin.common.spring;
//
//import cn.jiangzeyin.system.SystemBean;
//import cn.jiangzeyin.util.net.ip.IpUtil;
//import cn.jiangzeyin.util.util.file.FileUtil;
//import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.Assert;
//
//import java.io.File;
//import java.net.UnknownHostException;
//
///**
// * @author jiangzeyin
// * Created by jiangzeyin on 2017/2/14.
// */
//@Configuration
//public class ServiceInfoUtil implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
//    private static EmbeddedServletContainerInitializedEvent event;
//
//    @Override
//    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
//        ServiceInfoUtil.event = event;
//    }
//
//    public static int getPort() {
//        Assert.notNull(event);
//        int port = event.getEmbeddedServletContainer().getPort();
//        Assert.state(port != -1, "端口号获取失败");
//        return port;
//    }
//
//    /**
//     * 获取当前程序访问地址
//     *
//     * @return url
//     * @throws UnknownHostException 异常
//     */
//    public static String getServiceUrl() throws UnknownHostException {
//        if (SystemBean.getInstance().getActive() == SystemBean.Active.prod) {
//            return SystemBean.getInstance().domain;
//        }
//        return String.format("%s:%s", IpUtil.getHostAddress(), getPort());
//    }
//
//    public static String getTomcatTempPath() {
//        return String.format("%s/work/Tomcat/localhost/ROOT/", SystemBean.getInstance().getTomcatBaseDir());
//    }
//
//    public static void initTomcatTemPath() {
//        if (SiteCache.currentSite == null)
//            return;
//        String path = FileUtil.clearPath(getTomcatTempPath() + "/" + SiteCache.currentSite.getLocalPath());
//        FileUtil.mkdirs(new File(path), true);
//    }
//}
