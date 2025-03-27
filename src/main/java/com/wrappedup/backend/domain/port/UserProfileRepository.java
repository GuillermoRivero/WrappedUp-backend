package com.wrappedup.backend.domain.port;

import com.wrappedup.backend.domain.UserProfile;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository {
    UserProfile save(UserProfile profile);
    Optional<UserProfile> findById(UUID id);
    Optional<UserProfile> findByUserId(UUID userId);
    Optional<UserProfile> findByUserUsername(String username);
    void deleteById(UUID id);
    boolean existsByUserId(UUID userId);
} 