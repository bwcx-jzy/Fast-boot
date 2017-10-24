//package cn.jiangzeyin.util.system.util;
//
//import ch.qos.logback.classic.Logger;
//import cn.jiangzeyin.util.system.interfaces.*;
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.util.Assert;
//
//import java.util.concurrent.ThreadPoolExecutor;
//
///**
// * @author jiangzeyin
// * Created by jiangzeyin on 2017/2/7.
// */
//public class UtilSystemCache implements UtilSiteCacheInterface, UtilSystemLogInterface, UtilSystemParameterInterface, UtilSystemValueInterface, UtilSystemExecutorService {
//
//    private volatile static UtilSystemCache utilSystemCache;
//    private volatile static UtilSiteCacheInterface utilSiteCacheInterface;
//    private volatile static UtilSystemLogInterface utilSystemLogInterface;
//    private volatile static UtilSystemParameterInterface utilSystemParameterInterface;
//    private volatile static UtilSystemValueInterface utilSystemValueInterface;
//    private volatile static UtilSystemExecutorService utilSystemExecutorService;
//
//    /**
//     * 系统配置对象
//     *
//     * @return util
//     */
//    public static UtilSystemCache getInstance() {
//        if (utilSystemCache == null) {
//            synchronized (UtilSystemCache.class) {
//                if (utilSystemCache == null) {
//                    utilSystemCache = new UtilSystemCache();
//                }
//            }
//        }
//        return utilSystemCache;
//    }
//
//    private UtilSystemCache() {
//
//    }
//
//    public static void init(UtilSiteCacheInterface utilSiteCacheInterface) {
//        UtilSystemCache.utilSiteCacheInterface = utilSiteCacheInterface;
//        UtilSystemCache.getInstance().LOG_INFO().info("init util site ok");
//    }
//
//
//    public static void init(UtilSystemLogInterface utilSystemLogInterface) {
//        UtilSystemCache.utilSystemLogInterface = utilSystemLogInterface;
//        UtilSystemCache.getInstance().LOG_INFO().info("util logsss init ok");
//    }
//
//    public static void init(UtilSystemParameterInterface utilSystemParameterInterface) {
//        UtilSystemCache.utilSystemParameterInterface = utilSystemParameterInterface;
//        UtilSystemCache.getInstance().LOG_INFO().info("util SystemParameter init ok");
//    }
//
//    public static void init(UtilSystemValueInterface utilSystemValueInterface) {
//        UtilSystemCache.utilSystemValueInterface = utilSystemValueInterface;
//    }
//
//    public static void init(UtilSystemExecutorService utilSystemExecutorService) {
//        UtilSystemCache.utilSystemExecutorService = utilSystemExecutorService;
//    }
//
//    @Override
//    public String getSiteUrl(String tag) {
//        Assert.notNull(utilSiteCacheInterface);
//        return utilSiteCacheInterface.getSiteUrl(tag);
//    }
//
//    @Override
//    public String getLocalPath(String tag) {
//        Assert.notNull(utilSiteCacheInterface);
//        return utilSiteCacheInterface.getLocalPath(tag);
//    }
//
//    @Override
//    public int getCurrentSiteId() {
//        Assert.notNull(utilSiteCacheInterface);
//        return utilSiteCacheInterface.getCurrentSiteId();
//    }
//
//    @Override
//    public Logger LOG_INFO() {
//        Assert.notNull(utilSystemLogInterface);
//        return utilSystemLogInterface.LOG_INFO();
//    }
//
//    @Override
//    public Logger LOG_ERROR() {
//        Assert.notNull(utilSystemLogInterface);
//        return utilSystemLogInterface.LOG_ERROR();
//    }
//
//    @Override
//    public String getSystemParameterValue(String name) {
//        Assert.notNull(utilSystemParameterInterface);
//        return utilSystemParameterInterface.getSystemParameterValue(name);
//    }
//
//    @Override
//    public String getSystemParameterValue(String name, String def) {
//        Assert.notNull(utilSystemParameterInterface);
//        return utilSystemParameterInterface.getSystemParameterValue(name, def);
//    }
//
//    @Override
//    public JSONObject systemParameterToJSONObject(String name) {
//        Assert.notNull(utilSystemParameterInterface);
//        return utilSystemParameterInterface.systemParameterToJSONObject(name);
//    }
//
//    @Override
//    public String getSystemTag() {
//        Assert.notNull(utilSystemValueInterface);
//        return utilSystemValueInterface.getSystemTag();
//    }
//
//    @Override
//    public ThreadPoolExecutor newCachedThreadPool(Class cls) {
//        Assert.notNull(utilSystemExecutorService);
//        return utilSystemExecutorService.newCachedThreadPool(cls);
//    }
//}