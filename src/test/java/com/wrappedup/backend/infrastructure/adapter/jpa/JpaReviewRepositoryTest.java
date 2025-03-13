package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.Review;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaReviewRepositoryTest {

    @Mock
    private SpringDataReviewRepository springDataReviewRepository;

    @InjectMocks
    private JpaReviewRepository jpaReviewRepository;

    @Test
    void shouldSaveReview() {
        // Given
        UUID userId = UUID.randomUUID();
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        Review review = new Review(userId, book, "Great book!", 5, LocalDate.now(), LocalDate.now());
        when(springDataReviewRepository.save(any(Review.class))).thenReturn(review);

        // When
        Review savedReview = jpaReviewRepository.save(review);

        // Then
        assertEquals(review, savedReview);
        verify(springDataReviewRepository, times(1)).save(review);
    }

    @Test
    void shouldFindReviewsByUserId() {
        // Given
        UUID userId = UUID.randomUUID();
        Book book1 = new Book("Test Book 1", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        Book book2 = new Book("Test Book 2", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL789012W");
        List<Review> expectedReviews = Arrays.asList(
            new Review(userId, book1, "Great book!", 5, LocalDate.now(), LocalDate.now()),
            new Review(userId, book2, "Another great book!", 4, LocalDate.now(), LocalDate.now())
        );
        when(springDataReviewRepository.findByUserId(userId)).thenReturn(expectedReviews);

        // When
        List<Review> actualReviews = jpaReviewRepository.findByUserId(userId);

        // Then
        assertEquals(expectedReviews, actualReviews);
        assertEquals(2, actualReviews.size());
        verify(springDataReviewRepository, times(1)).findByUserId(userId);
    }

    @Test
    void shouldFindReviewById() {
        // Given
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        Review expectedReview = new Review(userId, book, "Great book!", 5, LocalDate.now(), LocalDate.now());
        when(springDataReviewRepository.findById(reviewId)).thenReturn(Optional.of(expectedReview));

        // When
        Optional<Review> actualReview = jpaReviewRepository.findById(reviewId);

        // Then
        assertTrue(actualReview.isPresent());
        assertEquals(expectedReview, actualReview.get());
        verify(springDataReviewRepository, times(1)).findById(reviewId);
    }
} 