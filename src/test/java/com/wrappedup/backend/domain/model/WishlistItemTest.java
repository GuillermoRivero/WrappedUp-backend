package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WishlistItemTest {

    private final WishlistItemId wishlistItemId = WishlistItemId.generate();
    private final UserId userId = UserId.generate();
    private final BookId bookId = BookId.generate();
    private final String description = "Want to read this next";
    private final Integer priority = 2;
    private final boolean isPublic = true;
    private final LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    private final LocalDateTime updatedAt = LocalDateTime.now();
    
    @Test
    @DisplayName("Should create a new wishlist item with all fields")
    void createNewWishlistItem_ShouldCreateItemWithAllFields() {
        // Act
        WishlistItem item = WishlistItem.createNewWishlistItem(
                userId, bookId, description, priority, isPublic
        );
        
        // Assert
        assertNotNull(item);
        assertNotNull(item.getId());
        assertEquals(userId, item.getUserId());
        assertEquals(bookId, item.getBookId());
        assertEquals(description, item.getDescription());
        assertEquals(priority, item.getPriority());
        assertEquals(isPublic, item.isPublic());
        assertNotNull(item.getCreatedAt());
        assertNotNull(item.getUpdatedAt());
        assertEquals(item.getCreatedAt(), item.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should use default priority when creating a new wishlist item with null priority")
    void createNewWishlistItem_WithNullPriority_ShouldUseDefaultPriority() {
        // Act
        WishlistItem item = WishlistItem.createNewWishlistItem(
                userId, bookId, description, null, isPublic
        );
        
        // Assert
        assertEquals(3, item.getPriority()); // Default priority should be 3
    }
    
    @Test
    @DisplayName("Should reconstitute an existing wishlist item with all fields")
    void reconstitute_ShouldCreateItemWithAllFields() {
        // Act
        WishlistItem item = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, priority, isPublic, createdAt, updatedAt
        );
        
        // Assert
        assertNotNull(item);
        assertEquals(wishlistItemId, item.getId());
        assertEquals(userId, item.getUserId());
        assertEquals(bookId, item.getBookId());
        assertEquals(description, item.getDescription());
        assertEquals(priority, item.getPriority());
        assertEquals(isPublic, item.isPublic());
        assertEquals(createdAt, item.getCreatedAt());
        assertEquals(updatedAt, item.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should use default priority when reconstituting with null priority")
    void reconstitute_WithNullPriority_ShouldUseDefaultPriority() {
        // Act
        WishlistItem item = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, null, isPublic, createdAt, updatedAt
        );
        
        // Assert
        assertEquals(3, item.getPriority()); // Default priority should be 3
    }
    
    @Test
    @DisplayName("Should use default visibility when reconstituting with null visibility")
    void reconstitute_WithNullPublicFlag_ShouldUseDefaultVisibility() {
        // Act
        WishlistItem item = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, priority, null, createdAt, updatedAt
        );
        
        // Assert
        assertFalse(item.isPublic()); // Default visibility should be false
    }
    
    @Test
    @DisplayName("Should use createdAt as updatedAt when reconstituting with null updatedAt")
    void reconstitute_WithNullUpdatedAt_ShouldUseCreatedAtAsUpdatedAt() {
        // Act
        WishlistItem item = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, priority, isPublic, createdAt, null
        );
        
        // Assert
        assertEquals(createdAt, item.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should update wishlist item details")
    void updateDetails_ShouldChangeItemDetails() {
        // Arrange
        WishlistItem item = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, priority, isPublic, createdAt, updatedAt
        );
        
        String newDescription = "Updated description";
        Integer newPriority = 1;
        boolean newIsPublic = false;
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        item.updateDetails(newDescription, newPriority, newIsPublic);
        
        // Assert
        assertEquals(newDescription, item.getDescription());
        assertEquals(newPriority, item.getPriority());
        assertEquals(newIsPublic, item.isPublic());
        assertTrue(item.getUpdatedAt().isAfter(beforeUpdate) || 
                item.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should not change fields when updating with null values")
    void updateDetails_WithNullValues_ShouldNotChangeFields() {
        // Arrange
        WishlistItem item = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, priority, isPublic, createdAt, updatedAt
        );
        
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        item.updateDetails(null, null, null);
        
        // Assert
        assertEquals(description, item.getDescription());
        assertEquals(priority, item.getPriority());
        assertEquals(isPublic, item.isPublic());
        assertTrue(item.getUpdatedAt().isAfter(beforeUpdate) || 
                item.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should accept valid priorities between 1 and 5")
    void updateDetails_WithValidPriority_ShouldUpdatePriority() {
        // Arrange
        WishlistItem item = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, priority, isPublic, createdAt, updatedAt
        );
        
        // Act & Assert - Check all valid priorities
        for (int validPriority = 1; validPriority <= 5; validPriority++) {
            item.updateDetails(null, validPriority, null);
            assertEquals(validPriority, item.getPriority());
        }
    }
    
    @Test
    @DisplayName("Equal wishlist items should be equal")
    void equals_WithSameId_ShouldBeEqual() {
        // Arrange
        WishlistItem item1 = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, priority, isPublic, createdAt, updatedAt
        );
        
        WishlistItem item2 = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, "Different description", 5, false, createdAt, updatedAt
        );
        
        // Assert
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
    
    @Test
    @DisplayName("Wishlist items with different IDs should not be equal")
    void equals_WithDifferentIds_ShouldNotBeEqual() {
        // Arrange
        WishlistItem item1 = WishlistItem.reconstitute(
                wishlistItemId, userId, bookId, description, priority, isPublic, createdAt, updatedAt
        );
        
        WishlistItem item2 = WishlistItem.reconstitute(
                WishlistItemId.generate(), userId, bookId, description, priority, isPublic, createdAt, updatedAt
        );
        
        // Assert
        assertNotEquals(item1, item2);
        assertNotEquals(item1.hashCode(), item2.hashCode());
    }
} 