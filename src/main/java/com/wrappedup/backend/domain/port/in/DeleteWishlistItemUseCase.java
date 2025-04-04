package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.WishlistItemId;

/**
 * Use case for deleting a wishlist item.
 */
public interface DeleteWishlistItemUseCase {

    /**
     * Deletes a wishlist item by its ID.
     *
     * @param id the ID of the wishlist item to delete
     */
    void deleteWishlistItem(WishlistItemId id);
} 