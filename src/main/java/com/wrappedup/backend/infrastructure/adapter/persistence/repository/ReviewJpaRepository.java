package com.wrappedup.backend.infrastructure.adapter.persistence.repository;

import com.wrappedup.backend.infrastructure.adapter.persistence.entity.ReviewJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for ReviewJpaEntity.
 */
@Repository
public interface ReviewJpaRepository extends JpaRepository<ReviewJpaEntity, UUID> {

    /**
     * Find all reviews by user ID.
     */
    List<ReviewJpaEntity> findByUserId(UUID userId);

    /**
     * Find a review by user ID and book ID.
     */
    Optional<ReviewJpaEntity> findByUserIdAndBookId(UUID userId, UUID bookId);

    /**
     * Find all public reviews by book ID.
     */
    List<ReviewJpaEntity> findByBookIdAndIsPublicTrue(UUID bookId);
} 