package com.wrappedup.backend.infrastructure.adapter.security;

import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.port.out.JwtTokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Adapter implementation of the JwtTokenPort using JJWT library.
 */
@Component
@Slf4j
public class JwtTokenAdapter implements JwtTokenPort {
    
    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
    
    public JwtTokenAdapter(
            @Value("${jwt.secret:}") String configuredSecret,
            @Value("${jwt.accessToken.expiration:900000}") long accessTokenExpirationMs,
            @Value("${jwt.refreshToken.expiration:2592000000}") long refreshTokenExpirationMs) {
        
        // Initialize the secretKey field 
        SecretKey keyToUse;
        
        // Generate a secure key for HS512 if none is configured or it's too short
        if (configuredSecret == null || configuredSecret.isEmpty() || 
            configuredSecret.getBytes().length * 8 < 512) {
            log.warn("JWT secret is not configured or not secure enough for HS512. Generating a secure key.");
            keyToUse = Keys.secretKeyFor(signatureAlgorithm);
        } else {
            try {
                // Try to use the configured secret, padding if needed
                byte[] keyBytes = Base64.getDecoder().decode(configuredSecret);
                
                // If key is too small, generate a new one
                if (keyBytes.length * 8 < 512) {
                    keyToUse = Keys.secretKeyFor(signatureAlgorithm);
                    log.warn("Configured JWT key is too small for HS512. Generated a secure key instead.");
                } else {
                    keyToUse = Keys.hmacShaKeyFor(keyBytes);
                    log.info("Using configured JWT secret key.");
                }
            } catch (Exception e) {
                log.warn("Error decoding JWT secret. Generating a secure key: {}", e.getMessage());
                keyToUse = Keys.secretKeyFor(signatureAlgorithm);
            }
        }
        
        this.secretKey = keyToUse;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }
    
    @Override
    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpirationMs);
    }
    
    @Override
    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpirationMs);
    }
    
    @Override
    public Optional<String> validateTokenAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // Get the userId from claims - should be stored in "userId" claim
            String userId = claims.get("userId", String.class);
            if (userId == null) {
                log.warn("No userId claim found in token");
                return Optional.empty();
            }
            
            return Optional.of(userId);
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.debug("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
    
    private String generateToken(User user, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);
        
        // Ensure the UUID is in the standard format
        String userId = user.getId().getValue().toString();
        
        return Jwts.builder()
                // Use email as the subject for compatibility with JwtAuthenticationFilter
                .setSubject(user.getEmail().getValue())
                // Store the user ID as a claim for future reference
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("username", user.getUsername().getValue())
                .claim("role", user.getRole().name())
                .signWith(secretKey, signatureAlgorithm)
                .compact();
    }
} 