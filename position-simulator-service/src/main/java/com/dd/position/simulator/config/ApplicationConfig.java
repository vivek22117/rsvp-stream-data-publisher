package com.dd.position.simulator.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

@Configuration
public class ApplicationConfig {

    private static final Logger logger = getLogger(ApplicationConfig.class);
    private static AWSCredentialsProvider awsCredentialsProvider;

    @Bean
    public Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

    @Bean
    public ThreadPoolTaskExecutor createExecutorService() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("JDAsync-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "ConcurrentTaskExecutor")
    public TaskExecutor taskExecutor2 () {
        return new ConcurrentTaskExecutor(
                Executors.newFixedThreadPool(3));
    }
}
