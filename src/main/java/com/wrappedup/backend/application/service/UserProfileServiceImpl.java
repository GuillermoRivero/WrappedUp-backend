package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.UserProfile;
import com.wrappedup.backend.domain.port.UserProfileRepository;
import com.wrappedup.backend.domain.port.UserRepository;
import com.wrappedup.backend.domain.port.UserProfileService;
import com.wrappedup.backend.dto.UpdateUserProfileDto;
import com.wrappedup.backend.dto.UserProfileDto;
import com.wrappedup.backend.mapper.UserProfileMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {
    
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    
    @Override
    @Transactional
    public UserProfileDto updateProfile(UUID userId, UpdateUserProfileDto profileDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
            
        // Get existing profile or create a new one - use orElseGet instead of orElse
        // to ensure the new UserProfile is only created if needed
        UserProfile profile = userProfileRepository.findByUserId(userId)
            .orElseGet(() -> new UserProfile(user));
            
        // Update profile fields
        if (profileDto.getFullName() != null) {
            profile.setFullName(profileDto.getFullName());
        }
        if (profileDto.getBio() != null) {
            profile.setBio(profileDto.getBio());
        }
        if (profileDto.getUserImageUrl() != null) {
            profile.setUserImageUrl(profileDto.getUserImageUrl());
        }
        if (profileDto.getFavoriteGenres() != null) {
            profile.setFavoriteGenres(profileDto.getFavoriteGenres());
        }
        if (profileDto.getReadingGoal() != null) {
            profile.setReadingGoal(profileDto.getReadingGoal());
        }
        if (profileDto.getPreferredLanguage() != null) {
            profile.setPreferredLanguage(profileDto.getPreferredLanguage());
        }
        profile.setPublicProfile(profileDto.isPublicProfile());
        if (profileDto.getSocialLinks() != null) {
            profile.setSocialLinks(profileDto.getSocialLinks());
        }
        if (profileDto.getLocation() != null) {
            profile.setLocation(profileDto.getLocation());
        }
        
        // Save the profile once
        UserProfile savedProfile = userProfileRepository.save(profile);
        return userProfileMapper.toDto(savedProfile);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileDto> getProfile(UUID userId) {
        return userProfileRepository.findByUserId(userId)
            .map(userProfileMapper::toDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<UserProfileDto> getPublicProfile(String username) {
        log.debug("Looking for public profile for username: {}", username);
        
        // First try to find the user by username
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.debug("User not found with username: {}", username);
            return Optional.empty();
        }
        
        // Then try to find their profile
        Optional<UserProfile> profile = userProfileRepository.findByUserId(user.get().getId());
        if (profile.isEmpty()) {
            log.debug("No profile found for user: {}", username);
            return Optional.empty();
        }
        
        // Check if profile is public
        if (!profile.get().isPublicProfile()) {
            log.debug("Profile for user {} is private", username);
            return Optional.empty();
        }
        
        log.debug("Public profile found for username: {}", username);
        return Optional.of(userProfileMapper.toDto(profile.get()));
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        return userProfileRepository.existsByUserId(userId);
    }
} 