package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.Review;
import com.wrappedup.backend.domain.port.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddReviewUseCaseTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AddReviewUseCase addReviewUseCase;

    @Test
    void shouldAddReview() {
        // Given
        UUID userId = UUID.randomUUID();
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        Review review = new Review(userId, book, "Great book!", 5, null, null);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // When
        Review savedReview = addReviewUseCase.execute(review);

        // Then
        assertEquals(review, savedReview);
        verify(reviewRepository, times(1)).save(review);
    }
} 