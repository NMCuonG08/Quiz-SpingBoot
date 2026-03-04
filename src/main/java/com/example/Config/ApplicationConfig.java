package com.example.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Application Configuration for Performance Optimization
 * Following Single Responsibility Principle
 */
@Configuration
@EnableCaching
@EnableTransactionManagement
public class ApplicationConfig implements WebMvcConfigurer {

    /**
     * Cache Manager for performance optimization
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(java.util.Arrays.asList(
                "employees",
                "departments",
                "leaveRequests",
                "employeesByDepartment",
                "managersList"));
        return cacheManager;
    }
}
