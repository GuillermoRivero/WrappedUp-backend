package com.wrappedup.backend.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a book review in the system.
 */
public class Review {
    private final ReviewId id;
    private final UserId userId;
    private final BookId bookId;
    private int rating;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPublic;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Review(
            ReviewId id,
            UserId userId,
            BookId bookId,
            int rating,
            String content,
            LocalDate startDate,
            LocalDate endDate,
            boolean isPublic,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "Review ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.bookId = Objects.requireNonNull(bookId, "Book ID cannot be null");
        
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
        
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPublic = isPublic;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    /**
     * Creates a new review with generated ID.
     */
    public static Review createNewReview(
            UserId userId,
            BookId bookId,
            int rating,
            String content,
            LocalDate startDate,
            LocalDate endDate,
            boolean isPublic) {
        LocalDateTime now = LocalDateTime.now();
        return new Review(
                ReviewId.generate(),
                userId,
                bookId,
                rating,
                content,
                startDate,
                endDate,
                isPublic,
                now,
                now
        );
    }
    
    /**
     * Reconstructs an existing review from persistence.
     */
    public static Review reconstitute(
            ReviewId id,
            UserId userId,
            BookId bookId,
            int rating,
            String content,
            LocalDate startDate,
            LocalDate endDate,
            boolean isPublic,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Review(
                id,
                userId,
                bookId,
                rating,
                content,
                startDate,
                endDate,
                isPublic,
                createdAt,
                updatedAt
        );
    }
    
    /**
     * Updates the review details.
     */
    public void updateReview(int rating, String content, LocalDate startDate, LocalDate endDate, Boolean isPublic) {
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
        
        if (content != null) {
            this.content = content;
        }
        
        if (startDate != null) {
            this.startDate = startDate;
        }
        
        if (endDate != null) {
            this.endDate = endDate;
        }
        
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
        
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Makes the review publicly visible.
     */
    public void makePublic() {
        this.isPublic = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Makes the review private (only visible to the author).
     */
    public void makePrivate() {
        this.isPublic = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    
    public ReviewId getId() {
        return id;
    }
    
    public UserId getUserId() {
        return userId;
    }
    
    public BookId getBookId() {
        return bookId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public String getContent() {
        return content;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Object methods
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", rating=" + rating +
                '}';
    }
} 