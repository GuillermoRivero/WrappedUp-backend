package com.wrappedup.backend.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JpaConfigTest {

    @Test
    @DisplayName("Should create hibernate properties with correct values")
    void hibernateProperties_ShouldReturnPropertiesWithCorrectValues() {
        // Arrange
        JpaConfig jpaConfig = new JpaConfig();
        
        // Act
        Properties properties = jpaConfig.hibernateProperties();
        
        // Assert
        assertNotNull(properties);
        assertEquals("allow", properties.getProperty("hibernate.event.merge.entity_copy_observer"));
        assertEquals("30", properties.getProperty("hibernate.jdbc.batch_size"));
        assertEquals("true", properties.getProperty("hibernate.order_inserts"));
        assertEquals("true", properties.getProperty("hibernate.order_updates"));
    }
} 