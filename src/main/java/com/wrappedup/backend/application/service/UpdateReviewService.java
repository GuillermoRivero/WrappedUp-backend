package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.port.in.UpdateReviewUseCase;
import com.wrappedup.backend.domain.port.out.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of the UpdateReviewUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateReviewService implements UpdateReviewUseCase {

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public Review updateReview(UpdateReviewCommand command) {
        log.info("Updating review with ID: {}", command.id());
        
        // Find the review
        Review review = reviewRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + command.id()));
        
        // Update review details
        review.updateReview(
                command.rating() != null ? command.rating() : review.getRating(),
                command.content(),
                command.startDate(),
                command.endDate(),
                command.isPublic()
        );
        
        // Save updated review
        Review updatedReview = reviewRepository.save(review);
        log.info("Review updated successfully: {}", updatedReview.getId());
        
        return updatedReview;
    }
} 