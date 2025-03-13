package com.wrappedup.backend.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void shouldCreateReview() {
        // Given
        UUID userId = UUID.randomUUID();
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        String text = "Great book!";
        int rating = 5;
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        // When
        Review review = new Review(userId, book, text, rating, startDate, endDate);

        // Then
        assertEquals(userId, review.getUserId());
        assertEquals(book, review.getBook());
        assertEquals(text, review.getText());
        assertEquals(rating, review.getRating());
        assertEquals(startDate, review.getStartDate());
        assertEquals(endDate, review.getEndDate());
    }

    @Test
    void shouldCreateReviewWithoutDates() {
        // Given
        UUID userId = UUID.randomUUID();
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        String text = "Great book!";
        int rating = 5;

        // When
        Review review = new Review(userId, book, text, rating, null, null);

        // Then
        assertEquals(userId, review.getUserId());
        assertEquals(book, review.getBook());
        assertEquals(text, review.getText());
        assertEquals(rating, review.getRating());
        assertNull(review.getStartDate());
        assertNull(review.getEndDate());
    }

    @Test
    void shouldValidateRatingRange() {
        // Given
        UUID userId = UUID.randomUUID();
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        LocalDate now = LocalDate.now();

        // Then
        assertDoesNotThrow(() -> new Review(userId, book, "Good", 1, now, now));
        assertDoesNotThrow(() -> new Review(userId, book, "Good", 5, now, now));
    }
} 