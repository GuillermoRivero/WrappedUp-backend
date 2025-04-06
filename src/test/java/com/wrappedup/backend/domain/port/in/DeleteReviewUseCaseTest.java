package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.ReviewId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.verify;

class DeleteReviewUseCaseTest {

    @Test
    @DisplayName("Should call deleteReview with the provided ReviewId")
    void deleteReview_ShouldCallWithProvidedReviewId() {
        // Create a mock implementation of the interface
        DeleteReviewUseCase useCase = Mockito.mock(DeleteReviewUseCase.class);
        
        // Create a test ReviewId
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        
        // Call the method
        useCase.deleteReview(reviewId);
        
        // Verify the method was called with the correct ReviewId
        verify(useCase).deleteReview(reviewId);
    }
} 