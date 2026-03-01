package com.example.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Database Configuration for JPA and Repository Setup
 * DataSource is auto-configured by Spring Boot from application.properties
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    // DataSource is auto-configured by Spring Boot
    // No need for custom DataSource beans
}
