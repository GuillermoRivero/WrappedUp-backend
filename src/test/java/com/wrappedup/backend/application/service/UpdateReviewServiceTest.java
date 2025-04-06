package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.UpdateReviewUseCase.UpdateReviewCommand;
import com.wrappedup.backend.domain.port.out.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private UpdateReviewService updateReviewService;

    @Captor
    private ArgumentCaptor<Review> reviewCaptor;

    private ReviewId reviewId;
    private UserId userId;
    private BookId bookId;
    private Review existingReview;
    private UpdateReviewCommand updateCommand;
    private final LocalDateTime now = LocalDateTime.now();
    private final LocalDate startDate = LocalDate.now().minusDays(5);
    private final LocalDate endDate = LocalDate.now();

    @BeforeEach
    void setUp() {
        reviewId = ReviewId.generate();
        userId = UserId.generate();
        bookId = BookId.generate();

        // Create existing review
        existingReview = Review.createNewReview(
                userId,
                bookId,
                4,
                "Original content",
                startDate,
                null,
                true
        );
        
        // Use reflection to set the ID since it's normally set by the repository
        ReflectionTestUtils.setField(existingReview, "id", reviewId);

        // Create update command
        updateCommand = new UpdateReviewCommand(
                reviewId,
                5,
                "Updated content",
                startDate,
                endDate,
                false
        );
    }

    @Test
    @DisplayName("Should update existing review with new values")
    void updateReview_WithValidCommand_ShouldUpdateReview() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Review updatedReview = updateReviewService.updateReview(updateCommand);

        // Assert
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).save(reviewCaptor.capture());

        Review capturedReview = reviewCaptor.getValue();
        assertNotNull(updatedReview);
        assertEquals(reviewId, updatedReview.getId());
        assertEquals(5, updatedReview.getRating());
        assertEquals("Updated content", updatedReview.getContent());
        assertEquals(startDate, updatedReview.getStartDate());
        assertEquals(endDate, updatedReview.getEndDate());
        assertFalse(updatedReview.isPublic());
    }

    @Test
    @DisplayName("Should throw exception when review is not found")
    void updateReview_WithNonExistingReview_ShouldThrowException() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateReviewService.updateReview(updateCommand)
        );

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).save(any());
        
        assertTrue(exception.getMessage().contains("Review not found"));
    }

    @Test
    @DisplayName("Should keep existing values when update command contains null values")
    void updateReview_WithPartialCommand_ShouldKeepExistingValues() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateReviewCommand partialCommand = new UpdateReviewCommand(
                reviewId,
                null,
                null,
                null,
                null,
                null
        );

        // Act
        Review updatedReview = updateReviewService.updateReview(partialCommand);

        // Assert
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).save(reviewCaptor.capture());

        Review capturedReview = reviewCaptor.getValue();
        assertNotNull(updatedReview);
        assertEquals(reviewId, updatedReview.getId());
        assertEquals(4, updatedReview.getRating()); // Original rating
        assertEquals("Original content", updatedReview.getContent()); // Original content
        assertEquals(startDate, updatedReview.getStartDate()); // Original start date
        assertNull(updatedReview.getEndDate()); // Original end date
        assertTrue(updatedReview.isPublic()); // Original public status
    }
} 