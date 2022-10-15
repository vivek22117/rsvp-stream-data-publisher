package com.dd.position.simulator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class ApplicationConfig {

    @Value("${threadpool.corepoolsize}")
    int corePoolSize;

    @Value("${threadpool.maxpoolsize}")
    int maxPoolSize;

    @Value("${threadpool.queuesize}")
    int queueSize;


    @Bean
    public Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Bean (name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor createExecutorService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadNamePrefix("JDAsync-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "concurrentTaskExecutor")
    public TaskExecutor taskExecutor2() {
        return new ConcurrentTaskExecutor(
                Executors.newFixedThreadPool(3));
    }
}
