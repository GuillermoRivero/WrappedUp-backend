package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.Review;
import com.wrappedup.backend.domain.port.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaReviewRepository implements ReviewRepository {
    
    private final SpringDataReviewRepository springDataReviewRepository;

    @Override
    public Review save(Review review) {
        return springDataReviewRepository.save(review);
    }

    @Override
    public List<Review> findByUserId(UUID userId) {
        return springDataReviewRepository.findByUserId(userId);
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return springDataReviewRepository.findById(id);
    }
} 