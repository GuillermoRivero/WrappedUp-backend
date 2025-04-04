package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;

import java.time.LocalDate;

/**
 * Use case for creating a new review.
 */
public interface CreateReviewUseCase {

    /**
     * Command for creating a new review.
     */
    record CreateReviewCommand(
            UserId userId,
            BookId bookId,
            int rating,
            String content,
            LocalDate startDate,
            LocalDate endDate,
            boolean isPublic
    ) {
        public CreateReviewCommand {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (bookId == null) {
                throw new IllegalArgumentException("Book ID cannot be null");
            }
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
        }
    }

    /**
     * Creates a new review or updates an existing one if the user has already reviewed the book.
     *
     * @param command The command containing the review details
     * @return The ID of the created or updated review
     */
    ReviewId createReview(CreateReviewCommand command);
} 