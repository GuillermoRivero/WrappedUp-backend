package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.User;

import java.util.Optional;

/**
 * Output port for JWT token operations.
 */
public interface JwtTokenPort {
    /**
     * Generates a JWT access token for a user.
     *
     * @param user The user for whom to generate the token
     * @return The generated JWT access token
     */
    String generateAccessToken(User user);

    /**
     * Generates a JWT refresh token for a user.
     *
     * @param user The user for whom to generate the refresh token
     * @return The generated JWT refresh token
     */
    String generateRefreshToken(User user);

    /**
     * Validates a JWT token and extracts the user ID if valid.
     *
     * @param token The JWT token to validate
     * @return Optional containing the user ID if token is valid, empty otherwise
     */
    Optional<String> validateTokenAndGetUserId(String token);

    /**
     * Checks if a token is expired.
     *
     * @param token The JWT token to check
     * @return true if the token is expired, false otherwise
     */
    boolean isTokenExpired(String token);
} 