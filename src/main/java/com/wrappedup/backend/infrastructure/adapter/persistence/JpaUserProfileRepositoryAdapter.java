package com.wrappedup.backend.infrastructure.adapter.persistence;

import com.wrappedup.backend.domain.model.*;
import com.wrappedup.backend.domain.port.out.UserProfileRepository;
import com.wrappedup.backend.domain.port.out.UserRepository;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.UserProfileJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.repository.UserJpaRepository;
import com.wrappedup.backend.infrastructure.adapter.persistence.repository.UserProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementation of the UserProfileRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class JpaUserProfileRepositoryAdapter implements UserProfileRepository {
    
    private final UserProfileJpaRepository userProfileJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserRepository userRepository;
    
    @Override
    public UserProfile save(UserProfile userProfile) {
        UserProfileJpaEntity entity = mapToJpaEntity(userProfile);
        UserProfileJpaEntity savedEntity = userProfileJpaRepository.save(entity);
        return mapToDomainEntity(savedEntity);
    }
    
    @Override
    public Optional<UserProfile> findById(UserProfileId id) {
        return userProfileJpaRepository.findById(id.getValue())
                .map(this::mapToDomainEntity);
    }
    
    @Override
    public Optional<UserProfile> findByUserId(UserId userId) {
        return userProfileJpaRepository.findByUserId(userId.getValue())
                .map(this::mapToDomainEntity);
    }
    
    @Override
    public Optional<UserProfile> findByUsername(Username username) {
        return userProfileJpaRepository.findByUserUsername(username.getValue())
                .map(this::mapToDomainEntity);
    }
    
    @Override
    public boolean existsByUserId(UserId userId) {
        return userProfileJpaRepository.existsByUserId(userId.getValue());
    }
    
    @Override
    public void deleteById(UserProfileId id) {
        userProfileJpaRepository.deleteById(id.getValue());
    }
    
    /**
     * Maps a domain UserProfile entity to a JPA entity.
     */
    private UserProfileJpaEntity mapToJpaEntity(UserProfile userProfile) {
        UserProfileJpaEntity entity = new UserProfileJpaEntity();
        
        // If profile has an ID, set it, otherwise let JPA generate one
        if (userProfile.getId() != null) {
            entity.setId(userProfile.getId().getValue());
        }
        
        // Set the user
        Optional<UserJpaEntity> userEntity = userJpaRepository.findById(userProfile.getUserId().getValue());
        userEntity.ifPresent(entity::setUser);
        
        // Set profile fields
        entity.setFullName(userProfile.getFullName());
        entity.setBio(userProfile.getBio());
        entity.setUserImageUrl(userProfile.getUserImageUrl());
        entity.setFavoriteGenres(userProfile.getFavoriteGenres());
        entity.setReadingGoal(userProfile.getReadingGoal());
        entity.setPreferredLanguage(userProfile.getPreferredLanguage());
        entity.setPublicProfile(userProfile.isPublicProfile());
        entity.setSocialLinks(userProfile.getSocialLinks());
        entity.setLocation(userProfile.getLocation());
        
        // Set timestamps if they exist in the domain entity
        if (userProfile.getCreatedAt() != null) {
            entity.setCreatedAt(userProfile.getCreatedAt());
        }
        
        if (userProfile.getUpdatedAt() != null) {
            entity.setUpdatedAt(userProfile.getUpdatedAt());
        }
        
        return entity;
    }
    
    /**
     * Maps a JPA entity to a domain UserProfile entity.
     */
    private UserProfile mapToDomainEntity(UserProfileJpaEntity entity) {
        // Find the user for this profile
        Optional<User> user = userRepository.findById(UserId.of(entity.getUser().getId()));
        
        if (user.isEmpty()) {
            throw new IllegalStateException("UserProfile exists but User not found");
        }
        
        return UserProfile.reconstitute(
                UserProfileId.of(entity.getId()),
                user.get().getId(),
                entity.getFullName(),
                entity.getBio(),
                entity.getUserImageUrl(),
                entity.getFavoriteGenres(),
                entity.getReadingGoal(),
                entity.getPreferredLanguage(),
                entity.isPublicProfile(),
                entity.getSocialLinks(),
                entity.getLocation(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
} 