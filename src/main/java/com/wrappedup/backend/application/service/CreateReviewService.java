package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.port.in.CreateReviewUseCase;
import com.wrappedup.backend.domain.port.out.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation of the CreateReviewUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateReviewService implements CreateReviewUseCase {

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public ReviewId createReview(CreateReviewCommand command) {
        log.info("Creating review for user {} and book {}", command.userId(), command.bookId());
        
        // Check if user has already reviewed this book
        Optional<Review> existingReview = reviewRepository.findByUserIdAndBookId(command.userId(), command.bookId());
        
        if (existingReview.isPresent()) {
            log.info("User has already reviewed this book, updating existing review");
            
            Review reviewToUpdate = existingReview.get();
            reviewToUpdate.updateReview(
                    command.rating(),
                    command.content(),
                    command.startDate(),
                    command.endDate(),
                    command.isPublic()
            );
            
            Review updatedReview = reviewRepository.save(reviewToUpdate);
            log.info("Review updated with ID: {}", updatedReview.getId());
            
            return updatedReview.getId();
        }
        
        // Create new review
        Review newReview = Review.createNewReview(
                command.userId(),
                command.bookId(),
                command.rating(),
                command.content(),
                command.startDate(),
                command.endDate(),
                command.isPublic()
        );
        
        Review savedReview = reviewRepository.save(newReview);
        log.info("New review created with ID: {}", savedReview.getId());
        
        return savedReview.getId();
    }
} 