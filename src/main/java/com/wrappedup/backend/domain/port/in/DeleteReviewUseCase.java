package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.ReviewId;

/**
 * Use case for deleting a review.
 */
public interface DeleteReviewUseCase {

    /**
     * Deletes a review by its ID.
     *
     * @param id The ID of the review to delete
     */
    void deleteReview(ReviewId id);
} 