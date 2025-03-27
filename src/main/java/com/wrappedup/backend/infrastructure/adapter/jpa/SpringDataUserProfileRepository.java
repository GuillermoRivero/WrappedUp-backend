package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUserId(UUID userId);
    Optional<UserProfile> findByUserUsername(String username);
    boolean existsByUserId(UUID userId);
} 