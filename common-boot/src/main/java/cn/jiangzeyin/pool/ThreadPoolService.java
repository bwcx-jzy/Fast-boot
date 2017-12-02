package cn.jiangzeyin.pool;

import cn.jiangzeyin.common.DefaultSystemLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统线程池管理
 *
 * @author jiangzeyin
 * create 2016-10-24
 */
public class ThreadPoolService {
    private ThreadPoolService() {
    }

    private final static ConcurrentHashMap<Class, PoolCacheInfo> POOL_CACHE_INFO_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    /**
     * 创建一个无限制线程池
     *
     * @param class1 class
     * @return 创建成功的线程对象
     * @author jiangzeyin
     * 2016-10-24
     */
    public synchronized static ThreadPoolExecutor newCachedThreadPool(Class class1) {
        if (class1 == null) throw new NullPointerException();
        PoolCacheInfo poolCacheInfo = POOL_CACHE_INFO_CONCURRENT_HASH_MAP.get(class1);
        if (poolCacheInfo == null) {
            // 创建线程方法
            poolCacheInfo = createPool(class1);
            POOL_CACHE_INFO_CONCURRENT_HASH_MAP.put(class1, poolCacheInfo);
            DefaultSystemLog.LOG().info(class1 + "线程池申请成功:" + poolCacheInfo);
        }
        return poolCacheInfo.poolExecutor;
    }

    private static PoolCacheInfo createPool(Class tClass) {
        PoolConfig poolConfig = (PoolConfig) tClass.getAnnotation(PoolConfig.class);
        SynchronousQueue<Runnable> synchronousQueue = new SynchronousQueue<>();
        ThreadPoolExecutor threadPoolExecutor;
        ProxyHandler proxyHandler;
        if (poolConfig == null) {
            threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, synchronousQueue);
            proxyHandler = new ProxyHandler(PolicyHandler.Caller);
        } else {
            threadPoolExecutor = new ThreadPoolExecutor(poolConfig.value(), poolConfig.maximumPoolSize(), poolConfig.keepAliveTime(), poolConfig.UNIT(), synchronousQueue);
            proxyHandler = new ProxyHandler(poolConfig.HANDLER());
        }
        threadPoolExecutor.setRejectedExecutionHandler(proxyHandler);
        SystemThreadFactory systemThreadFactory = new SystemThreadFactory(tClass.getName());
        threadPoolExecutor.setThreadFactory(systemThreadFactory);
        return new PoolCacheInfo(threadPoolExecutor, synchronousQueue, proxyHandler);
    }

    public static int getPoolQueuedTasks(Class tClass) {
        PoolCacheInfo poolCacheInfo = POOL_CACHE_INFO_CONCURRENT_HASH_MAP.get(tClass);
        if (poolCacheInfo == null)
            return 0;
        return poolCacheInfo.synchronousQueue.size();
    }

    public static int getPoolRejectedExecutionCount(Class tclass) {
        PoolCacheInfo poolCacheInfo = POOL_CACHE_INFO_CONCURRENT_HASH_MAP.get(tclass);
        if (poolCacheInfo == null)
            return 0;
        return poolCacheInfo.handler.getHandlerCount();
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
        for (Entry<Class, PoolCacheInfo> entry : POOL_CACHE_INFO_CONCURRENT_HASH_MAP.entrySet()) {
            executors.add(entry.getValue().poolExecutor);
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
        for (Entry<Class, PoolCacheInfo> entry : POOL_CACHE_INFO_CONCURRENT_HASH_MAP.entrySet()) {
            DefaultSystemLog.LOG().info(String.format("关闭%s使用的线程池", entry.getKey()));
            entry.getValue().poolExecutor.shutdown();
        }
    }

    private static class PoolCacheInfo {
        private final ThreadPoolExecutor poolExecutor;
        private final SynchronousQueue<Runnable> synchronousQueue;
        private final ProxyHandler handler;

        PoolCacheInfo(ThreadPoolExecutor poolExecutor, SynchronousQueue<Runnable> synchronousQueue, ProxyHandler handler) {
            this.poolExecutor = poolExecutor;
            this.synchronousQueue = synchronousQueue;
            this.handler = handler;
        }

        @Override
        public String toString() {
            return poolExecutor.toString() +
                    " MaximumPoolSize:" + poolExecutor.getMaximumPoolSize() +
                    " CorePoolSize:" + poolExecutor.getCorePoolSize() +
                    " LargestPoolSize:" + poolExecutor.getLargestPoolSize() +
                    " queueSize:" + synchronousQueue.size() +
                    " RejectedExecutionCount:" + handler.getHandlerCount();
        }
    }

    private static class ProxyHandler implements RejectedExecutionHandler {
        private final AtomicInteger handlerCount = new AtomicInteger(0);
        private final RejectedExecutionHandler rejectedExecutionHandler;

        ProxyHandler(PolicyHandler policyHandler) {
            RejectedExecutionHandler rejectedExecutionHandler1 = null;
            switch (policyHandler) {
                case Abort:
                    rejectedExecutionHandler1 = new ThreadPoolExecutor.AbortPolicy();
                    break;
                case Caller:
                    rejectedExecutionHandler1 = new CallerRunsPolicy();
                    break;
                case Discard:
                    rejectedExecutionHandler1 = new ThreadPoolExecutor.DiscardPolicy();
                    break;
                case DiscardOldest:
                    rejectedExecutionHandler1 = new ThreadPoolExecutor.DiscardOldestPolicy();
                    break;
            }
            rejectedExecutionHandler = rejectedExecutionHandler1;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            handlerCount.getAndIncrement();
            rejectedExecutionHandler.rejectedExecution(r, executor);
        }

        int getHandlerCount() {
            return handlerCount.get();
        }
    }
}
