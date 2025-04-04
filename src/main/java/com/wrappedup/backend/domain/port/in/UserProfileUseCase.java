package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.Username;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Input port for user profile operations.
 */
public interface UserProfileUseCase {
    /**
     * Command for updating a user profile.
     */
    record UpdateProfileCommand(
            UserId userId,
            String fullName,
            String bio,
            String userImageUrl,
            List<String> favoriteGenres,
            Integer readingGoal,
            String preferredLanguage,
            Boolean isPublicProfile,
            Map<String, String> socialLinks,
            String location) {
        
        public UpdateProfileCommand {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
        }
    }
    
    /**
     * Create a new profile for a user.
     * If the user already has a profile, returns the existing profile.
     * 
     * @param userId The ID of the user
     * @return The created or existing user profile
     */
    UserProfile createProfile(UserId userId);
    
    /**
     * Update a user's profile.
     * If the user does not have a profile yet, one will be created.
     * 
     * @param command The update profile command
     * @return The updated user profile
     */
    UserProfile updateProfile(UpdateProfileCommand command);
    
    /**
     * Get a user's profile by user ID.
     * 
     * @param userId The ID of the user
     * @return The user profile if found
     */
    Optional<UserProfile> getProfileByUserId(UserId userId);
    
    /**
     * Get a user's profile by username.
     * If the profile is private, returns empty.
     * 
     * @param username The username of the user
     * @return The public user profile if found and public
     */
    Optional<UserProfile> getPublicProfileByUsername(Username username);
    
    /**
     * Check if a user has a profile.
     * 
     * @param userId The ID of the user
     * @return True if the user has a profile, false otherwise
     */
    boolean hasProfile(UserId userId);
    
    /**
     * Delete a user's profile.
     * 
     * @param userId The ID of the user
     */
    void deleteProfile(UserId userId);
} 