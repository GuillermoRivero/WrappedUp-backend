package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;

/**
 * Use case for creating a wishlist item.
 */
public interface CreateWishlistItemUseCase {

    /**
     * Command for creating a wishlist item.
     */
    record CreateWishlistItemCommand(
            UserId userId,
            BookId bookId,
            String description,
            Integer priority,
            Boolean isPublic
    ) {
        public CreateWishlistItemCommand {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (bookId == null) {
                throw new IllegalArgumentException("Book ID cannot be null");
            }
            // Priority defaults to 3 if not provided
            priority = priority == null ? 3 : priority;
            // isPublic defaults to false if not provided
            isPublic = isPublic == null ? false : isPublic;
            // Validate priority range
            if (priority < 1 || priority > 5) {
                throw new IllegalArgumentException("Priority must be between 1 and 5");
            }
        }
    }

    /**
     * Creates a wishlist item.
     *
     * @param command the command for creating a wishlist item
     * @return the created wishlist item's ID
     */
    WishlistItemId createWishlistItem(CreateWishlistItemCommand command);
} 