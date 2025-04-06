package com.wrappedup.backend.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.retry.annotation.EnableRetry;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RetryConfigTest {

    @Test
    @DisplayName("Should be annotated with EnableRetry")
    void retryConfig_ShouldBeAnnotatedWithEnableRetry() {
        // Act & Assert
        assertTrue(RetryConfig.class.isAnnotationPresent(EnableRetry.class));
    }
} 