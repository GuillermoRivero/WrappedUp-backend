package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Review;
import com.wrappedup.backend.domain.port.ReviewRepository;

public class AddReviewUseCase {
    private final ReviewRepository reviewRepository;

    public AddReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review execute(Review review) {
        return reviewRepository.save(review);
    }
} 