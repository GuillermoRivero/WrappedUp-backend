package com.wrappedup.backend.domain.port.out;

import java.util.UUID;

/**
 * Port for extracting user IDs from tokens.
 */
public interface UserIdPort {
    
    /**
     * Extracts a user ID from a JWT token.
     *
     * @param token the JWT token
     * @return the user ID
     */
    UUID extractUserId(String token);
} 