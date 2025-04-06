package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.WishlistItemId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.verify;

class DeleteWishlistItemUseCaseTest {

    @Test
    @DisplayName("Should call deleteWishlistItem with the provided WishlistItemId")
    void deleteWishlistItem_ShouldCallWithProvidedWishlistItemId() {
        // Create a mock implementation of the interface
        DeleteWishlistItemUseCase useCase = Mockito.mock(DeleteWishlistItemUseCase.class);
        
        // Create a test WishlistItemId
        WishlistItemId wishlistItemId = WishlistItemId.fromUUID(UUID.randomUUID());
        
        // Call the method
        useCase.deleteWishlistItem(wishlistItemId);
        
        // Verify the method was called with the correct WishlistItemId
        verify(useCase).deleteWishlistItem(wishlistItemId);
    }
} 