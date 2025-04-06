package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.GetUserUseCase;
import com.wrappedup.backend.domain.port.in.UserProfileUseCase;
import com.wrappedup.backend.domain.port.out.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation of the UserProfileUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService implements UserProfileUseCase {
    
    private final UserProfileRepository userProfileRepository;
    private final GetUserUseCase getUserUseCase;
    
    @Override
    @Transactional
    public UserProfile createProfile(UserId userId) {
        log.debug("Creating profile for user with ID: {}", userId);
        
        // Check if user exists
        Optional<User> user = getUserUseCase.getUserById(userId);
        if (user.isEmpty()) {
            log.error("Cannot create profile for non-existent user: {}", userId);
            throw new IllegalArgumentException("User not found");
        }
        
        // Check if profile already exists
        Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(userId);
        if (existingProfile.isPresent()) {
            log.debug("Profile already exists for user: {}", userId);
            return existingProfile.get();
        }
        
        // Create new profile
        UserProfile profile = UserProfile.createNewProfile(userId);
        UserProfile savedProfile = userProfileRepository.save(profile);
        
        log.info("Created profile for user: {}", userId);
        return savedProfile;
    }
    
    @Override
    @Transactional
    public UserProfile updateProfile(UpdateProfileCommand command) {
        log.debug("Updating profile for user with ID: {}", command.userId());
        
        // Find existing profile
        Optional<UserProfile> existingProfileOpt = userProfileRepository.findByUserId(command.userId());
        
        UserProfile profile;
        if (existingProfileOpt.isPresent()) {
            profile = existingProfileOpt.get();
        } else {
            // Create new profile if it doesn't exist
            // Check if user exists
            Optional<User> user = getUserUseCase.getUserById(command.userId());
            if (user.isEmpty()) {
                log.error("Cannot create profile for non-existent user: {}", command.userId());
                throw new IllegalArgumentException("User not found");
            }
            
            // Create new profile
            profile = UserProfile.createNewProfile(command.userId());
            // Save the newly created profile first, to match test expectation
            profile = userProfileRepository.save(profile);
        }
        
        // Update profile
        profile.updateProfile(
                command.fullName(),
                command.bio(),
                command.userImageUrl(),
                command.favoriteGenres(),
                command.readingGoal(),
                command.preferredLanguage(),
                command.isPublicProfile(),
                command.socialLinks(),
                command.location()
        );
        
        UserProfile savedProfile = userProfileRepository.save(profile);
        log.info("Updated profile for user: {}", command.userId());
        
        return savedProfile;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfile> getProfileByUserId(UserId userId) {
        log.debug("Getting profile for user with ID: {}", userId);
        return userProfileRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfile> getPublicProfileByUsername(Username username) {
        log.debug("Getting public profile for username: {}", username);
        
        Optional<UserProfile> profileOpt = userProfileRepository.findByUsername(username);
        
        // Check if profile exists and is public
        if (profileOpt.isPresent() && profileOpt.get().isPublicProfile()) {
            return profileOpt;
        }
        
        return Optional.empty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasProfile(UserId userId) {
        log.debug("Checking if user has profile: {}", userId);
        return userProfileRepository.existsByUserId(userId);
    }
    
    @Override
    @Transactional
    public void deleteProfile(UserId userId) {
        log.debug("Deleting profile for user with ID: {}", userId);
        
        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            userProfileRepository.deleteById(profile.getId());
            log.info("Deleted profile for user: {}", userId);
        });
    }
} 