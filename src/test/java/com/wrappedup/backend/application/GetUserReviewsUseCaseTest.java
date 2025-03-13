package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.Review;
import com.wrappedup.backend.domain.port.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserReviewsUseCaseTest {

    private static final UUID TEST_USER_ID = UUID.fromString("f2295dea-c8cb-4493-ae1c-dbfebbfc4f3a");

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private GetUserReviewsUseCase getUserReviewsUseCase;

    @Test
    void shouldGetUserReviews() {
        // Given
        LocalDate now = LocalDate.now();
        Book book1 = new Book("Test Book 1", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        Book book2 = new Book("Test Book 2", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL789012W");
        List<Review> expectedReviews = Arrays.asList(
            new Review(TEST_USER_ID, book1, "Great book!", 5, now, now),
            new Review(TEST_USER_ID, book2, "Good book!", 4, now, now)
        );
        when(reviewRepository.findByUserId(TEST_USER_ID)).thenReturn(expectedReviews);

        // When
        List<Review> actualReviews = getUserReviewsUseCase.execute(TEST_USER_ID);

        // Then
        assertNotNull(actualReviews);
        assertEquals(2, actualReviews.size());
        verify(reviewRepository, times(1)).findByUserId(TEST_USER_ID);
        
        // Verify individual review properties
        for (int i = 0; i < expectedReviews.size(); i++) {
            Review expected = expectedReviews.get(i);
            Review actual = actualReviews.get(i);
            assertEquals(expected.getUserId(), actual.getUserId());
            assertEquals(expected.getBook().getTitle(), actual.getBook().getTitle());
            assertEquals(expected.getText(), actual.getText());
            assertEquals(expected.getRating(), actual.getRating());
            assertEquals(expected.getStartDate(), actual.getStartDate());
            assertEquals(expected.getEndDate(), actual.getEndDate());
        }
    }

    @Test
    void shouldReturnEmptyListWhenNoReviews() {
        // Given
        when(reviewRepository.findByUserId(TEST_USER_ID)).thenReturn(List.of());

        // When
        List<Review> reviews = getUserReviewsUseCase.execute(TEST_USER_ID);

        // Then
        assertTrue(reviews.isEmpty());
        verify(reviewRepository, times(1)).findByUserId(TEST_USER_ID);
    }
} 