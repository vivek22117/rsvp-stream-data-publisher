package com.dd.position.simulator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class ApplicationConfig {

    private static final int MAX_POOL_SIZE = 2;
    private static final int CORE_POOL_SIZE = 2;
    private static final int QUEUE_CAPACITY = 500;


    @Bean
    public Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Bean
    public ThreadPoolTaskExecutor createExecutorService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(MAX_POOL_SIZE);
        executor.setMaxPoolSize(CORE_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix("JDAsync-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "ConcurrentTaskExecutor")
    public TaskExecutor taskExecutor2() {
        return new ConcurrentTaskExecutor(
                Executors.newFixedThreadPool(3));
    }
}
