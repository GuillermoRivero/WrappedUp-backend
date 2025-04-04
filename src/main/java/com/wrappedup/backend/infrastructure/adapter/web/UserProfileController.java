package com.wrappedup.backend.infrastructure.adapter.web;

import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.UserProfileUseCase;
import com.wrappedup.backend.infrastructure.adapter.security.DomainUserDetailsService.DomainUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for user profile operations.
 */
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {
    
    private final UserProfileUseCase userProfileUseCase;
    
    /**
     * DTO for user profile requests.
     */
    record UpdateProfileRequest(
            String fullName,
            String bio,
            String userImageUrl,
            List<String> favoriteGenres,
            Integer readingGoal,
            String preferredLanguage,
            Boolean isPublicProfile,
            Map<String, String> socialLinks,
            String location
    ) {}
    
    /**
     * DTO for user profile responses.
     */
    record UserProfileResponse(
            String id,
            String userId,
            String fullName,
            String bio,
            String userImageUrl,
            List<String> favoriteGenres,
            Integer readingGoal,
            String preferredLanguage,
            boolean isPublicProfile,
            Map<String, String> socialLinks,
            String location,
            String createdAt,
            String updatedAt
    ) {}
    
    /**
     * Update the authenticated user's profile.
     */
    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        try {
            Optional<UserId> userIdOpt = extractUserIdFromAuthentication();
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.status(401).build(); // Unauthorized
            }
            
            UserId userId = userIdOpt.get();
            
            // Create command for updating profile
            var command = new UserProfileUseCase.UpdateProfileCommand(
                    userId,
                    request.fullName(),
                    request.bio(),
                    request.userImageUrl(),
                    request.favoriteGenres(),
                    request.readingGoal(),
                    request.preferredLanguage(),
                    request.isPublicProfile(),
                    request.socialLinks(),
                    request.location()
            );
            
            // Update profile
            UserProfile updatedProfile = userProfileUseCase.updateProfile(command);
            
            return ResponseEntity.ok(mapToResponse(updatedProfile));
        } catch (IllegalArgumentException e) {
            log.error("Failed to update profile", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get the authenticated user's profile.
     */
    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile() {
        try {
            Optional<UserId> userIdOpt = extractUserIdFromAuthentication();
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.status(401).build(); // Unauthorized
            }
            
            UserId userId = userIdOpt.get();
            
            // Get profile
            Optional<UserProfile> profile = userProfileUseCase.getProfileByUserId(userId);
            
            if (profile.isPresent()) {
                return ResponseEntity.ok(mapToResponse(profile.get()));
            } else {
                // If profile doesn't exist, create it
                UserProfile newProfile = userProfileUseCase.createProfile(userId);
                return ResponseEntity.ok(mapToResponse(newProfile));
            }
        } catch (Exception e) {
            log.error("Failed to get profile: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * Alternative endpoint for getting the current user's profile.
     * Some frontend implementations might use this URL pattern.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
        return getProfile();
    }
    
    /**
     * Get a public profile by username.
     */
    @GetMapping("/public/{username}")
    public ResponseEntity<UserProfileResponse> getPublicProfile(@PathVariable String username) {
        try {
            // Create username value object
            Username usernameObj = new Username(username);
            
            // Get public profile
            Optional<UserProfile> profile = userProfileUseCase.getPublicProfileByUsername(usernameObj);
            
            return profile
                    .map(p -> ResponseEntity.ok(mapToResponse(p)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Failed to get public profile", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete the authenticated user's profile.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteProfile() {
        try {
            Optional<UserId> userIdOpt = extractUserIdFromAuthentication();
            if (userIdOpt.isEmpty()) {
                return ResponseEntity.status(401).build(); // Unauthorized
            }
            
            UserId userId = userIdOpt.get();
            
            // Delete profile
            userProfileUseCase.deleteProfile(userId);
            
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete profile", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Extract the user ID from the authentication context.
     * Handles various types of principal objects.
     * 
     * @return Optional containing the user ID if authentication is valid, empty otherwise
     */
    private Optional<UserId> extractUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        Object principal = authentication.getPrincipal();
        
        try {
            if (principal instanceof DomainUserDetails) {
                return Optional.of(((DomainUserDetails) principal).getUser().getId());
            } else if (principal instanceof User) {
                return Optional.of(((User) principal).getId());
            } else {
                log.warn("Unexpected principal type: {}", principal.getClass().getName());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error extracting user ID from principal: {}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Map domain UserProfile to UserProfileResponse DTO.
     */
    private UserProfileResponse mapToResponse(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId().toString(),
                profile.getUserId().toString(),
                profile.getFullName(),
                profile.getBio(),
                profile.getUserImageUrl(),
                profile.getFavoriteGenres(),
                profile.getReadingGoal(),
                profile.getPreferredLanguage(),
                profile.isPublicProfile(),
                profile.getSocialLinks(),
                profile.getLocation(),
                profile.getCreatedAt().toString(),
                profile.getUpdatedAt().toString()
        );
    }
} 