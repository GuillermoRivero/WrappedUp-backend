package com.wrappedup.backend.config;

import jakarta.persistence.EntityManager;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Configuración específica para tests
 * Proporciona mocks para componentes que podrían causar problemas en tests
 */
@Configuration
@Profile("test")
public class TestConfig {

    /**
     * Proporciona un mock de EntityManager para tests
     * Esto evita NPEs cuando se llama a flush() o refresh() en tests
     */
    @Bean
    @Primary
    public EntityManager entityManager() {
        EntityManager mockEntityManager = Mockito.mock(EntityManager.class);
        
        // Configurar comportamiento del mock si es necesario
        // Por ejemplo:
        // Mockito.doNothing().when(mockEntityManager).flush();
        
        return mockEntityManager;
    }
} 