package com.wrappedup.backend.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Configuration to enable Spring Retry functionality.
 */
@Configuration
@EnableRetry
public class RetryConfig {
    // No additional configuration required - just enabling retry
} 