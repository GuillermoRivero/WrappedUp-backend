package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;

/**
 * Use case for updating a wishlist item.
 */
public interface UpdateWishlistItemUseCase {

    /**
     * Command for updating a wishlist item.
     */
    record UpdateWishlistItemCommand(
            WishlistItemId id,
            String description,
            Integer priority,
            Boolean isPublic
    ) {
        public UpdateWishlistItemCommand {
            if (id == null) {
                throw new IllegalArgumentException("Wishlist item ID cannot be null");
            }
            // Validate priority range if provided
            if (priority != null && (priority < 1 || priority > 5)) {
                throw new IllegalArgumentException("Priority must be between 1 and 5");
            }
        }
    }

    /**
     * Updates a wishlist item.
     *
     * @param command the command for updating a wishlist item
     * @return the updated wishlist item
     * @throws IllegalArgumentException if the wishlist item does not exist
     */
    WishlistItem updateWishlistItem(UpdateWishlistItemCommand command);
} 