package org.example.scheduler.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "jobExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);         // minimum number of threads
        executor.setMaxPoolSize(5);          // maximum number of threads
        executor.setQueueCapacity(10);       // queue size for extra tasks
        executor.setThreadNamePrefix("JobThread-");
        executor.setKeepAliveSeconds(30);    // thread idle timeout
        executor.initialize();
        return executor;
    }
}
