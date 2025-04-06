package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.ReviewUseCase.CreateReviewCommand;
import com.wrappedup.backend.domain.port.in.ReviewUseCase.UpdateReviewCommand;
import com.wrappedup.backend.domain.port.in.ReviewUseCase.PaginatedReviewsResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewUseCaseTest {

    @Test
    @DisplayName("Should throw exception when userId is null in CreateReviewCommand")
    void createReviewCommand_WithNullUserId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                null, 
                BookId.of(UUID.randomUUID()), 
                4, 
                "Great Book", 
                "I really enjoyed this book!", 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when bookId is null in CreateReviewCommand")
    void createReviewCommand_WithNullBookId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                UserId.fromUUID(UUID.randomUUID()),
                null, 
                4, 
                "Great Book", 
                "I really enjoyed this book!", 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when rating is less than 1 in CreateReviewCommand")
    void createReviewCommand_WithRatingLessThanOne_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                UserId.fromUUID(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                0, 
                "Bad Rating", 
                "Rating too low", 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when rating is greater than 5 in CreateReviewCommand")
    void createReviewCommand_WithRatingGreaterThanFive_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                UserId.fromUUID(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                6, 
                "Too High Rating", 
                "Rating too high", 
                true
            )
        );
    }

    @Test
    @DisplayName("Should create CreateReviewCommand with valid parameters")
    void createReviewCommand_WithValidParameters_ShouldCreateInstance() {
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        String title = "Great Book";
        String content = "I really enjoyed this book!";
        boolean isPublic = true;
        int rating = 5;
        
        CreateReviewCommand command = new CreateReviewCommand(
            userId, bookId, rating, title, content, isPublic
        );
        
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertEquals(bookId, command.bookId());
        assertEquals(rating, command.rating());
        assertEquals(title, command.title());
        assertEquals(content, command.content());
        assertEquals(isPublic, command.isPublic());
    }

    @Test
    @DisplayName("Should throw exception when reviewId is null in UpdateReviewCommand")
    void updateReviewCommand_WithNullReviewId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateReviewCommand(
                null,
                UserId.fromUUID(UUID.randomUUID()),
                4,
                "Updated Title",
                "Updated content",
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when userId is null in UpdateReviewCommand")
    void updateReviewCommand_WithNullUserId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateReviewCommand(
                ReviewId.fromUUID(UUID.randomUUID()),
                null,
                4,
                "Updated Title",
                "Updated content",
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when rating is less than 1 in UpdateReviewCommand")
    void updateReviewCommand_WithRatingLessThanOne_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateReviewCommand(
                ReviewId.fromUUID(UUID.randomUUID()),
                UserId.fromUUID(UUID.randomUUID()),
                0,
                "Updated Title",
                "Updated content",
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when rating is greater than 5 in UpdateReviewCommand")
    void updateReviewCommand_WithRatingGreaterThanFive_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateReviewCommand(
                ReviewId.fromUUID(UUID.randomUUID()),
                UserId.fromUUID(UUID.randomUUID()),
                6,
                "Updated Title",
                "Updated content",
                true
            )
        );
    }

    @Test
    @DisplayName("Should create UpdateReviewCommand with valid parameters")
    void updateReviewCommand_WithValidParameters_ShouldCreateInstance() {
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        String title = "Updated Title";
        String content = "Updated content";
        Boolean isPublic = true;
        int rating = 4;
        
        UpdateReviewCommand command = new UpdateReviewCommand(
            reviewId, userId, rating, title, content, isPublic
        );
        
        assertNotNull(command);
        assertEquals(reviewId, command.reviewId());
        assertEquals(userId, command.userId());
        assertEquals(rating, command.rating());
        assertEquals(title, command.title());
        assertEquals(content, command.content());
        assertEquals(isPublic, command.isPublic());
    }

    @Test
    @DisplayName("Should create PaginatedReviewsResult with valid parameters")
    void paginatedReviewsResult_WithValidParameters_ShouldCreateInstance() {
        Review review1 = Mockito.mock(Review.class);
        Review review2 = Mockito.mock(Review.class);
        List<Review> reviews = Arrays.asList(review1, review2);
        
        PaginatedReviewsResult result = new PaginatedReviewsResult(reviews, 0, 10, 2, 1);
        
        assertNotNull(result);
        assertEquals(2, result.reviews().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
    }

    @Test
    @DisplayName("Should call createReview with the provided command")
    void createReview_ShouldCallWithProvidedCommand() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create a test command
        CreateReviewCommand command = new CreateReviewCommand(
            UserId.fromUUID(UUID.randomUUID()),
            BookId.of(UUID.randomUUID()),
            5,
            "Great Book",
            "I really enjoyed this book!",
            true
        );
        
        // Mock a review response
        Review mockReview = Mockito.mock(Review.class);
        when(useCase.createReview(command)).thenReturn(mockReview);
        
        // Call the method
        Review result = useCase.createReview(command);
        
        // Verify the method was called with the correct command
        verify(useCase).createReview(command);
        
        // Verify the result
        assertEquals(mockReview, result);
    }

    @Test
    @DisplayName("Should call updateReview with the provided command")
    void updateReview_ShouldCallWithProvidedCommand() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create a test command
        UpdateReviewCommand command = new UpdateReviewCommand(
            ReviewId.fromUUID(UUID.randomUUID()),
            UserId.fromUUID(UUID.randomUUID()),
            4,
            "Updated Title",
            "Updated content",
            true
        );
        
        // Mock a review response
        Review mockReview = Mockito.mock(Review.class);
        when(useCase.updateReview(command)).thenReturn(mockReview);
        
        // Call the method
        Review result = useCase.updateReview(command);
        
        // Verify the method was called with the correct command
        verify(useCase).updateReview(command);
        
        // Verify the result
        assertEquals(mockReview, result);
    }

    @Test
    @DisplayName("Should call getReviewById with the provided ReviewId")
    void getReviewById_ShouldCallWithProvidedReviewId() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
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
    @DisplayName("Should call getUserReviewForBook with the provided UserId and BookId")
    void getUserReviewForBook_ShouldCallWithProvidedUserIdAndBookId() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create test IDs
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock a review response
        Review mockReview = Mockito.mock(Review.class);
        when(useCase.getUserReviewForBook(userId, bookId)).thenReturn(Optional.of(mockReview));
        
        // Call the method
        Optional<Review> result = useCase.getUserReviewForBook(userId, bookId);
        
        // Verify the method was called with the correct UserId and BookId
        verify(useCase).getUserReviewForBook(userId, bookId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockReview, result.get());
    }

    @Test
    @DisplayName("Should call getUserReviews with the provided parameters")
    void getUserReviews_ShouldCallWithProvidedParameters() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create test parameters
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        int page = 0;
        int size = 10;
        
        // Mock a paginated result
        Review mockReview = Mockito.mock(Review.class);
        PaginatedReviewsResult mockResult = new PaginatedReviewsResult(
            Collections.singletonList(mockReview), page, size, 1, 1
        );
        when(useCase.getUserReviews(userId, page, size)).thenReturn(mockResult);
        
        // Call the method
        PaginatedReviewsResult result = useCase.getUserReviews(userId, page, size);
        
        // Verify the method was called with the correct parameters
        verify(useCase).getUserReviews(userId, page, size);
        
        // Verify the result
        assertEquals(1, result.reviews().size());
        assertEquals(page, result.page());
        assertEquals(size, result.size());
    }

    @Test
    @DisplayName("Should call getBookReviews with the provided parameters")
    void getBookReviews_ShouldCallWithProvidedParameters() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create test parameters
        BookId bookId = BookId.of(UUID.randomUUID());
        int page = 0;
        int size = 10;
        
        // Mock a paginated result
        Review mockReview1 = Mockito.mock(Review.class);
        Review mockReview2 = Mockito.mock(Review.class);
        PaginatedReviewsResult mockResult = new PaginatedReviewsResult(
            Arrays.asList(mockReview1, mockReview2), page, size, 2, 1
        );
        when(useCase.getBookReviews(bookId, page, size)).thenReturn(mockResult);
        
        // Call the method
        PaginatedReviewsResult result = useCase.getBookReviews(bookId, page, size);
        
        // Verify the method was called with the correct parameters
        verify(useCase).getBookReviews(bookId, page, size);
        
        // Verify the result
        assertEquals(2, result.reviews().size());
        assertEquals(page, result.page());
        assertEquals(size, result.size());
    }

    @Test
    @DisplayName("Should call getPublicBookReviews with the provided parameters")
    void getPublicBookReviews_ShouldCallWithProvidedParameters() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create test parameters
        BookId bookId = BookId.of(UUID.randomUUID());
        int page = 0;
        int size = 10;
        
        // Mock a paginated result
        Review mockReview = Mockito.mock(Review.class);
        PaginatedReviewsResult mockResult = new PaginatedReviewsResult(
            Collections.singletonList(mockReview), page, size, 1, 1
        );
        when(useCase.getPublicBookReviews(bookId, page, size)).thenReturn(mockResult);
        
        // Call the method
        PaginatedReviewsResult result = useCase.getPublicBookReviews(bookId, page, size);
        
        // Verify the method was called with the correct parameters
        verify(useCase).getPublicBookReviews(bookId, page, size);
        
        // Verify the result
        assertEquals(1, result.reviews().size());
        assertEquals(page, result.page());
        assertEquals(size, result.size());
    }

    @Test
    @DisplayName("Should call deleteReview with the provided parameters")
    void deleteReview_ShouldCallWithProvidedParameters() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create test parameters
        ReviewId reviewId = ReviewId.fromUUID(UUID.randomUUID());
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Call the method
        useCase.deleteReview(reviewId, userId);
        
        // Verify the method was called with the correct parameters
        verify(useCase).deleteReview(reviewId, userId);
    }

    @Test
    @DisplayName("Should call getAverageRatingForBook with the provided BookId")
    void getAverageRatingForBook_ShouldCallWithProvidedBookId() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create a test BookId
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock an average rating
        double mockRating = 4.5;
        when(useCase.getAverageRatingForBook(bookId)).thenReturn(mockRating);
        
        // Call the method
        double result = useCase.getAverageRatingForBook(bookId);
        
        // Verify the method was called with the correct BookId
        verify(useCase).getAverageRatingForBook(bookId);
        
        // Verify the result
        assertEquals(mockRating, result);
    }

    @Test
    @DisplayName("Should call hasUserReviewedBook with the provided UserId and BookId")
    void hasUserReviewedBook_ShouldCallWithProvidedUserIdAndBookId() {
        // Create a mock implementation of the interface
        ReviewUseCase useCase = Mockito.mock(ReviewUseCase.class);
        
        // Create test IDs
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock a result
        when(useCase.hasUserReviewedBook(userId, bookId)).thenReturn(true);
        
        // Call the method
        boolean result = useCase.hasUserReviewedBook(userId, bookId);
        
        // Verify the method was called with the correct UserId and BookId
        verify(useCase).hasUserReviewedBook(userId, bookId);
        
        // Verify the result
        assertTrue(result);
    }
} 