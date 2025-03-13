package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Review;
import com.wrappedup.backend.domain.port.ReviewRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetUserReviewsUseCase {
    private final ReviewRepository reviewRepository;

    public GetUserReviewsUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<Review> execute(UUID userId) {
        return reviewRepository.findByUserId(userId);
    }

    public Optional<Review> findById(UUID id) {
        return reviewRepository.findById(id);
    }
} 