package com.wrappedup.backend.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorsFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CorsFilter corsFilter;

    @BeforeEach
    void setUp() {
        // Set default values for properties
        ReflectionTestUtils.setField(corsFilter, "allowedOriginsString", "http://localhost:8080,http://localhost:3000");
        ReflectionTestUtils.setField(corsFilter, "allowedMethodsString", "GET,POST,PUT,DELETE");
        ReflectionTestUtils.setField(corsFilter, "allowedHeadersString", "Authorization,Content-Type");
        ReflectionTestUtils.setField(corsFilter, "exposedHeadersString", "Authorization,Content-Type");
        ReflectionTestUtils.setField(corsFilter, "MAX_AGE", 7200L);
        
        // Initialize lists
        corsFilter.init();
    }

    @Test
    @DisplayName("Should have HIGHEST_PRECEDENCE order")
    void corsFilter_ShouldHaveHighestPrecedenceOrder() {
        // Act & Assert
        assertTrue(CorsFilter.class.isAnnotationPresent(Order.class));
        Order orderAnnotation = CorsFilter.class.getAnnotation(Order.class);
        assertEquals(Ordered.HIGHEST_PRECEDENCE, orderAnnotation.value());
    }

    @Test
    @DisplayName("Should initialize properties correctly")
    void init_ShouldInitializePropertiesCorrectly() {
        // Act - already called in setUp()
        
        // Assert
        List<String> allowedOrigins = (List<String>) ReflectionTestUtils.getField(corsFilter, "ALLOWED_ORIGINS");
        List<String> allowedMethods = (List<String>) ReflectionTestUtils.getField(corsFilter, "ALLOWED_METHODS");
        List<String> allowedHeaders = (List<String>) ReflectionTestUtils.getField(corsFilter, "ALLOWED_HEADERS");
        List<String> exposedHeaders = (List<String>) ReflectionTestUtils.getField(corsFilter, "EXPOSED_HEADERS");
        
        assertEquals(Arrays.asList("http://localhost:8080", "http://localhost:3000"), allowedOrigins);
        assertEquals(Arrays.asList("GET", "POST", "PUT", "DELETE"), allowedMethods);
        assertEquals(Arrays.asList("Authorization", "Content-Type"), allowedHeaders);
        assertEquals(Arrays.asList("Authorization", "Content-Type"), exposedHeaders);
    }

    @Test
    @DisplayName("Should set CORS headers for allowed origin")
    void doFilter_ShouldSetCorsHeadersForAllowedOrigin() throws ServletException, IOException {
        // Arrange
        String origin = "http://localhost:3000";
        when(request.getHeader("Origin")).thenReturn(origin);
        when(request.getMethod()).thenReturn("GET");
        
        // Act
        corsFilter.doFilter(request, response, filterChain);
        
        // Assert
        verify(response).setHeader("Access-Control-Allow-Origin", origin);
        verify(response).setHeader("Access-Control-Allow-Credentials", "true");
        verify(response).setHeader(eq("Access-Control-Allow-Methods"), anyString());
        verify(response).setHeader(eq("Access-Control-Allow-Headers"), anyString());
        verify(response).setHeader(eq("Access-Control-Expose-Headers"), anyString());
        verify(response).setHeader("Access-Control-Max-Age", "7200");
        verify(response).setHeader("Vary", "Origin");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should respond with 200 OK for OPTIONS request")
    void doFilter_ShouldRespondWith200ForOptionsRequest() throws ServletException, IOException {
        // Arrange
        String origin = "http://localhost:3000";
        when(request.getHeader("Origin")).thenReturn(origin);
        when(request.getMethod()).thenReturn("OPTIONS");
        
        // Act
        corsFilter.doFilter(request, response, filterChain);
        
        // Assert
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    @DisplayName("Should handle null origin")
    void doFilter_ShouldHandleNullOrigin() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Origin")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        
        // Act
        corsFilter.doFilter(request, response, filterChain);
        
        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle mobile requests")
    void doFilter_ShouldHandleMobileRequests() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Origin")).thenReturn(null);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)");
        when(request.getMethod()).thenReturn("GET");
        
        // Act
        corsFilter.doFilter(request, response, filterChain);
        
        // Assert
        verify(filterChain).doFilter(request, response);
    }
} 