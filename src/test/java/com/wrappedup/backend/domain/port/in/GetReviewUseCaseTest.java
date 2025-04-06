package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetReviewUseCaseTest {

    @Test
    @DisplayName("Should call getReviewById with the provided ReviewId")
    void getReviewById_ShouldCallWithProvidedReviewId() {
        // Create a mock implementation of the interface
        GetReviewUseCase useCase = Mockito.mock(GetReviewUseCase.class);
        
        // Create a test ReviewId
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        
        // Mock a review response
        Review mockReview = Mockito.mock(Review.class);
        when(useCase.getReviewById(reviewId)).thenReturn(Optional.of(mockReview));
        
        // Call the method
        Optional<Review> result = useCase.getReviewById(reviewId);
        
        // Verify the method was called with the correct ReviewId
        verify(useCase).getReviewById(reviewId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockReview, result.get());
    }
    
    @Test
    @DisplayName("Should call getReviewsByUserId with the provided UserId")
    void getReviewsByUserId_ShouldCallWithProvidedUserId() {
        // Create a mock implementation of the interface
        GetReviewUseCase useCase = Mockito.mock(GetReviewUseCase.class);
        
        // Create a test UserId
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Mock reviews response
        Review mockReview1 = Mockito.mock(Review.class);
        Review mockReview2 = Mockito.mock(Review.class);
        List<Review> mockReviews = Arrays.asList(mockReview1, mockReview2);
        when(useCase.getReviewsByUserId(userId)).thenReturn(mockReviews);
        
        // Call the method
        List<Review> result = useCase.getReviewsByUserId(userId);
        
        // Verify the method was called with the correct UserId
        verify(useCase).getReviewsByUserId(userId);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(mockReviews, result);
    }
    
    @Test
    @DisplayName("Should call getReviewByUserIdAndBookId with the provided UserId and BookId")
    void getReviewByUserIdAndBookId_ShouldCallWithProvidedUserIdAndBookId() {
        // Create a mock implementation of the interface
        GetReviewUseCase useCase = Mockito.mock(GetReviewUseCase.class);
        
        // Create test IDs
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock a review response
        Review mockReview = Mockito.mock(Review.class);
        when(useCase.getReviewByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockReview));
        
        // Call the method
        Optional<Review> result = useCase.getReviewByUserIdAndBookId(userId, bookId);
        
        // Verify the method was called with the correct UserId and BookId
        verify(useCase).getReviewByUserIdAndBookId(userId, bookId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockReview, result.get());
    }
    
    @Test
    @DisplayName("Should call getPublicReviewsByBookId with the provided BookId")
    void getPublicReviewsByBookId_ShouldCallWithProvidedBookId() {
        // Create a mock implementation of the interface
        GetReviewUseCase useCase = Mockito.mock(GetReviewUseCase.class);
        
        // Create a test BookId
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock reviews response
        Review mockReview1 = Mockito.mock(Review.class);
        Review mockReview2 = Mockito.mock(Review.class);
        Review mockReview3 = Mockito.mock(Review.class);
        List<Review> mockReviews = Arrays.asList(mockReview1, mockReview2, mockReview3);
        when(useCase.getPublicReviewsByBookId(bookId)).thenReturn(mockReviews);
        
        // Call the method
        List<Review> result = useCase.getPublicReviewsByBookId(bookId);
        
        // Verify the method was called with the correct BookId
        verify(useCase).getPublicReviewsByBookId(bookId);
        
        // Verify the result
        assertEquals(3, result.size());
        assertEquals(mockReviews, result);
    }
} 