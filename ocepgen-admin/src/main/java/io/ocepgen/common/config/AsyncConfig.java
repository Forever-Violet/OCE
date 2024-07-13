package io.ocepgen.common.config;

import io.ocepgen.common.exception.CustomAsyncExceptionHandler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 实现AsyncConfigurer接口，配置自定义的线程池
 * @author roxy
 *
 * 注：IO密集型（某大厂实践经验）
 *        核心线程数 = CPU核数 / （1-阻塞系数）
 * 或着
 * CPU密集型：核心线程数 = CPU核数 + 1
 * IO密集型：核心线程数 = CPU核数 * 2
 * CPU密集型 可以理解为 就是处理繁杂算法的操作，对硬盘等操作不是很频繁，比如一个算法非常之复杂，可能要处理半天，而最终插入到数据库的时间很快。
 * IO密集型 可以理解为 简单的业务逻辑处理，比如计算1+1=2，但是要处理的数据很多，每一条都要去插入数据库，对数据库频繁操作。
 */
@EnableAsync(proxyTargetClass = true) //开启异步任务支持
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    //获取当前机器的核数
    public static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    @Bean(name = "ocepgenAsyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncOperationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(CPU_NUM); //核心线程数，默认1
        executor.setMaxPoolSize(CPU_NUM * 2); //最大线程池容量，默认 Integer.MAX_VALUE
        executor.setQueueCapacity(1000); //任务队列容量，默认 Integer.MAX_VALUE
        executor.setKeepAliveSeconds(60); //非核心线程活跃时间，没有任务会被回收，默认60，设置executor.setAllowCoreThreadTimeOut(true); 核心线程也会被回收
        // 拒绝策略，当任务队列满了并且线程数达到最大容量，如何处理新任务。这里采用的是，直接返回给调用者线程执行任务，如果执行器已经关闭，那么丢弃任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程名前缀 + 分组名称
        executor.setThreadNamePrefix("ocepgenAsyncTaskThread-");
        executor.setThreadGroupName("ocepgenAsyncTaskGroup");
        // 所有任务结束后关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }
}
