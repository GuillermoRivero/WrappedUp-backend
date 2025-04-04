package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;

import java.util.Optional;

/**
 * Output port for user repository operations.
 * This is an interface in the domain that is implemented by the infrastructure layer.
 */
public interface UserRepository {
    /**
     * Save a user entity.
     * @param user The user to save
     * @return The saved user
     */
    User save(User user);
    
    /**
     * Find a user by their ID.
     * @param id The user ID
     * @return An optional containing the user if found
     */
    Optional<User> findById(UserId id);
    
    /**
     * Find a user by their username.
     * @param username The username
     * @return An optional containing the user if found
     */
    Optional<User> findByUsername(Username username);
    
    /**
     * Find a user by their email.
     * @param email The email
     * @return An optional containing the user if found
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * Check if a username exists.
     * @param username The username to check
     * @return True if the username exists, false otherwise
     */
    boolean existsByUsername(Username username);
    
    /**
     * Check if an email exists.
     * @param email The email to check
     * @return True if the email exists, false otherwise
     */
    boolean existsByEmail(Email email);
} 