package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Port for review persistence operations.
 */
public interface ReviewRepository {
    /**
     * Save a review.
     */
    Review save(Review review);
    
    /**
     * Find a review by ID.
     */
    Optional<Review> findById(ReviewId id);
    
    /**
     * Find all reviews by user ID.
     */
    List<Review> findByUserId(UserId userId);
    
    /**
     * Find a review by user ID and book ID.
     */
    Optional<Review> findByUserIdAndBookId(UserId userId, BookId bookId);
    
    /**
     * Find all public reviews for a book.
     */
    List<Review> findPublicReviewsByBookId(BookId bookId);
    
    /**
     * Delete a review by its ID.
     */
    void deleteById(ReviewId id);
} 