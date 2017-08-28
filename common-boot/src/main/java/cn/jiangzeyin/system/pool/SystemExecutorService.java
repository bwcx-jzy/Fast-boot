package cn.jiangzeyin.system.pool;


import cn.jiangzeyin.system.log.SystemLog;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

/**
 * 系统线程池管理
 *
 * @author jiangzeyin
 */
public class SystemExecutorService {
    private final static ConcurrentHashMap<Class<?>, ThreadPoolExecutor> ExecutorServiceMap = new ConcurrentHashMap<>();// ExecutorService
    private final static ConcurrentHashMap<Class<?>, SystemThreadFactory> THREAD_FACTORY_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * 创建一个无限制线程池
     *
     * @param class1 class
     * @return pool
     * @author jiangzeyin
     */
    public static ThreadPoolExecutor newCachedThreadPool(Class<?> class1) {
        Assert.notNull(class1, " 线程池工作类不能为空");
//        if (ExecutorServiceMap.contains(class1))
//            throw new IllegalAccessException(String.format("%s 的线程池已经存在", class1.getName()));
        ThreadPoolExecutor executorService = ExecutorServiceMap.get(class1);

        if (executorService == null) {
            executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            ExecutorServiceMap.put(class1, executorService);
            // 提交线程池失败 处理方法
            executorService.setRejectedExecutionHandler(new CallerRunsPolicy());
            // 创建线程方法
            SystemThreadFactory systemThreadFactory = new SystemThreadFactory(class1.getName());
            THREAD_FACTORY_CONCURRENT_HASH_MAP.put(class1, systemThreadFactory);
            executorService.setThreadFactory(systemThreadFactory);
            SystemLog.LOG().info(class1 + "线程池申请成功");
        }
        return executorService;
    }

    /**
     * 获取线程池信息
     *
     * @return list
     * @author jiangzeyin
     */
    public static List<ThreadPoolExecutor> getThreadPoolExecutorInfo() {
        List<ThreadPoolExecutor> executors = new ArrayList<>();
        Iterator<Entry<Class<?>, ThreadPoolExecutor>> entries = ExecutorServiceMap.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<Class<?>, ThreadPoolExecutor> entry = entries.next();
            executors.add(entry.getValue());
        }
        return executors;
    }

    /**
     * 创建线程池
     *
     * @param object obj
     * @return ex
     * @author jiangzeyin
     */
    public static ExecutorService newCachedThreadPool(Object object) {
        Assert.notNull(object, " 线程池工作类不能为空");
        return newCachedThreadPool(object.getClass());
    }

    /**
     * 关闭所有线程池
     *
     * @author jiangzeyin
     */
    public static void shutdown() {
        Iterator<Entry<Class<?>, ThreadPoolExecutor>> entries = ExecutorServiceMap.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<Class<?>, ThreadPoolExecutor> entry = entries.next();
            SystemLog.LOG().info(String.format("关闭%s使用的线程池", entry.getKey()));
            entry.getValue().shutdown();
        }
    }

}
