package cn.jiangzeyin.pool;

import cn.jiangzeyin.common.DefaultSystemLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

/**
 * 系统线程池管理
 *
 * @author jiangzeyin
 * create 2016-10-24
 */
public class SystemExecutorService {
    private final static ConcurrentHashMap<Class, ThreadPoolExecutor> THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<Class, SynchronousQueue<Runnable>> SYNCHRONOUS_QUEUE_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * 创建一个无限制线程池
     *
     * @param class1 class
     * @return 创建成功的线程对象
     * @author jiangzeyin
     * 2016-10-24
     */
    public static ThreadPoolExecutor newCachedThreadPool(Class class1) {
        if (class1 == null) throw new NullPointerException();
        ThreadPoolExecutor executorService = THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP.get(class1);
        if (executorService == null) {
            executorService = createPool(class1);
            THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP.put(class1, executorService);
            // 提交线程池失败 处理方法
            executorService.setRejectedExecutionHandler(new CallerRunsPolicy());
            // 创建线程方法
            SystemThreadFactory systemThreadFactory = new SystemThreadFactory(class1.getName());
            executorService.setThreadFactory(systemThreadFactory);
            DefaultSystemLog.LOG().info(class1 + "线程池申请成功");
        }
        return executorService;
    }

    private static ThreadPoolExecutor createPool(Class tClass) {
        SynchronousQueue<Runnable> synchronousQueue = new SynchronousQueue<>();
        SYNCHRONOUS_QUEUE_CONCURRENT_HASH_MAP.put(tClass, synchronousQueue);
        ConfigClass configClass = (ConfigClass) tClass.getAnnotation(ConfigClass.class);
        if (configClass == null)
            return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, synchronousQueue);
        else
            return new ThreadPoolExecutor(configClass.value(), configClass.maximumPoolSize(), configClass.keepAliveTime(), TimeUnit.SECONDS, synchronousQueue);
    }

    public static int getPoolQueuedTasks(Class tClass) {
        SynchronousQueue<Runnable> synchronousQueue = SYNCHRONOUS_QUEUE_CONCURRENT_HASH_MAP.get(tClass);
        return synchronousQueue == null ? 0 : synchronousQueue.size();
    }

    /**
     * 获取线程池信息
     *
     * @return 所有线程对象
     * @author jiangzeyin
     * create 2016-11-29
     */
    public static List<ThreadPoolExecutor> getThreadPoolExecutorInfo() {
        List<ThreadPoolExecutor> executors = new ArrayList<>();
        for (Entry<Class, ThreadPoolExecutor> entry : THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP.entrySet()) {
            executors.add(entry.getValue());
        }
        return executors;
    }

    /**
     * 关闭所有线程池
     *
     * @author jiangzeyin
     * create 2016-10-24
     */
    public static void shutdown() {
        for (Entry<Class, ThreadPoolExecutor> entry : THREAD_POOL_EXECUTOR_CONCURRENT_HASH_MAP.entrySet()) {
            DefaultSystemLog.LOG().info(String.format("关闭%s使用的线程池", entry.getKey()));
            entry.getValue().shutdown();
        }
    }

}
