package com.wrappedup.backend.infrastructure.adapter.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenAdapter jwtTokenAdapter;
    private final UserDetailsService userDetailsService;
    
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestURI = request.getRequestURI();
        final String authHeader = request.getHeader("Authorization");
        
        log.debug("Processing request to URI: {}", requestURI);
        
        // Skip authentication if no Bearer token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No Bearer token found in request to {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract and validate the token
            final String jwt = authHeader.substring(7);
            // Only log a portion of the token for security
            final String tokenPreview = jwt.length() > 20 ? jwt.substring(0, 20) + "..." : jwt;
            log.debug("JWT token found in request to {}, token length: {}", requestURI, jwt.length());
            
            // Get the user email from the subject of the token
            final String email = extractEmailFromToken(jwt);
            
            if (email == null) {
                log.warn("Failed to extract email from token for URI: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }
            
            log.debug("Extracted email from token: {}", email);
            
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("Authentication already set in SecurityContext for URI: {}, user: {}", 
                        requestURI, email);
                filterChain.doFilter(request, response);
                return;
            }
            
            // Load user details and validate token
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.debug("Loaded user details for email: {}", email);
                
                boolean isTokenValid = !jwtTokenAdapter.isTokenExpired(jwt);
                
                if (isTokenValid) {
                    log.debug("Token is valid for user: {}", email);
                    
                    // Set authentication in context
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authentication set in SecurityContext for user: {}", email);
                } else {
                    log.warn("Token validation failed for user: {}, URI: {}", email, requestURI);
                }
            } catch (Exception e) {
                log.error("Error loading user details for email: {}, URI: {}, Error: {}", 
                        email, requestURI, e.getMessage());
            }
            
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error processing JWT token for URI {}: {}", requestURI, e.getMessage());
            filterChain.doFilter(request, response);
        }
    }
    
    private String extractEmailFromToken(String token) {
        try {
            // Extract the email from the token subject
            return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        } catch (Exception e) {
            log.error("Error extracting email from token: {}", e.getMessage());
            return null;
        }
    }
} 