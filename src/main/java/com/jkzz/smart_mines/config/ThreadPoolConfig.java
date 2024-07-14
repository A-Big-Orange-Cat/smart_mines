package com.jkzz.smart_mines.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置类
 */
@Data
@EnableScheduling
@EnableAsync
@Configuration
public class ThreadPoolConfig implements SchedulingConfigurer {

    /**
     * 线程的名称前缀
     */
    private static final String THREAD_PREFIX = "comm-t-";
    /**
     * 线程池中的核心线程数量,默认为1
     */
    @Value("${spring.task.execution.pool.core-size}")
    private int corePoolSize;
    /**
     * 线程池中允许线程的空闲时间,默认为 60s
     */
    private int keepAliveTime = ((int) TimeUnit.SECONDS.toSeconds(30));
    /**
     * 线程池中的最大线程数量
     */
    @Value("${spring.task.execution.pool.max-size}")
    private int maxPoolSize;
    /**
     * 线程池中的队列最大数量
     */
    @Value("${spring.task.execution.pool.queue-capacity}")
    private int queueCapacity;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
    }

    @Bean(name = "commThreadPoolExecutor")
    public TaskScheduler taskScheduler() {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

        //taskScheduler.setCorePoolSize(corePoolSize);
        taskScheduler.setPoolSize(maxPoolSize);
        //executor.setKeepAliveSeconds(keepAliveTime);
        //taskScheduler.setQueueCapacity(queueCapacity);
        taskScheduler.setThreadNamePrefix(THREAD_PREFIX);
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskScheduler.setDaemon(true);

        taskScheduler.initialize();

        return taskScheduler;
    }

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize * 2); // 核心线程数
        executor.setMaxPoolSize(maxPoolSize * 2); // 最大线程数
        executor.setQueueCapacity(queueCapacity); // 队列大小
        executor.setKeepAliveSeconds(keepAliveTime); // 线程空闲时间
        executor.setThreadNamePrefix("hik-t-"); // 线程名前缀
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略
        executor.setDaemon(true);

        executor.initialize();
        return executor;
    }

}
