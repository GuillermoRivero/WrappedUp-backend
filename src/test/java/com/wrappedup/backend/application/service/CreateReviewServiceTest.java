package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.CreateReviewUseCase.CreateReviewCommand;
import com.wrappedup.backend.domain.port.out.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private CreateReviewService createReviewService;

    @Captor
    private ArgumentCaptor<Review> reviewCaptor;

    private UserId userId;
    private BookId bookId;
    private ReviewId reviewId;
    private CreateReviewCommand createCommand;
    private Review existingReview;
    private Review newReview;

    private static final int RATING = 4;
    private static final String CONTENT = "Great book!";
    private static final LocalDate START_DATE = LocalDate.now().minusDays(7);
    private static final LocalDate END_DATE = LocalDate.now().minusDays(1);
    private static final boolean IS_PUBLIC = true;

    @BeforeEach
    void setUp() {
        userId = UserId.generate();
        bookId = BookId.generate();
        reviewId = ReviewId.generate();

        createCommand = new CreateReviewCommand(
                userId,
                bookId,
                RATING,
                CONTENT,
                START_DATE,
                END_DATE,
                IS_PUBLIC
        );

        LocalDateTime now = LocalDateTime.now();
        existingReview = Review.reconstitute(
                reviewId,
                userId,
                bookId,
                3,  // Different rating than the create command
                "Initial content",
                START_DATE.minusDays(1),
                END_DATE.minusDays(2),
                false,  // Different visibility than the create command
                now.minusDays(10),
                now.minusDays(10)
        );

        newReview = Review.createNewReview(
                userId,
                bookId,
                RATING,
                CONTENT,
                START_DATE,
                END_DATE,
                IS_PUBLIC
        );
    }

    @Test
    @DisplayName("Should create a new review when user hasn't reviewed the book before")
    void createReview_NewReview_ShouldCreateReview() {
        // Arrange
        when(reviewRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(newReview);

        // Act
        ReviewId result = createReviewService.createReview(createCommand);

        // Assert
        assertNotNull(result);
        verify(reviewRepository).findByUserIdAndBookId(userId, bookId);
        verify(reviewRepository).save(reviewCaptor.capture());
        
        Review capturedReview = reviewCaptor.getValue();
        assertEquals(userId, capturedReview.getUserId());
        assertEquals(bookId, capturedReview.getBookId());
        assertEquals(RATING, capturedReview.getRating());
        assertEquals(CONTENT, capturedReview.getContent());
        assertEquals(START_DATE, capturedReview.getStartDate());
        assertEquals(END_DATE, capturedReview.getEndDate());
        assertEquals(IS_PUBLIC, capturedReview.isPublic());
    }

    @Test
    @DisplayName("Should update existing review when user has already reviewed the book")
    void createReview_ExistingReview_ShouldUpdateReview() {
        // Arrange
        when(reviewRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(existingReview);

        // Act
        ReviewId result = createReviewService.createReview(createCommand);

        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result);
        verify(reviewRepository).findByUserIdAndBookId(userId, bookId);
        verify(reviewRepository).save(reviewCaptor.capture());
        
        Review capturedReview = reviewCaptor.getValue();
        assertEquals(reviewId, capturedReview.getId());
        assertEquals(userId, capturedReview.getUserId());
        assertEquals(bookId, capturedReview.getBookId());
        assertEquals(RATING, capturedReview.getRating());
        assertEquals(CONTENT, capturedReview.getContent());
        assertEquals(START_DATE, capturedReview.getStartDate());
        assertEquals(END_DATE, capturedReview.getEndDate());
        assertEquals(IS_PUBLIC, capturedReview.isPublic());
    }
} 