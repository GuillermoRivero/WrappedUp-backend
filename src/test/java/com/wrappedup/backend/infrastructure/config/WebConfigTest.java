package com.wrappedup.backend.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebConfigTest {

    @Test
    @DisplayName("Should create RestTemplate")
    void restTemplate_ShouldReturnRestTemplate() {
        // Arrange
        WebConfig webConfig = new WebConfig();
        
        // Act
        RestTemplate restTemplate = webConfig.restTemplate();
        
        // Assert
        assertNotNull(restTemplate);
        assertEquals(RestTemplate.class, restTemplate.getClass());
    }
} 