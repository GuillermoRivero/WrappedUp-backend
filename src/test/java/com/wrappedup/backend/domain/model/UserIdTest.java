package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    @DisplayName("Should create UserId from existing UUID")
    void fromUUID_WithValidUUID_ShouldCreateUserId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        // Act
        UserId userId = UserId.fromUUID(uuid);
        
        // Assert
        assertNotNull(userId);
        assertEquals(uuid, userId.getValue());
    }
    
    @Test
    @DisplayName("Should create UserId from UUID string")
    void of_WithValidUUIDString_ShouldCreateUserId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        
        // Act
        UserId userId = UserId.of(uuidString);
        
        // Assert
        assertNotNull(userId);
        assertEquals(uuid, userId.getValue());
    }
    
    @Test
    @DisplayName("Should create UserId from UUID object")
    void of_WithUUIDObject_ShouldCreateUserId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        // Act
        UserId userId = UserId.of(uuid);
        
        // Assert
        assertNotNull(userId);
        assertEquals(uuid, userId.getValue());
    }
    
    @Test
    @DisplayName("Should generate new UserId")
    void generate_ShouldCreateUniqueUserId() {
        // Act
        UserId userId1 = UserId.generate();
        UserId userId2 = UserId.generate();
        
        // Assert
        assertNotNull(userId1);
        assertNotNull(userId2);
        assertNotEquals(userId1, userId2);
    }
    
    @Test
    @DisplayName("Should throw exception for null UUID")
    void fromUUID_WithNullUUID_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> UserId.fromUUID(null)
        );
    }
    
    @Test
    @DisplayName("Should throw exception for invalid UUID string")
    void of_WithInvalidUUIDString_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> UserId.of("not-a-uuid")
        );
    }
    
    @Test
    @DisplayName("Equal UserIds should be equal")
    void equals_WithSameUUID_ShouldBeEqual() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UserId userId1 = UserId.fromUUID(uuid);
        UserId userId2 = UserId.fromUUID(uuid);
        
        // Assert
        assertEquals(userId1, userId2);
        assertEquals(userId1.hashCode(), userId2.hashCode());
    }
    
    @Test
    @DisplayName("Different UserIds should not be equal")
    void equals_WithDifferentUUIDs_ShouldNotBeEqual() {
        // Arrange
        UserId userId1 = UserId.generate();
        UserId userId2 = UserId.generate();
        
        // Assert
        assertNotEquals(userId1, userId2);
        assertNotEquals(userId1.hashCode(), userId2.hashCode());
    }
    
    @Test
    @DisplayName("toString should return string representation of UUID")
    void toString_ShouldReturnStringRepresentationOfUUID() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.fromUUID(uuid);
        
        // Assert
        assertEquals(uuid.toString(), userId.toString());
    }
} 