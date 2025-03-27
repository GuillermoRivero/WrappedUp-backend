package com.wrappedup.backend.mapper;

import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.UserProfile;
import com.wrappedup.backend.dto.UpdateUserProfileDto;
import com.wrappedup.backend.dto.UserProfileDto;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {
    
    public UserProfileDto toDto(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        
        UserProfileDto dto = new UserProfileDto();
        User user = profile.getUser();
        
        // Basic Information
        dto.setUsername(user.getRealUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(profile.getFullName());
        dto.setBio(profile.getBio());
        dto.setUserImageUrl(profile.getUserImageUrl());
        
        // Reading Preferences
        dto.setFavoriteGenres(profile.getFavoriteGenres());
        dto.setReadingGoal(profile.getReadingGoal());
        dto.setPreferredLanguage(profile.getPreferredLanguage());
        
        // Social Features
        dto.setPublicProfile(profile.isPublicProfile());
        dto.setSocialLinks(profile.getSocialLinks());
        dto.setLocation(profile.getLocation());
        
        // Timestamps
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        
        return dto;
    }
    
    public UserProfile toEntity(UserProfileDto dto, User user) {
        if (dto == null) {
            return null;
        }
        
        UserProfile profile = new UserProfile(user);
        
        // Basic Information
        profile.setFullName(dto.getFullName());
        profile.setBio(dto.getBio());
        profile.setUserImageUrl(dto.getUserImageUrl());
        
        // Reading Preferences
        profile.setFavoriteGenres(dto.getFavoriteGenres());
        profile.setReadingGoal(dto.getReadingGoal());
        profile.setPreferredLanguage(dto.getPreferredLanguage());
        
        // Social Features
        profile.setPublicProfile(dto.isPublicProfile());
        profile.setSocialLinks(dto.getSocialLinks());
        profile.setLocation(dto.getLocation());
        
        return profile;
    }
    
    public UserProfile toEntity(UpdateUserProfileDto dto, User user) {
        if (dto == null) {
            return null;
        }
        
        UserProfile profile = new UserProfile(user);
        
        // Basic Information
        profile.setFullName(dto.getFullName());
        profile.setBio(dto.getBio());
        profile.setUserImageUrl(dto.getUserImageUrl());
        
        // Reading Preferences
        profile.setFavoriteGenres(dto.getFavoriteGenres());
        profile.setReadingGoal(dto.getReadingGoal());
        profile.setPreferredLanguage(dto.getPreferredLanguage());
        
        // Social Features
        profile.setPublicProfile(dto.isPublicProfile());
        profile.setSocialLinks(dto.getSocialLinks());
        profile.setLocation(dto.getLocation());
        
        return profile;
    }
} 