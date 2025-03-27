package com.wrappedup.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class UserProfileDto {
    // Basic Information
    @NotBlank
    private String username;
    
    @Email
    @NotBlank
    private String email;
    
    @Size(max = 100)
    private String fullName;
    
    @Size(max = 500)
    private String bio;
    
    private String userImageUrl;
    
    // Reading Preferences
    private List<String> favoriteGenres;
    
    private Integer readingGoal;
    
    private String preferredLanguage;
    
    // Social Features
    @JsonProperty("isPublicProfile")
    private boolean publicProfile;
    
    private Map<String, String> socialLinks;
    
    private String location;
    
    // Timestamps
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Add this method for backward compatibility
    public boolean isPublicProfile() {
        return publicProfile;
    }
} 