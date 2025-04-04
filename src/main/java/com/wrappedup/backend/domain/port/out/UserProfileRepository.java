package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.UserProfileId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;

import java.util.Optional;

/**
 * Output port for user profile repository operations.
 * This is an interface in the domain that is implemented by the infrastructure layer.
 */
public interface UserProfileRepository {
    /**
     * Save a user profile entity.
     * @param userProfile The user profile to save
     * @return The saved user profile
     */
    UserProfile save(UserProfile userProfile);
    
    /**
     * Find a user profile by its ID.
     * @param id The user profile ID
     * @return An optional containing the user profile if found
     */
    Optional<UserProfile> findById(UserProfileId id);
    
    /**
     * Find a user profile by the user ID.
     * @param userId The user ID
     * @return An optional containing the user profile if found
     */
    Optional<UserProfile> findByUserId(UserId userId);
    
    /**
     * Find a user profile by the user's username.
     * @param username The username
     * @return An optional containing the user profile if found
     */
    Optional<UserProfile> findByUsername(Username username);
    
    /**
     * Check if a user profile exists for a user.
     * @param userId The user ID to check
     * @return True if a profile exists for the user, false otherwise
     */
    boolean existsByUserId(UserId userId);
    
    /**
     * Delete a user profile by its ID.
     * @param id The user profile ID
     */
    void deleteById(UserProfileId id);
} 