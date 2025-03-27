package com.wrappedup.backend.domain.port;

import com.wrappedup.backend.domain.UserProfile;
import com.wrappedup.backend.dto.UpdateUserProfileDto;
import com.wrappedup.backend.dto.UserProfileDto;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileService {
    UserProfileDto updateProfile(UUID userId, UpdateUserProfileDto profileDto);
    Optional<UserProfileDto> getProfile(UUID userId);
    Optional<UserProfileDto> getPublicProfile(String username);
    boolean existsByUserId(UUID userId);
} 