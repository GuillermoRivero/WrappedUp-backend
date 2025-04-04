package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;

import java.time.LocalDate;

/**
 * Use case for updating an existing review.
 */
public interface UpdateReviewUseCase {

    /**
     * Command for updating a review.
     */
    record UpdateReviewCommand(
            ReviewId id,
            Integer rating,
            String content,
            LocalDate startDate,
            LocalDate endDate,
            Boolean isPublic
    ) {
        public UpdateReviewCommand {
            if (id == null) {
                throw new IllegalArgumentException("Review ID cannot be null");
            }
            if (rating != null && (rating < 1 || rating > 5)) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
        }
    }

    /**
     * Updates an existing review.
     *
     * @param command The command containing the review updates
     * @return The updated review
     * @throws IllegalArgumentException if the review does not exist
     */
    Review updateReview(UpdateReviewCommand command);
} 