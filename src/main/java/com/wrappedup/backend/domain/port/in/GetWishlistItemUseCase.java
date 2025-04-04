package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;

import java.util.List;
import java.util.Optional;

/**
 * Use case for retrieving wishlist items.
 */
public interface GetWishlistItemUseCase {

    /**
     * Retrieves a wishlist item by its ID.
     *
     * @param id the ID of the wishlist item
     * @return the wishlist item, if found
     */
    Optional<WishlistItem> getWishlistItemById(WishlistItemId id);

    /**
     * Retrieves all wishlist items for a user.
     *
     * @param userId the ID of the user
     * @return the list of wishlist items
     */
    List<WishlistItem> getWishlistItemsByUserId(UserId userId);

    /**
     * Retrieves all public wishlist items for a user by username.
     * 
     * @param username the username of the user
     * @return the list of public wishlist items
     * @throws IllegalArgumentException if the user is not found
     */
    List<WishlistItem> getPublicWishlistItemsByUsername(String username);

    /**
     * Retrieves a wishlist item by user ID and book ID.
     *
     * @param userId the ID of the user
     * @param bookId the ID of the book
     * @return the wishlist item, if found
     */
    Optional<WishlistItem> getWishlistItemByUserIdAndBookId(UserId userId, BookId bookId);

    /**
     * Checks if a wishlist item exists for a user and book.
     *
     * @param userId the ID of the user
     * @param bookId the ID of the book
     * @return true if the wishlist item exists, false otherwise
     */
    boolean existsByUserIdAndBookId(UserId userId, BookId bookId);
} 