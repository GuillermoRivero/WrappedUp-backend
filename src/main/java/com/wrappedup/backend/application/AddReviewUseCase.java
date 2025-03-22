package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Review;
import com.wrappedup.backend.domain.port.ReviewRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public class AddReviewUseCase {
    private final ReviewRepository reviewRepository;

    public AddReviewUseCase(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review execute(Review review) {
        Optional<Review> existingReview = reviewRepository.findByUserIdAndBookId(
            review.getUserId(), review.getBook().getId());
        
        if (existingReview.isPresent()) {
            Review reviewToUpdate = existingReview.get();
            reviewToUpdate.setText(review.getText());
            reviewToUpdate.setRating(review.getRating());
            reviewToUpdate.setStartDate(review.getStartDate());
            reviewToUpdate.setEndDate(review.getEndDate());
            return reviewRepository.save(reviewToUpdate);
        }
        
        return reviewRepository.save(review);
    }
} 