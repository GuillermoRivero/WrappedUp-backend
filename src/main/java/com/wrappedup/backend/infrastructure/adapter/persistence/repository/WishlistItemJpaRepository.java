package com.wrappedup.backend.infrastructure.adapter.persistence.repository;

import com.wrappedup.backend.infrastructure.adapter.persistence.entity.WishlistItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistItemJpaRepository extends JpaRepository<WishlistItemJpaEntity, UUID> {

    List<WishlistItemJpaEntity> findAllByUserId(UUID userId);
    
    Optional<WishlistItemJpaEntity> findByUserIdAndBookId(UUID userId, UUID bookId);
    
    boolean existsByUserIdAndBookId(UUID userId, UUID bookId);
} 