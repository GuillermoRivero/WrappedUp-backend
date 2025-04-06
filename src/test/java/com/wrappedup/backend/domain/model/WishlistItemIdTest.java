package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WishlistItemIdTest {

    @Test
    @DisplayName("Should create WishlistItemId from existing UUID")
    void fromUUID_WithValidUUID_ShouldCreateWishlistItemId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        // Act
        WishlistItemId wishlistItemId = WishlistItemId.fromUUID(uuid);
        
        // Assert
        assertNotNull(wishlistItemId);
        assertEquals(uuid, wishlistItemId.getValue());
    }
    
    @Test
    @DisplayName("Should create WishlistItemId from UUID string")
    void fromString_WithValidUUIDString_ShouldCreateWishlistItemId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        
        // Act
        WishlistItemId wishlistItemId = WishlistItemId.fromString(uuidString);
        
        // Assert
        assertNotNull(wishlistItemId);
        assertEquals(uuid, wishlistItemId.getValue());
    }
    
    @Test
    @DisplayName("Should generate new WishlistItemId")
    void generate_ShouldCreateUniqueWishlistItemId() {
        // Act
        WishlistItemId wishlistItemId1 = WishlistItemId.generate();
        WishlistItemId wishlistItemId2 = WishlistItemId.generate();
        
        // Assert
        assertNotNull(wishlistItemId1);
        assertNotNull(wishlistItemId2);
        assertNotEquals(wishlistItemId1, wishlistItemId2);
    }
    
    @Test
    @DisplayName("Should throw exception for null UUID")
    void fromUUID_WithNullUUID_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> WishlistItemId.fromUUID(null)
        );
    }
    
    @Test
    @DisplayName("Should throw exception for invalid UUID string")
    void fromString_WithInvalidUUIDString_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> WishlistItemId.fromString("not-a-uuid")
        );
    }
    
    @Test
    @DisplayName("Equal WishlistItemIds should be equal")
    void equals_WithSameUUID_ShouldBeEqual() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        WishlistItemId wishlistItemId1 = WishlistItemId.fromUUID(uuid);
        WishlistItemId wishlistItemId2 = WishlistItemId.fromUUID(uuid);
        
        // Assert
        assertEquals(wishlistItemId1, wishlistItemId2);
        assertEquals(wishlistItemId1.hashCode(), wishlistItemId2.hashCode());
    }
    
    @Test
    @DisplayName("Different WishlistItemIds should not be equal")
    void equals_WithDifferentUUIDs_ShouldNotBeEqual() {
        // Arrange
        WishlistItemId wishlistItemId1 = WishlistItemId.generate();
        WishlistItemId wishlistItemId2 = WishlistItemId.generate();
        
        // Assert
        assertNotEquals(wishlistItemId1, wishlistItemId2);
        assertNotEquals(wishlistItemId1.hashCode(), wishlistItemId2.hashCode());
    }
    
    @Test
    @DisplayName("toString should return string representation of UUID")
    void toString_ShouldReturnStringRepresentationOfUUID() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        WishlistItemId wishlistItemId = WishlistItemId.fromUUID(uuid);
        
        // Assert
        assertEquals(uuid.toString(), wishlistItemId.toString());
    }
} 