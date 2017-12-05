package cn.jiangzeyin.pool;

import cn.jiangzeyin.OtherUtil;
import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.common.DefaultSystemLog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
    private final static ConcurrentHashMap<Class, PoolCacheInfo> POOL_CACHE_INFO_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

    private ThreadPoolService() {
    }

    /**
     * 创建一个无限制线程池
     *
     * @param class1 class
     * @return 创建成功的线程对象
     * @author jiangzeyin
     * 2016-10-24
     */
    public synchronized static ExecutorService newCachedThreadPool(Class class1) {
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

    /**
     * 创建一个缓存线程对象
     *
     * @param tClass 线程池主类
     * @return 缓存对象
     */
    private static PoolCacheInfo createPool(Class tClass) {
        PoolConfig poolConfig = (PoolConfig) tClass.getAnnotation(PoolConfig.class);
        BlockingQueue<Runnable> blockingQueue;
        SystemThreadFactory systemThreadFactory = new SystemThreadFactory(tClass.getName());
        ThreadPoolExecutor threadPoolExecutor;
        ProxyHandler proxyHandler;
        if (poolConfig == null) {
            proxyHandler = new ProxyHandler(PolicyHandler.Caller);
            blockingQueue = new SynchronousQueue<>();
            threadPoolExecutor = new ThreadPoolExecutor(0,
                    Integer.MAX_VALUE,
                    60L,
                    TimeUnit.SECONDS,
                    blockingQueue,
                    systemThreadFactory,
                    proxyHandler);
        } else {
            proxyHandler = new ProxyHandler(poolConfig.HANDLER());
            int corePoolSize = poolConfig.value();
            if (corePoolSize == 0)
                blockingQueue = new SynchronousQueue<>();
            else
                blockingQueue = new LinkedBlockingQueue<>();
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                    poolConfig.maximumPoolSize(),
                    poolConfig.keepAliveTime(),
                    poolConfig.UNIT(),
                    blockingQueue,
                    systemThreadFactory,
                    proxyHandler);
        }
        return new PoolCacheInfo(threadPoolExecutor, blockingQueue, proxyHandler, systemThreadFactory);
    }

    /**
     * 获取线程池队列数
     *
     * @param tClass 线程池主类
     * @return int
     */
    public static int getPoolQueuedTasks(Class tClass) {
        PoolCacheInfo poolCacheInfo = POOL_CACHE_INFO_CONCURRENT_HASH_MAP.get(tClass);
        if (poolCacheInfo == null)
            return 0;
        return poolCacheInfo.blockingQueue.size();
    }

    /**
     * 获取线程池取消执行的任务数
     *
     * @param tclass 线程池主类
     * @return int
     */
    public static int getPoolRejectedExecutionCount(Class tclass) {
        PoolCacheInfo poolCacheInfo = POOL_CACHE_INFO_CONCURRENT_HASH_MAP.get(tclass);
        if (poolCacheInfo == null)
            return 0;
        return poolCacheInfo.handler.getRejectedExecutionCount();
    }

    /**
     * 获取线程池信息
     *
     * @return 所有线程对象
     * @author jiangzeyin
     * create 2016-11-29
     */
    public static JSONArray getThreadPoolStatusInfo() {
        JSONArray jsonArray = new JSONArray();
        for (Entry<Class, PoolCacheInfo> entry : POOL_CACHE_INFO_CONCURRENT_HASH_MAP.entrySet()) {
            PoolCacheInfo poolCacheInfo = entry.getValue();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", OtherUtil.simplifyClassName(entry.getKey().getName()));
            ThreadPoolExecutor threadPoolExecutor = poolCacheInfo.poolExecutor;
            jsonObject.put("corePoolSize", threadPoolExecutor.getCorePoolSize()); // 核心数
            jsonObject.put("poolSize", threadPoolExecutor.getPoolSize()); // 工作集数
            jsonObject.put("activeCount", threadPoolExecutor.getActiveCount()); // 活跃线程数
            jsonObject.put("largestPoolSize", threadPoolExecutor.getLargestPoolSize()); // 曾经最大线程数
            jsonObject.put("completedTaskCount", threadPoolExecutor.getCompletedTaskCount()); // 已完成数
            jsonObject.put("taskCount", threadPoolExecutor.getTaskCount()); // 总任务数
            jsonObject.put("queueSize", poolCacheInfo.blockingQueue.size()); // 任务队列数
            jsonObject.put("rejectedExecutionCount", poolCacheInfo.handler.getRejectedExecutionCount()); // 拒绝任务数
            jsonObject.put("maxThreadNumber", poolCacheInfo.systemThreadFactory.threadNumber.get()); // 最大线程编号
            jsonObject.put("maximumPoolSize", threadPoolExecutor.getMaximumPoolSize());// 最大线程数
            jsonArray.add(jsonObject);
        }
        return jsonArray;
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
        private final BlockingQueue<Runnable> blockingQueue;
        private final ProxyHandler handler;
        private final SystemThreadFactory systemThreadFactory;

        PoolCacheInfo(ThreadPoolExecutor poolExecutor, BlockingQueue<Runnable> blockingQueue, ProxyHandler handler, SystemThreadFactory systemThreadFactory) {
            this.poolExecutor = poolExecutor;
            this.blockingQueue = blockingQueue;
            this.handler = handler;
            this.systemThreadFactory = systemThreadFactory;
        }

        @Override
        public String toString() {
            return poolExecutor.toString() +
                    " MaximumPoolSize:" + poolExecutor.getMaximumPoolSize() +
                    " CorePoolSize:" + poolExecutor.getCorePoolSize() +
                    " LargestPoolSize:" + poolExecutor.getLargestPoolSize() +
                    " blockingQueue:" + blockingQueue.size() +
                    " RejectedExecutionCount:" + handler.getRejectedExecutionCount();
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

        int getRejectedExecutionCount() {
            return handlerCount.get();
        }
    }

    /**
     * 线程池工厂
     *
     * @author jiangzeyin
     * create 2016-11-21
     */
    static class SystemThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        SystemThreadFactory(String poolName) {
            if (StringUtil.isEmpty(poolName))
                poolName = "pool";
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolName + "-" + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
