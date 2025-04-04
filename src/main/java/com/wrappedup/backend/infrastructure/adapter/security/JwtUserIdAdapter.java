package com.wrappedup.backend.infrastructure.adapter.security;

import com.wrappedup.backend.domain.port.out.JwtTokenPort;
import com.wrappedup.backend.domain.port.out.UserIdPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter that extracts user IDs from JWT tokens using the JwtTokenPort.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtUserIdAdapter implements UserIdPort {

    private final JwtTokenPort jwtTokenPort;

    @Override
    public UUID extractUserId(String token) {
        try {
            return jwtTokenPort.validateTokenAndGetUserId(token)
                    .map(userIdStr -> {
                        try {
                            return UUID.fromString(userIdStr);
                        } catch (IllegalArgumentException e) {
                            log.error("Invalid UUID format in token: {}", userIdStr);
                            throw new IllegalArgumentException("Invalid ID format. IDs must be valid UUIDs.");
                        }
                    })
                    .orElseThrow(() -> {
                        log.error("Failed to extract user ID from token");
                        return new IllegalArgumentException("Invalid token");
                    });
        } catch (IllegalArgumentException e) {
            log.error("Error extracting user ID: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error extracting user ID: {}", e.getMessage());
            throw new IllegalArgumentException("Could not process token");
        }
    }
} 