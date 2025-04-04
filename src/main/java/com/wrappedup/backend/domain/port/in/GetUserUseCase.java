package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;

import java.util.Optional;

/**
 * Input port for retrieving user information.
 */
public interface GetUserUseCase {
    /**
     * Get a user by their ID.
     * @param id The user ID
     * @return The user if found, or empty if not found
     */
    Optional<User> getUserById(UserId id);
    
    /**
     * Get a user by their username.
     * @param username The username
     * @return The user if found, or empty if not found
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * Get a user by their email.
     * @param email The email
     * @return The user if found, or empty if not found
     */
    Optional<User> getUserByEmail(String email);
} 