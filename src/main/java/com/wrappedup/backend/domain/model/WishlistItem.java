package com.wrappedup.backend.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a wishlist item.
 */
public class WishlistItem {
    private final WishlistItemId id;
    private final UserId userId;
    private final BookId bookId;
    private String description;
    private Integer priority;
    private boolean isPublic;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private WishlistItem(
            WishlistItemId id,
            UserId userId,
            BookId bookId,
            String description,
            Integer priority,
            boolean isPublic,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "Wishlist Item ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.bookId = Objects.requireNonNull(bookId, "Book ID cannot be null");
        this.description = description;
        this.priority = priority != null ? priority : 3; // Default priority is 3
        this.isPublic = isPublic;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    /**
     * Creates a new wishlist item with a generated ID.
     */
    public static WishlistItem createNewWishlistItem(
            UserId userId,
            BookId bookId,
            String description,
            Integer priority,
            boolean isPublic) {
        LocalDateTime now = LocalDateTime.now();
        return new WishlistItem(
                WishlistItemId.generate(),
                userId,
                bookId,
                description,
                priority,
                isPublic,
                now,
                now
        );
    }
    
    /**
     * Reconstructs an existing wishlist item from persistence.
     */
    public static WishlistItem reconstitute(
            WishlistItemId id,
            UserId userId,
            BookId bookId,
            String description,
            Integer priority,
            Boolean isPublic,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new WishlistItem(
                id,
                userId,
                bookId,
                description,
                priority != null ? priority : 3,
                isPublic != null ? isPublic : false,
                createdAt,
                updatedAt != null ? updatedAt : createdAt  // Use createdAt as fallback if updatedAt is null
        );
    }
    
    /**
     * Updates the wishlist item details.
     */
    public void updateDetails(String description, Integer priority, Boolean isPublic) {
        if (description != null) {
            this.description = description;
        }
        
        if (priority != null && priority >= 1 && priority <= 5) {
            this.priority = priority;
        }
        
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
        
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    
    public WishlistItemId getId() {
        return id;
    }
    
    public UserId getUserId() {
        return userId;
    }
    
    public BookId getBookId() {
        return bookId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Integer getPriority() {
        return priority;
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
        WishlistItem that = (WishlistItem) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "WishlistItem{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", priority=" + priority +
                ", isPublic=" + isPublic +
                '}';
    }
} 