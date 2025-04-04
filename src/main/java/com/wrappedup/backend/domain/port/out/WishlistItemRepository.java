package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;

import java.util.List;
import java.util.Optional;

/**
 * Port for wishlist item persistence operations.
 */
public interface WishlistItemRepository {
    /**
     * Save a wishlist item.
     */
    WishlistItem save(WishlistItem wishlistItem);
    
    /**
     * Find a wishlist item by ID.
     */
    Optional<WishlistItem> findById(WishlistItemId id);
    
    /**
     * Find all wishlist items for a user.
     */
    List<WishlistItem> findAllByUserId(UserId userId);
    
    /**
     * Find a wishlist item by user ID and book ID.
     */
    Optional<WishlistItem> findByUserIdAndBookId(UserId userId, BookId bookId);
    
    /**
     * Delete a wishlist item.
     */
    void deleteById(WishlistItemId id);
    
    /**
     * Check if a wishlist item exists for a user and book.
     */
    boolean existsByUserIdAndBookId(UserId userId, BookId bookId);
} 