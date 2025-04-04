package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Use case for retrieving reviews.
 */
public interface GetReviewUseCase {

    /**
     * Gets a review by its ID.
     *
     * @param id The ID of the review
     * @return The review, if found
     */
    Optional<Review> getReviewById(ReviewId id);

    /**
     * Gets all reviews created by a user.
     *
     * @param userId The ID of the user
     * @return The list of reviews
     */
    List<Review> getReviewsByUserId(UserId userId);

    /**
     * Gets a review by user ID and book ID.
     *
     * @param userId The ID of the user
     * @param bookId The ID of the book
     * @return The review, if found
     */
    Optional<Review> getReviewByUserIdAndBookId(UserId userId, BookId bookId);

    /**
     * Gets all public reviews for a book.
     *
     * @param bookId The ID of the book
     * @return The list of public reviews
     */
    List<Review> getPublicReviewsByBookId(BookId bookId);
} 