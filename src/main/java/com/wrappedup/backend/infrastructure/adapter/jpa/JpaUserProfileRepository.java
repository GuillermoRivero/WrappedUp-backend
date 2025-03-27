package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.UserProfile;
import com.wrappedup.backend.domain.port.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaUserProfileRepository implements UserProfileRepository {
    
    private final SpringDataUserProfileRepository springDataUserProfileRepository;

    @Override
    public UserProfile save(UserProfile profile) {
        return springDataUserProfileRepository.save(profile);
    }

    @Override
    public Optional<UserProfile> findById(UUID id) {
        return springDataUserProfileRepository.findById(id);
    }

    @Override
    public Optional<UserProfile> findByUserId(UUID userId) {
        return springDataUserProfileRepository.findByUserId(userId);
    }

    @Override
    public Optional<UserProfile> findByUserUsername(String username) {
        return springDataUserProfileRepository.findByUserUsername(username);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return springDataUserProfileRepository.existsByUserId(userId);
    }

    @Override
    public void deleteById(UUID id) {
        springDataUserProfileRepository.deleteById(id);
    }
} 