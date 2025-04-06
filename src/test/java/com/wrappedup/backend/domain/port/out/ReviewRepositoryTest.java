package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewRepositoryTest {

    @Test
    @DisplayName("Should save a review")
    void save_ShouldReturnSavedReview() {
        // Create a mock implementation of the interface
        ReviewRepository repository = Mockito.mock(ReviewRepository.class);
        
        // Create a test review
        Review review = createMockReview();
        
        // Mock the save behavior
        when(repository.save(review)).thenReturn(review);
        
        // Call the method
        Review result = repository.save(review);
        
        // Verify the method was called with the correct review
        verify(repository).save(review);
        
        // Verify the result
        assertEquals(review, result);
    }
    
    @Test
    @DisplayName("Should find a review by ID")
    void findById_ShouldReturnReviewWhenFound() {
        // Create a mock implementation of the interface
        ReviewRepository repository = Mockito.mock(ReviewRepository.class);
        
        // Create a test review and ID
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        Review review = createMockReview();
        
        // Mock the findById behavior
        when(repository.findById(reviewId)).thenReturn(Optional.of(review));
        
        // Call the method
        Optional<Review> result = repository.findById(reviewId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(reviewId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(review, result.get());
    }
    
    @Test
    @DisplayName("Should return empty when review not found by ID")
    void findById_ShouldReturnEmptyWhenNotFound() {
        // Create a mock implementation of the interface
        ReviewRepository repository = Mockito.mock(ReviewRepository.class);
        
        // Create a test ID
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        
        // Mock the findById behavior
        when(repository.findById(reviewId)).thenReturn(Optional.empty());
        
        // Call the method
        Optional<Review> result = repository.findById(reviewId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(reviewId);
        
        // Verify the result
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should find reviews by user ID")
    void findByUserId_ShouldReturnUserReviews() {
        // Create a mock implementation of the interface
        ReviewRepository repository = Mockito.mock(ReviewRepository.class);
        
        // Create test data
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        Review review1 = createMockReview();
        Review review2 = createMockReview();
        List<Review> expectedReviews = Arrays.asList(review1, review2);
        
        // Mock the findByUserId behavior
        when(repository.findByUserId(userId)).thenReturn(expectedReviews);
        
        // Call the method
        List<Review> result = repository.findByUserId(userId);
        
        // Verify the method was called with the correct user ID
        verify(repository).findByUserId(userId);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(expectedReviews, result);
    }
    
    @Test
    @DisplayName("Should find a review by user ID and book ID")
    void findByUserIdAndBookId_ShouldReturnReviewWhenFound() {
        // Create a mock implementation of the interface
        ReviewRepository repository = Mockito.mock(ReviewRepository.class);
        
        // Create test data
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        Review review = createMockReview();
        
        // Mock the findByUserIdAndBookId behavior
        when(repository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(review));
        
        // Call the method
        Optional<Review> result = repository.findByUserIdAndBookId(userId, bookId);
        
        // Verify the method was called with the correct IDs
        verify(repository).findByUserIdAndBookId(userId, bookId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(review, result.get());
    }
    
    @Test
    @DisplayName("Should find public reviews by book ID")
    void findPublicReviewsByBookId_ShouldReturnPublicReviews() {
        // Create a mock implementation of the interface
        ReviewRepository repository = Mockito.mock(ReviewRepository.class);
        
        // Create test data
        BookId bookId = BookId.of(UUID.randomUUID());
        Review review1 = createMockReview();
        Review review2 = createMockReview();
        List<Review> expectedReviews = Arrays.asList(review1, review2);
        
        // Mock the findPublicReviewsByBookId behavior
        when(repository.findPublicReviewsByBookId(bookId)).thenReturn(expectedReviews);
        
        // Call the method
        List<Review> result = repository.findPublicReviewsByBookId(bookId);
        
        // Verify the method was called with the correct book ID
        verify(repository).findPublicReviewsByBookId(bookId);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(expectedReviews, result);
    }
    
    @Test
    @DisplayName("Should delete a review by ID")
    void deleteById_ShouldCallRepositoryWithCorrectId() {
        // Create a mock implementation of the interface
        ReviewRepository repository = Mockito.mock(ReviewRepository.class);
        
        // Create a test ID
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        
        // Call the method
        repository.deleteById(reviewId);
        
        // Verify the method was called with the correct ID
        verify(repository).deleteById(reviewId);
    }
    
    private Review createMockReview() {
        LocalDateTime now = LocalDateTime.now();
        return Review.reconstitute(
                ReviewId.fromUUID(UUID.randomUUID()),
                UserId.fromUUID(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()),
                5,
                "Great book!",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(2),
                true,
                now,
                now
        );
    }
} 