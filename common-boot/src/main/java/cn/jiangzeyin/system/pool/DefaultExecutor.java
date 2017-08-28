package cn.jiangzeyin.system.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jiangzeyin
 * Created by jiangzeyin on 2017/2/14.
 */
public class DefaultExecutor implements Executor {
    private static final ThreadPoolExecutor EXECUTOR = SystemExecutorService.newCachedThreadPool(DefaultExecutor.class);

    public static final DefaultExecutor DEFAULT_EXECUTOR = new DefaultExecutor();

    @Override
    public void execute(Runnable command) {
        EXECUTOR.execute(command);
    }
}
