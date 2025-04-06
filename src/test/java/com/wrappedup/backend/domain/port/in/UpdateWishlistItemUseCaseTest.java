package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;
import com.wrappedup.backend.domain.port.in.UpdateWishlistItemUseCase.UpdateWishlistItemCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateWishlistItemUseCaseTest {

    @Test
    @DisplayName("Should throw exception when id is null in UpdateWishlistItemCommand")
    void updateWishlistItemCommand_WithNullId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateWishlistItemCommand(
                null, 
                "Updated description", 
                3, 
                true
            )
        );
    }
    
    @Test
    @DisplayName("Should throw exception when priority is less than 1 in UpdateWishlistItemCommand")
    void updateWishlistItemCommand_WithPriorityLessThanOne_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateWishlistItemCommand(
                WishlistItemId.fromUUID(UUID.randomUUID()),
                "Updated description", 
                0, 
                true
            )
        );
    }
    
    @Test
    @DisplayName("Should throw exception when priority is greater than 5 in UpdateWishlistItemCommand")
    void updateWishlistItemCommand_WithPriorityGreaterThanFive_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateWishlistItemCommand(
                WishlistItemId.fromUUID(UUID.randomUUID()),
                "Updated description", 
                6, 
                true
            )
        );
    }
    
    @Test
    @DisplayName("Should create UpdateWishlistItemCommand with valid parameters")
    void updateWishlistItemCommand_WithValidParameters_ShouldCreateInstance() {
        WishlistItemId wishlistItemId = WishlistItemId.fromUUID(UUID.randomUUID());
        String description = "Updated description";
        Integer priority = 4;
        Boolean isPublic = true;
        
        UpdateWishlistItemCommand command = new UpdateWishlistItemCommand(
            wishlistItemId, description, priority, isPublic
        );
        
        assertNotNull(command);
        assertEquals(wishlistItemId, command.id());
        assertEquals(description, command.description());
        assertEquals(priority, command.priority());
        assertEquals(isPublic, command.isPublic());
    }
    
    @Test
    @DisplayName("Should create UpdateWishlistItemCommand with minimum required parameters")
    void updateWishlistItemCommand_WithMinimumRequiredParameters_ShouldCreateInstance() {
        WishlistItemId wishlistItemId = WishlistItemId.fromUUID(UUID.randomUUID());
        
        UpdateWishlistItemCommand command = new UpdateWishlistItemCommand(
            wishlistItemId, null, null, null
        );
        
        assertNotNull(command);
        assertEquals(wishlistItemId, command.id());
        assertNull(command.description());
        assertNull(command.priority());
        assertNull(command.isPublic());
    }
    
    @Test
    @DisplayName("Should allow null priority in UpdateWishlistItemCommand")
    void updateWishlistItemCommand_WithNullPriority_ShouldCreateInstance() {
        WishlistItemId wishlistItemId = WishlistItemId.fromUUID(UUID.randomUUID());
        
        UpdateWishlistItemCommand command = new UpdateWishlistItemCommand(
            wishlistItemId, "Updated description", null, true
        );
        
        assertNotNull(command);
        assertNull(command.priority());
    }
    
    @Test
    @DisplayName("Should call updateWishlistItem with the provided command")
    void updateWishlistItem_ShouldCallWithProvidedCommand() {
        // Create a mock implementation of the interface
        UpdateWishlistItemUseCase useCase = Mockito.mock(UpdateWishlistItemUseCase.class);
        
        // Create a test command
        WishlistItemId wishlistItemId = WishlistItemId.fromUUID(UUID.randomUUID());
        UpdateWishlistItemCommand command = new UpdateWishlistItemCommand(
            wishlistItemId, "Updated description", 4, true
        );
        
        // Mock a wishlist item response
        WishlistItem mockWishlistItem = Mockito.mock(WishlistItem.class);
        when(useCase.updateWishlistItem(command)).thenReturn(mockWishlistItem);
        
        // Call the method
        WishlistItem result = useCase.updateWishlistItem(command);
        
        // Verify the method was called with the correct command
        verify(useCase).updateWishlistItem(command);
        
        // Verify the result
        assertEquals(mockWishlistItem, result);
    }
} 