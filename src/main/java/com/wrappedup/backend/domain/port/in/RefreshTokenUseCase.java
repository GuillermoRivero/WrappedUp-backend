package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.User;

/**
 * Input port for refreshing authentication tokens.
 */
public interface RefreshTokenUseCase {
    /**
     * Command for refreshing a token.
     */
    record RefreshTokenCommand(String refreshToken) {
        public RefreshTokenCommand {
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("Refresh token cannot be null or blank");
            }
        }
    }
    
    /**
     * Result of token refresh.
     */
    record RefreshTokenResult(User user, String accessToken, String refreshToken) {
        public RefreshTokenResult {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }
            if (accessToken == null || accessToken.isBlank()) {
                throw new IllegalArgumentException("Access token cannot be null or blank");
            }
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new IllegalArgumentException("Refresh token cannot be null or blank");
            }
        }
    }
    
    /**
     * Refresh an authentication token.
     * @param command The refresh token command
     * @return The refresh result with user and new tokens
     * @throws IllegalArgumentException if the refresh token is invalid or expired
     */
    RefreshTokenResult refreshToken(RefreshTokenCommand command) throws IllegalArgumentException;
} 