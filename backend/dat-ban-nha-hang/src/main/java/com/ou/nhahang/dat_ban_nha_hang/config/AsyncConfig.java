package com.ou.nhahang.dat_ban_nha_hang.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 5 nhân viên làm việc thường trực
        executor.setMaxPoolSize(10); // Tối đa 10 nhân viên khi quá tải
        executor.setQueueCapacity(500); // Hàng đợi chờ tối đa 500 thông báo
        executor.setThreadNamePrefix("Noti-Async-");
        executor.initialize();
        return executor;
    }
}