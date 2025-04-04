package com.wrappedup.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuration for JPA settings.
 */
@Configuration
@EnableTransactionManagement
public class JpaConfig {

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        
        // Disable version checks for detached entities (helps with the detached entity issue)
        hibernateProperties.setProperty("hibernate.event.merge.entity_copy_observer", "allow");
        
        // Other useful settings
        hibernateProperties.setProperty("hibernate.jdbc.batch_size", "30");
        hibernateProperties.setProperty("hibernate.order_inserts", "true");
        hibernateProperties.setProperty("hibernate.order_updates", "true");
        
        return hibernateProperties;
    }
} 