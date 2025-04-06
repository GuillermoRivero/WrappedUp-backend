package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    private final ReviewId reviewId = ReviewId.generate();
    private final UserId userId = UserId.generate();
    private final BookId bookId = BookId.generate();
    private final int rating = 4;
    private final String content = "This is a great book!";
    private final LocalDate startDate = LocalDate.now().minusMonths(1);
    private final LocalDate endDate = LocalDate.now().minusDays(7);
    private final boolean isPublic = true;
    private final LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    private final LocalDateTime updatedAt = LocalDateTime.now();
    
    @Test
    @DisplayName("Should create a new review with all fields")
    void createNewReview_ShouldCreateReviewWithAllFields() {
        // Act
        Review review = Review.createNewReview(userId, bookId, rating, content, startDate, endDate, isPublic);
        
        // Assert
        assertNotNull(review);
        assertNotNull(review.getId());
        assertEquals(userId, review.getUserId());
        assertEquals(bookId, review.getBookId());
        assertEquals(rating, review.getRating());
        assertEquals(content, review.getContent());
        assertEquals(startDate, review.getStartDate());
        assertEquals(endDate, review.getEndDate());
        assertEquals(isPublic, review.isPublic());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
        assertEquals(review.getCreatedAt(), review.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should reconstitute an existing review with all fields")
    void reconstitute_ShouldCreateReviewWithAllFields() {
        // Act
        Review review = Review.reconstitute(
                reviewId, userId, bookId, rating, content, startDate, endDate, isPublic, createdAt, updatedAt
        );
        
        // Assert
        assertNotNull(review);
        assertEquals(reviewId, review.getId());
        assertEquals(userId, review.getUserId());
        assertEquals(bookId, review.getBookId());
        assertEquals(rating, review.getRating());
        assertEquals(content, review.getContent());
        assertEquals(startDate, review.getStartDate());
        assertEquals(endDate, review.getEndDate());
        assertEquals(isPublic, review.isPublic());
        assertEquals(createdAt, review.getCreatedAt());
        assertEquals(updatedAt, review.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should update review details")
    void updateReview_ShouldChangeReviewDetails() {
        // Arrange
        Review review = Review.reconstitute(
                reviewId, userId, bookId, rating, content, startDate, endDate, isPublic, createdAt, updatedAt
        );
        int newRating = 5;
        String newContent = "Updated review content";
        LocalDate newStartDate = LocalDate.now().minusDays(10);
        LocalDate newEndDate = LocalDate.now().minusDays(2);
        boolean newIsPublic = false;
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        review.updateReview(newRating, newContent, newStartDate, newEndDate, newIsPublic);
        
        // Assert
        assertEquals(newRating, review.getRating());
        assertEquals(newContent, review.getContent());
        assertEquals(newStartDate, review.getStartDate());
        assertEquals(newEndDate, review.getEndDate());
        assertEquals(newIsPublic, review.isPublic());
        assertTrue(review.getUpdatedAt().isAfter(beforeUpdate) || 
                review.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should make review public")
    void makePublic_ShouldSetIsPublicToTrue() {
        // Arrange
        Review review = Review.reconstitute(
                reviewId, userId, bookId, rating, content, startDate, endDate, false, createdAt, updatedAt
        );
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        review.makePublic();
        
        // Assert
        assertTrue(review.isPublic());
        assertTrue(review.getUpdatedAt().isAfter(beforeUpdate) || 
                review.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should make review private")
    void makePrivate_ShouldSetIsPublicToFalse() {
        // Arrange
        Review review = Review.reconstitute(
                reviewId, userId, bookId, rating, content, startDate, endDate, true, createdAt, updatedAt
        );
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        review.makePrivate();
        
        // Assert
        assertFalse(review.isPublic());
        assertTrue(review.getUpdatedAt().isAfter(beforeUpdate) || 
                review.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid rating below range")
    void constructor_WithInvalidRatingBelowRange_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> Review.createNewReview(userId, bookId, 0, content, startDate, endDate, isPublic)
        );
    }
    
    @Test
    @DisplayName("Should throw exception for invalid rating above range")
    void constructor_WithInvalidRatingAboveRange_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> Review.createNewReview(userId, bookId, 6, content, startDate, endDate, isPublic)
        );
    }
    
    @Test
    @DisplayName("Equal reviews should be equal")
    void equals_WithSameId_ShouldBeEqual() {
        // Arrange
        Review review1 = Review.reconstitute(
                reviewId, userId, bookId, rating, content, startDate, endDate, isPublic, createdAt, updatedAt
        );
        
        Review review2 = Review.reconstitute(
                reviewId, userId, bookId, rating, content, startDate, endDate, isPublic, createdAt, updatedAt
        );
        
        // Assert
        assertEquals(review1, review2);
        assertEquals(review1.hashCode(), review2.hashCode());
    }
    
    @Test
    @DisplayName("Reviews with different IDs should not be equal")
    void equals_WithDifferentIds_ShouldNotBeEqual() {
        // Arrange
        Review review1 = Review.reconstitute(
                reviewId, userId, bookId, rating, content, startDate, endDate, isPublic, createdAt, updatedAt
        );
        
        Review review2 = Review.reconstitute(
                ReviewId.generate(), userId, bookId, rating, content, startDate, endDate, isPublic, createdAt, updatedAt
        );
        
        // Assert
        assertNotEquals(review1, review2);
        assertNotEquals(review1.hashCode(), review2.hashCode());
    }
} 