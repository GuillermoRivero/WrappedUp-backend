package com.wrappedup.backend.infrastructure.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://wrappedup.duckdns.org",
        "http://wrappedup.duckdns.org",
        "http://localhost:8080",
        "http://localhost:8081"
    );
    
    private static final List<String> ALLOWED_METHODS = Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
    );
    
    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
        "Authorization", 
        "Content-Type", 
        "Accept", 
        "Origin", 
        "X-Requested-With", 
        "Access-Control-Request-Method", 
        "Access-Control-Request-Headers",
        "Cache-Control",
        "User-Agent",
        "Accept-Encoding",
        "Accept-Language",
        "Referer",
        "Connection",
        "X-XSRF-TOKEN",
        "X-CSRF-TOKEN"
    );
    
    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
        "Authorization", 
        "Content-Type", 
        "Accept", 
        "Origin",
        "Access-Control-Allow-Origin",
        "Access-Control-Allow-Credentials",
        "Access-Control-Allow-Headers",
        "Access-Control-Allow-Methods",
        "X-Total-Count",
        "Content-Disposition"
    );
    
    private static final long MAX_AGE = 7200L; // Extended to 2 hours for mobile

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        
        
        boolean isMobileRequest = userAgent != null && 
            (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone"));

        if (origin == null && isMobileRequest) {
            String referer = request.getHeader("Referer");
            if (referer != null) {
                origin = referer;
            } else {
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Credentials", "false");
            }
        } else if (origin != null) {
            if (ALLOWED_ORIGINS.contains(origin) || (isMobileRequest && (origin.startsWith("capacitor://") || origin.startsWith("ionic://")))) {
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Credentials", "true");
            }
            
            String allowedMethodsStr = String.join(", ", ALLOWED_METHODS);
            response.setHeader("Access-Control-Allow-Methods", allowedMethodsStr);
            
            String allowedHeadersStr = String.join(", ", ALLOWED_HEADERS);
            response.setHeader("Access-Control-Allow-Headers", allowedHeadersStr);
            
            String exposedHeadersStr = String.join(", ", EXPOSED_HEADERS);
            response.setHeader("Access-Control-Expose-Headers", exposedHeadersStr);
            
            response.setHeader("Access-Control-Max-Age", String.valueOf(MAX_AGE));
            
            if (isMobileRequest) {
                response.setHeader("Vary", "Origin");
                response.setHeader("Timing-Allow-Origin", origin);
            }
        } else {
            logger.warn("No Origin header in request");
        }

        if ("OPTIONS".equalsIgnoreCase(method)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return; 
        } else {
            chain.doFilter(req, res);
        }
    }
} 