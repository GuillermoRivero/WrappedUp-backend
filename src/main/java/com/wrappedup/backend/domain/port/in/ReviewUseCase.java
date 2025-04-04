package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for review operations.
 */
public interface ReviewUseCase {
    /**
     * Command for creating a new review.
     */
    record CreateReviewCommand(
            UserId userId,
            BookId bookId,
            int rating,
            String title,
            String content,
            boolean isPublic) {
        
        public CreateReviewCommand {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (bookId == null) {
                throw new IllegalArgumentException("Book ID cannot be null");
            }
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
        }
    }
    
    /**
     * Command for updating a review.
     */
    record UpdateReviewCommand(
            ReviewId reviewId,
            UserId userId,
            int rating,
            String title,
            String content,
            Boolean isPublic) {
        
        public UpdateReviewCommand {
            if (reviewId == null) {
                throw new IllegalArgumentException("Review ID cannot be null");
            }
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
        }
    }
    
    /**
     * Result for paginated review queries.
     */
    record PaginatedReviewsResult(
            List<Review> reviews,
            int page,
            int size,
            long totalElements,
            int totalPages) {
    }
    
    /**
     * Create a new review.
     * @param command The create review command
     * @return The created review
     * @throws IllegalArgumentException if the user has already reviewed this book
     */
    Review createReview(CreateReviewCommand command) throws IllegalArgumentException;
    
    /**
     * Update an existing review.
     * @param command The update review command
     * @return The updated review
     * @throws IllegalArgumentException if the review does not exist or the user is not the author
     */
    Review updateReview(UpdateReviewCommand command) throws IllegalArgumentException;
    
    /**
     * Get a review by its ID.
     * @param id The review ID
     * @return The review if found
     */
    Optional<Review> getReviewById(ReviewId id);
    
    /**
     * Get a user's review for a specific book.
     * @param userId The user ID
     * @param bookId The book ID
     * @return The review if found
     */
    Optional<Review> getUserReviewForBook(UserId userId, BookId bookId);
    
    /**
     * Get all reviews by a user.
     * @param userId The user ID
     * @param page The page number (0-based)
     * @param size The page size
     * @return Paginated result of reviews by the user
     */
    PaginatedReviewsResult getUserReviews(UserId userId, int page, int size);
    
    /**
     * Get all reviews for a book.
     * @param bookId The book ID
     * @param page The page number (0-based)
     * @param size The page size
     * @return Paginated result of reviews for the book
     */
    PaginatedReviewsResult getBookReviews(BookId bookId, int page, int size);
    
    /**
     * Get public reviews for a book.
     * @param bookId The book ID
     * @param page The page number (0-based)
     * @param size The page size
     * @return Paginated result of public reviews for the book
     */
    PaginatedReviewsResult getPublicBookReviews(BookId bookId, int page, int size);
    
    /**
     * Delete a review.
     * @param reviewId The review ID
     * @param userId The user ID (must be the author of the review)
     * @throws IllegalArgumentException if the review does not exist or the user is not the author
     */
    void deleteReview(ReviewId reviewId, UserId userId) throws IllegalArgumentException;
    
    /**
     * Get the average rating for a book.
     * @param bookId The book ID
     * @return The average rating or 0 if there are no reviews
     */
    double getAverageRatingForBook(BookId bookId);
    
    /**
     * Check if a user has already reviewed a book.
     * @param userId The user ID
     * @param bookId The book ID
     * @return True if the user has already reviewed the book, false otherwise
     */
    boolean hasUserReviewedBook(UserId userId, BookId bookId);
} 