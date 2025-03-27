package com.wrappedup.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UpdateUserProfileDto {
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
    
    // Add this method for backward compatibility
    public boolean isPublicProfile() {
        return publicProfile;
    }
} 