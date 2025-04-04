package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.User;

/**
 * Input port for user authentication.
 */
public interface AuthenticateUserUseCase {
    /**
     * Command for authenticating a user.
     */
    record AuthenticationCommand(String email, String password) {
        public AuthenticationCommand {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email cannot be null or blank");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password cannot be null or blank");
            }
        }
    }
    
    /**
     * Result of authentication.
     */
    record AuthenticationResult(User user, String accessToken, String refreshToken) {
        public AuthenticationResult {
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
     * Authenticate a user with email and password.
     * @param command The authentication command with credentials
     * @return The authentication result with user and tokens
     * @throws IllegalArgumentException if authentication fails
     */
    AuthenticationResult authenticate(AuthenticationCommand command) throws IllegalArgumentException;
} 