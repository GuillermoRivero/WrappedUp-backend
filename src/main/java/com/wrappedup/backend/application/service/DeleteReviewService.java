package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.port.in.DeleteReviewUseCase;
import com.wrappedup.backend.domain.port.out.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of the DeleteReviewUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteReviewService implements DeleteReviewUseCase {

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public void deleteReview(ReviewId id) {
        log.info("Deleting review with ID: {}", id);
        
        // Check if review exists
        if (!reviewRepository.findById(id).isPresent()) {
            log.warn("Review not found: {}", id);
            return; // Silently ignore if not found
        }
        
        // Delete review
        reviewRepository.deleteById(id);
        
        log.info("Review deleted: {}", id);
    }
} 