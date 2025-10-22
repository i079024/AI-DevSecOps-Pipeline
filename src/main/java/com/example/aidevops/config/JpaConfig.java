package com.example.aidevops.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class JpaConfig {

    /**
     * Additional JPA properties to suppress JTA warnings and optimize performance
     * This specifically addresses the HHH000489 warning about JTA platform
     */
    @Bean
    public Properties jpaProperties() {
        Properties properties = new Properties();
        
        // JTA Configuration - explicitly set to none to suppress the warning
        properties.setProperty("hibernate.transaction.jta.platform", "none");
        
        // Transaction type - use RESOURCE_LOCAL instead of JTA
        properties.setProperty("hibernate.transaction.coordinator_class", 
            "org.hibernate.resource.transaction.backend.jdbc.internal.JdbcResourceLocalTransactionCoordinatorBuilder");
        
        // Connection and performance optimizations
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "false");
        properties.setProperty("hibernate.connection.autocommit", "false");
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        
        // Batch processing optimizations
        properties.setProperty("hibernate.jdbc.batch_size", "25");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        
        // Cache configuration
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");
        
        // Logging and monitoring
        properties.setProperty("hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS", "50");
        properties.setProperty("hibernate.generate_statistics", "false");
        
        // Additional optimizations for H2 database
        properties.setProperty("hibernate.connection.acquisition_timeout", "10000");
        properties.setProperty("hibernate.connection.validation_timeout", "3000");
        
        return properties;
    }

    /**
     * Configure JPA Vendor Adapter with Hibernate-specific settings
     */
    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setShowSql(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        return adapter;
    }
}