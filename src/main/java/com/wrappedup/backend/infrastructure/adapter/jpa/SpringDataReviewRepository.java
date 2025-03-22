package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByUserId(UUID userId);
    
    Optional<Review> findByUserIdAndBookId(UUID userId, UUID bookId);
} 