package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.exception.UserAlreadyExistsException;
import com.wrappedup.backend.domain.model.UserId;

/**
 * Input port for user registration operations.
 * This defines the interface for creating new users.
 */
public interface RegisterUserUseCase {
    /**
     * Command for registering a new user.
     */
    record RegisterUserCommand(String username, String email, String password) {
        public RegisterUserCommand {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Username must not be null or blank");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email must not be null or blank");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password must not be null or blank");
            }
        }
    }
    
    /**
     * Register a new user with the system.
     * @param command The registration command with user details
     * @return The ID of the newly registered user
     * @throws UserAlreadyExistsException If the username or email is already taken
     */
    UserId registerUser(RegisterUserCommand command) throws UserAlreadyExistsException;
} 