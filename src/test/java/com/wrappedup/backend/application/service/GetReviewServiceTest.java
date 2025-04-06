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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private GetReviewService getReviewService;

    private ReviewId reviewId;
    private UserId userId;
    private BookId bookId;
    private Review review;
    private List<Review> reviewList;

    @BeforeEach
    void setUp() {
        reviewId = ReviewId.generate();
        userId = UserId.generate();
        bookId = BookId.generate();

        // Create a review
        review = Review.createNewReview(
                userId, 
                bookId, 
                4, 
                "Great book", 
                LocalDate.now().minusDays(10), 
                LocalDate.now(), 
                true
        );
        ReflectionTestUtils.setField(review, "id", reviewId);

        // Create a list of reviews
        Review privateReview = Review.createNewReview(
                userId, 
                BookId.generate(), 
                3, 
                "Private review", 
                LocalDate.now().minusDays(5), 
                null, 
                false
        );
        reviewList = Arrays.asList(review, privateReview);
    }

    @Test
    @DisplayName("Should get review by ID when it exists")
    void getReviewById_WhenReviewExists_ShouldReturnReview() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // Act
        Optional<Review> result = getReviewService.getReviewById(reviewId);

        // Assert
        verify(reviewRepository).findById(reviewId);
        assertTrue(result.isPresent());
        assertEquals(review, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when review doesn't exist by ID")
    void getReviewById_WhenReviewDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act
        Optional<Review> result = getReviewService.getReviewById(reviewId);

        // Assert
        verify(reviewRepository).findById(reviewId);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should get reviews by user ID")
    void getReviewsByUserId_ShouldReturnUserReviews() {
        // Arrange
        when(reviewRepository.findByUserId(userId)).thenReturn(reviewList);

        // Act
        List<Review> result = getReviewService.getReviewsByUserId(userId);

        // Assert
        verify(reviewRepository).findByUserId(userId);
        assertEquals(2, result.size());
        assertEquals(reviewList, result);
    }

    @Test
    @DisplayName("Should return empty list when no reviews exist for user ID")
    void getReviewsByUserId_WhenNoReviewsExist_ShouldReturnEmptyList() {
        // Arrange
        UserId nonExistentUserId = UserId.generate();
        when(reviewRepository.findByUserId(nonExistentUserId)).thenReturn(Collections.emptyList());

        // Act
        List<Review> result = getReviewService.getReviewsByUserId(nonExistentUserId);

        // Assert
        verify(reviewRepository).findByUserId(nonExistentUserId);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when user ID is null")
    void getReviewsByUserId_WhenUserIdIsNull_ShouldReturnEmptyList() {
        // Act
        List<Review> result = getReviewService.getReviewsByUserId(null);

        // Assert
        verify(reviewRepository, never()).findByUserId(any());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should get review by user ID and book ID when it exists")
    void getReviewByUserIdAndBookId_WhenReviewExists_ShouldReturnReview() {
        // Arrange
        when(reviewRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(review));

        // Act
        Optional<Review> result = getReviewService.getReviewByUserIdAndBookId(userId, bookId);

        // Assert
        verify(reviewRepository).findByUserIdAndBookId(userId, bookId);
        assertTrue(result.isPresent());
        assertEquals(review, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when review doesn't exist by user ID and book ID")
    void getReviewByUserIdAndBookId_WhenReviewDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        UserId nonExistentUserId = UserId.generate();
        when(reviewRepository.findByUserIdAndBookId(nonExistentUserId, bookId)).thenReturn(Optional.empty());

        // Act
        Optional<Review> result = getReviewService.getReviewByUserIdAndBookId(nonExistentUserId, bookId);

        // Assert
        verify(reviewRepository).findByUserIdAndBookId(nonExistentUserId, bookId);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional when user ID is null")
    void getReviewByUserIdAndBookId_WhenUserIdIsNull_ShouldReturnEmpty() {
        // Act
        Optional<Review> result = getReviewService.getReviewByUserIdAndBookId(null, bookId);

        // Assert
        verify(reviewRepository, never()).findByUserIdAndBookId(any(), any());
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional when book ID is null")
    void getReviewByUserIdAndBookId_WhenBookIdIsNull_ShouldReturnEmpty() {
        // Act
        Optional<Review> result = getReviewService.getReviewByUserIdAndBookId(userId, null);

        // Assert
        verify(reviewRepository, never()).findByUserIdAndBookId(any(), any());
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should get public reviews by book ID")
    void getPublicReviewsByBookId_ShouldReturnPublicReviews() {
        // Arrange
        List<Review> publicReviews = Collections.singletonList(review); // Only the public review
        when(reviewRepository.findPublicReviewsByBookId(bookId)).thenReturn(publicReviews);

        // Act
        List<Review> result = getReviewService.getPublicReviewsByBookId(bookId);

        // Assert
        verify(reviewRepository).findPublicReviewsByBookId(bookId);
        assertEquals(1, result.size());
        assertEquals(publicReviews, result);
    }

    @Test
    @DisplayName("Should return empty list when no public reviews exist for book ID")
    void getPublicReviewsByBookId_WhenNoReviewsExist_ShouldReturnEmptyList() {
        // Arrange
        BookId nonExistentBookId = BookId.generate();
        when(reviewRepository.findPublicReviewsByBookId(nonExistentBookId)).thenReturn(Collections.emptyList());

        // Act
        List<Review> result = getReviewService.getPublicReviewsByBookId(nonExistentBookId);

        // Assert
        verify(reviewRepository).findPublicReviewsByBookId(nonExistentBookId);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when book ID is null")
    void getPublicReviewsByBookId_WhenBookIdIsNull_ShouldReturnEmptyList() {
        // Act
        List<Review> result = getReviewService.getPublicReviewsByBookId(null);

        // Assert
        verify(reviewRepository, never()).findPublicReviewsByBookId(any());
        assertTrue(result.isEmpty());
    }
} 