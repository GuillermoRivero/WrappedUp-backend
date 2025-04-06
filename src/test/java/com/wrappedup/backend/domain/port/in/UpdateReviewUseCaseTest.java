package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.port.in.UpdateReviewUseCase.UpdateReviewCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateReviewUseCaseTest {

    @Test
    @DisplayName("Should throw exception when id is null in UpdateReviewCommand")
    void updateReviewCommand_WithNullId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateReviewCommand(
                null, 
                4, 
                "Updated content", 
                LocalDate.now().minusDays(10), 
                LocalDate.now(), 
                true
            )
        );
    }
    
    @Test
    @DisplayName("Should throw exception when rating is less than 1 in UpdateReviewCommand")
    void updateReviewCommand_WithRatingLessThanOne_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateReviewCommand(
                ReviewId.fromUUID(UUID.randomUUID()),
                0,
                "Updated content", 
                LocalDate.now().minusDays(10), 
                LocalDate.now(), 
                true
            )
        );
    }
    
    @Test
    @DisplayName("Should throw exception when rating is greater than 5 in UpdateReviewCommand")
    void updateReviewCommand_WithRatingGreaterThanFive_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateReviewCommand(
                ReviewId.fromUUID(UUID.randomUUID()),
                6,
                "Updated content", 
                LocalDate.now().minusDays(10), 
                LocalDate.now(), 
                true
            )
        );
    }
    
    @Test
    @DisplayName("Should create UpdateReviewCommand with valid parameters")
    void updateReviewCommand_WithValidParameters_ShouldCreateInstance() {
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        Integer rating = 4;
        String content = "Updated content";
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();
        Boolean isPublic = true;
        
        UpdateReviewCommand command = new UpdateReviewCommand(
            reviewId, rating, content, startDate, endDate, isPublic
        );
        
        assertNotNull(command);
        assertEquals(reviewId, command.id());
        assertEquals(rating, command.rating());
        assertEquals(content, command.content());
        assertEquals(startDate, command.startDate());
        assertEquals(endDate, command.endDate());
        assertEquals(isPublic, command.isPublic());
    }
    
    @Test
    @DisplayName("Should create UpdateReviewCommand with minimum required parameters")
    void updateReviewCommand_WithMinimumRequiredParameters_ShouldCreateInstance() {
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        
        UpdateReviewCommand command = new UpdateReviewCommand(
            reviewId, null, null, null, null, null
        );
        
        assertNotNull(command);
        assertEquals(reviewId, command.id());
        assertNull(command.rating());
        assertNull(command.content());
        assertNull(command.startDate());
        assertNull(command.endDate());
        assertNull(command.isPublic());
    }
    
    @Test
    @DisplayName("Should allow null rating in UpdateReviewCommand")
    void updateReviewCommand_WithNullRating_ShouldCreateInstance() {
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        
        UpdateReviewCommand command = new UpdateReviewCommand(
            reviewId, null, "Updated content", null, null, true
        );
        
        assertNotNull(command);
        assertNull(command.rating());
    }
    
    @Test
    @DisplayName("Should call updateReview with the provided command")
    void updateReview_ShouldCallWithProvidedCommand() {
        // Create a mock implementation of the interface
        UpdateReviewUseCase useCase = Mockito.mock(UpdateReviewUseCase.class);
        
        // Create a test command
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        UpdateReviewCommand command = new UpdateReviewCommand(
            reviewId, 4, "Updated content", LocalDate.now().minusDays(10), 
            LocalDate.now(), true
        );
        
        // Mock a review response
        Review mockReview = Mockito.mock(Review.class);
        when(useCase.updateReview(command)).thenReturn(mockReview);
        
        // Call the method
        Review result = useCase.updateReview(command);
        
        // Verify the method was called with the correct command
        verify(useCase).updateReview(command);
        
        // Verify the result
        assertEquals(mockReview, result);
    }
} 