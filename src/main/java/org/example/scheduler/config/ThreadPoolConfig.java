package org.example.scheduler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService executorService() {
        // Creates a fixed thread pool with 3 threads
        return Executors.newFixedThreadPool(3);
    }
}
