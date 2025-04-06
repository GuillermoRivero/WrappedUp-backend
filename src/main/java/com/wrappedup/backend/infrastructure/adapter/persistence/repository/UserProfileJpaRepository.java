package com.wrappedup.backend.infrastructure.adapter.persistence.repository;

import com.wrappedup.backend.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.UserProfileJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserProfileJpaEntity.
 */
@Repository
public interface UserProfileJpaRepository extends JpaRepository<UserProfileJpaEntity, UUID> {
    Optional<UserProfileJpaEntity> findByUser(UserJpaEntity user);
    Optional<UserProfileJpaEntity> findByUserId(UUID userId);
    Optional<UserProfileJpaEntity> findByUserUsername(String username);
    boolean existsByUserId(UUID userId);
} 