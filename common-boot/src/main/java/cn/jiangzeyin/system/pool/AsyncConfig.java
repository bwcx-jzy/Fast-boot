package cn.jiangzeyin.system.pool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * Created by jiangzeyin on 2017/4/10.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    public static final String POOL_NAME = "service_threadPoolTaskExecutor";

    /**
     * 自定义线程池
     *
     * @return executor
     */
    @Bean(name = POOL_NAME)
    public Executor threadPoolTaskExecutor() {
        return SystemExecutorService.newCachedThreadPool(AsyncConfig.class);
    }
}
