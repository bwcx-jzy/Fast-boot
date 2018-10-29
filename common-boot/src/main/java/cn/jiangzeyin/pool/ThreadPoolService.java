package cn.jiangzeyin.pool;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
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
public final class ThreadPoolService {
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
        if (class1 == null) {
            throw new NullPointerException();
        }
        PoolCacheInfo poolCacheInfo = POOL_CACHE_INFO_CONCURRENT_HASH_MAP.computeIfAbsent(class1, aClass -> {
            // 创建线程方法
            PoolCacheInfo poolCacheInfo1 = createPool(class1);
            DefaultSystemLog.LOG().info(class1 + "线程池申请成功:" + poolCacheInfo1);
            return poolCacheInfo1;
        });
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
        ThreadPoolExecutorPool threadPoolExecutor;
        ProxyHandler proxyHandler;
        if (poolConfig == null) {
            proxyHandler = new ProxyHandler(PolicyHandler.Caller);
            blockingQueue = new SynchronousQueue<>();
            threadPoolExecutor = new ThreadPoolExecutorPool(0,
                    Integer.MAX_VALUE,
                    60L,
                    TimeUnit.SECONDS,
                    blockingQueue,
                    systemThreadFactory,
                    proxyHandler);
        } else {
            proxyHandler = new ProxyHandler(poolConfig.HANDLER());
            int corePoolSize = poolConfig.value();
            if (corePoolSize == 0) {
                blockingQueue = new SynchronousQueue<>();
            } else {
                blockingQueue = new LinkedBlockingQueue<>();
            }
            // 构建对象
            threadPoolExecutor = new ThreadPoolExecutorPool(corePoolSize,
                    poolConfig.maximumPoolSize(),
                    poolConfig.keepAliveTime(),
                    poolConfig.UNIT(),
                    blockingQueue,
                    systemThreadFactory,
                    proxyHandler);
            // 获取线程队列最大值
            threadPoolExecutor.setQueueMaxSize(poolConfig.queueMaxSize());
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
        if (poolCacheInfo == null) {
            return 0;
        }
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
        if (poolCacheInfo == null) {
            return 0;
        }
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
            String name = ClassUtil.getShortClassName(entry.getKey().getName());
            JSONObject jsonObject = convertInfo(name, poolCacheInfo);
            jsonArray.add(jsonObject);
        }
        // 排序
        jsonArray.sort((o1, o2) -> {
            JSONObject jsonObject1 = (JSONObject) o1;
            JSONObject jsonObject2 = (JSONObject) o2;
            return jsonObject2.getLong("taskCount").compareTo(jsonObject1.getLong("taskCount"));
        });
        return jsonArray;
    }

    private static JSONObject convertInfo(String name, PoolCacheInfo poolCacheInfo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        ThreadPoolExecutor threadPoolExecutor = poolCacheInfo.poolExecutor;
        // 核心数
        jsonObject.put("corePoolSize", threadPoolExecutor.getCorePoolSize());
        // 工作集数
        jsonObject.put("poolSize", threadPoolExecutor.getPoolSize());
        // 活跃线程数
        jsonObject.put("activeCount", threadPoolExecutor.getActiveCount());
        // 曾经最大线程数
        jsonObject.put("largestPoolSize", threadPoolExecutor.getLargestPoolSize());
        // 已完成数
        jsonObject.put("completedTaskCount", threadPoolExecutor.getCompletedTaskCount());
        // 总任务数
        jsonObject.put("taskCount", threadPoolExecutor.getTaskCount());
        // 任务队列数
        jsonObject.put("queueSize", poolCacheInfo.blockingQueue.size());
        // 拒绝任务数
        jsonObject.put("rejectedExecutionCount", poolCacheInfo.handler.getRejectedExecutionCount());
        // 最大线程编号
        jsonObject.put("maxThreadNumber", poolCacheInfo.systemThreadFactory.threadNumber.get());
        // 最大线程数
        jsonObject.put("maximumPoolSize", threadPoolExecutor.getMaximumPoolSize());
        return jsonObject;
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
            String name = ClassUtil.getShortClassName(entry.getKey().getName());
            JSONObject jsonObject = convertInfo(name, entry.getValue());
            DefaultSystemLog.LOG().info("关闭完成:" + jsonObject);
        }
    }

    private static class PoolCacheInfo {
        private final ThreadPoolExecutorPool poolExecutor;
        private final BlockingQueue<Runnable> blockingQueue;
        private final ProxyHandler handler;
        private final SystemThreadFactory systemThreadFactory;

        PoolCacheInfo(ThreadPoolExecutorPool poolExecutor, BlockingQueue<Runnable> blockingQueue, ProxyHandler handler, SystemThreadFactory systemThreadFactory) {
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
                default:
                    throw new IllegalArgumentException("暂时不支持");
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
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        SystemThreadFactory(String poolName) {
            if (StrUtil.isEmpty(poolName)) {
                poolName = "pool";
            }
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolName + "-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private static class ThreadPoolExecutorPool extends ThreadPoolExecutor {
        private int queueMaxSize = 0;

        ThreadPoolExecutorPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        void setQueueMaxSize(int queueMaxSize) {
            this.queueMaxSize = queueMaxSize;
        }

        @Override
        public void execute(Runnable command) {
            checkQueueSize();
            super.execute(command);
        }

        @Override
        public Future<?> submit(Runnable task) {
            checkQueueSize();
            return super.submit(task);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            checkQueueSize();
            return super.submit(task);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            checkQueueSize();
            return super.submit(task, result);
        }

        /**
         * 判断队列数最大值
         */
        private void checkQueueSize() {
            if (queueMaxSize > 0) {
                int queueSize = getQueue().size();
                if (queueSize > queueMaxSize) {
                    throw new RuntimeException("queue size :" + queueSize + "  >" + queueMaxSize);
                }
            }
        }
    }
}
