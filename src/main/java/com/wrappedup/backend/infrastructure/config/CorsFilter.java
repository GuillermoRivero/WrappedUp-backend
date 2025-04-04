package com.wrappedup.backend.infrastructure.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);
    
    @Value("${cors.allowed-origins:http://localhost:8080,http://localhost:3000,https://wrappedupdev.duckdns.org,http://wrappedupdev.duckdns.org}")
    private String allowedOriginsString;
    
    private List<String> ALLOWED_ORIGINS;
    
    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH,HEAD}")
    private String allowedMethodsString;
    
    private List<String> ALLOWED_METHODS;
    
    @Value("${cors.allowed-headers:Authorization,Content-Type,Accept,Origin,X-Requested-With,Access-Control-Request-Method,Access-Control-Request-Headers,Cache-Control,User-Agent,Accept-Encoding,Accept-Language,Referer,Connection,X-XSRF-TOKEN,X-CSRF-TOKEN}")
    private String allowedHeadersString;
    
    private List<String> ALLOWED_HEADERS;
    
    @Value("${cors.exposed-headers:Authorization,Content-Type,Accept,Origin,Access-Control-Allow-Origin,Access-Control-Allow-Credentials,Access-Control-Allow-Headers,Access-Control-Allow-Methods,X-Total-Count,Content-Disposition}")
    private String exposedHeadersString;
    
    private List<String> EXPOSED_HEADERS;
    
    @Value("${cors.max-age:7200}")
    private long MAX_AGE;
    
    @jakarta.annotation.PostConstruct
    public void init() {
        ALLOWED_ORIGINS = Arrays.asList(allowedOriginsString.split(","));
        ALLOWED_METHODS = Arrays.asList(allowedMethodsString.split(","));
        ALLOWED_HEADERS = Arrays.asList(allowedHeadersString.split(","));
        EXPOSED_HEADERS = Arrays.asList(exposedHeadersString.split(","));
        
        logger.info("CORS Filter configured with the following origins: {}", ALLOWED_ORIGINS);
        logger.info("CORS Filter configured with the following methods: {}", ALLOWED_METHODS);
        logger.info("CORS Filter configured with the following headers: {}", ALLOWED_HEADERS);
        logger.info("CORS Filter configured with the following exposed headers: {}", EXPOSED_HEADERS);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String requestURI = request.getRequestURI();
        
        logger.debug("CORS Filter processing request: {} {} from origin: {}", method, requestURI, origin);
        logger.debug("Request headers: Host={}, User-Agent={}", request.getHeader("Host"), userAgent);
        
        boolean isMobileRequest = userAgent != null && 
            (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone"));

        // For development environment, allow all origins
        boolean isDevelopment = true; // Set to true for development mode
        
        if (origin == null && isMobileRequest) {
            String referer = request.getHeader("Referer");
            logger.debug("Mobile request with null origin. Referer: {}", referer);
            
            if (referer != null) {
                origin = referer;
                logger.debug("Using referer as origin: {}", origin);
            } else {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Credentials", "false");
                logger.debug("Set CORS headers for null origin mobile request without referer");
            }
        } else if (origin != null) {
            boolean isAllowed = ALLOWED_ORIGINS.contains(origin) || 
                               (isMobileRequest && (origin.startsWith("capacitor://") || origin.startsWith("ionic://"))) ||
                               isDevelopment;
            
            logger.debug("Origin: {} is {} in allowed list: {}", 
                        origin, 
                        isAllowed ? "present" : "not present", 
                        ALLOWED_ORIGINS);
            
            if (isAllowed) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                logger.debug("Set Access-Control-Allow-Origin: {}", origin);
            } else {
                logger.warn("Origin: {} is not allowed", origin);
            }
            
            String allowedMethodsStr = String.join(", ", ALLOWED_METHODS);
            response.setHeader("Access-Control-Allow-Methods", allowedMethodsStr);
            
            String allowedHeadersStr = String.join(", ", ALLOWED_HEADERS);
            response.setHeader("Access-Control-Allow-Headers", allowedHeadersStr);
            
            String exposedHeadersStr = String.join(", ", EXPOSED_HEADERS);
            response.setHeader("Access-Control-Expose-Headers", exposedHeadersStr);
            
            response.setHeader("Access-Control-Max-Age", String.valueOf(MAX_AGE));
            
            // Always add Vary header
            response.setHeader("Vary", "Origin");
            
            if (isMobileRequest) {
                response.setHeader("Timing-Allow-Origin", origin);
            }
        } else {
            logger.warn("No Origin header in request to: {}", requestURI);
            // For development, set permissive CORS headers even for null origin
            if (isDevelopment) {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Credentials", "true");
                
                String allowedMethodsStr = String.join(", ", ALLOWED_METHODS);
                response.setHeader("Access-Control-Allow-Methods", allowedMethodsStr);
                
                String allowedHeadersStr = String.join(", ", ALLOWED_HEADERS);
                response.setHeader("Access-Control-Allow-Headers", allowedHeadersStr);
                
                String exposedHeadersStr = String.join(", ", EXPOSED_HEADERS);
                response.setHeader("Access-Control-Expose-Headers", exposedHeadersStr);
                
                response.setHeader("Access-Control-Max-Age", String.valueOf(MAX_AGE));
                logger.debug("Set permissive CORS headers for null origin request in development mode");
            }
        }

        if ("OPTIONS".equalsIgnoreCase(method)) {
            logger.debug("Responding OK to OPTIONS request");
            response.setStatus(HttpServletResponse.SC_OK);
            return; 
        } else {
            chain.doFilter(req, res);
        }
    }
} 