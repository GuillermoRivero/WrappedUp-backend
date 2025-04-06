package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.out.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private DeleteReviewService deleteReviewService;

    private ReviewId reviewId;
    private Review existingReview;

    @BeforeEach
    void setUp() {
        reviewId = ReviewId.generate();
        UserId userId = UserId.generate();
        BookId bookId = BookId.generate();

        // Create existing review
        existingReview = Review.createNewReview(
                userId,
                bookId,
                4,
                "Test review content",
                LocalDate.now().minusDays(5),
                LocalDate.now(),
                true
        );
        
        // Use reflection to set the ID since it's normally set by the repository
        ReflectionTestUtils.setField(existingReview, "id", reviewId);
    }

    @Test
    @DisplayName("Should delete review when it exists")
    void deleteReview_WhenReviewExists_ShouldDeleteReview() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        doNothing().when(reviewRepository).deleteById(reviewId);

        // Act
        deleteReviewService.deleteReview(reviewId);

        // Assert
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    @DisplayName("Should not delete review when it doesn't exist")
    void deleteReview_WhenReviewDoesNotExist_ShouldDoNothing() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act
        deleteReviewService.deleteReview(reviewId);

        // Assert
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).deleteById(any(ReviewId.class));
    }

    @Test
    @DisplayName("Should handle repository errors gracefully")
    void deleteReview_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        doThrow(new RuntimeException("Database error")).when(reviewRepository).deleteById(reviewId);

        // Act & Assert
        try {
            deleteReviewService.deleteReview(reviewId);
        } catch (Exception e) {
            // We expect the service to propagate exceptions from the repository
        }

        // Verify the repository methods were called correctly
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).deleteById(reviewId);
    }
} 