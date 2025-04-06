package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileIdTest {

    @Test
    @DisplayName("Should create UserProfileId from existing UUID")
    void of_WithValidUUID_ShouldCreateUserProfileId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        // Act
        UserProfileId userProfileId = UserProfileId.of(uuid);
        
        // Assert
        assertNotNull(userProfileId);
        assertEquals(uuid, userProfileId.getValue());
    }
    
    @Test
    @DisplayName("Should create UserProfileId from UUID string")
    void of_WithValidUUIDString_ShouldCreateUserProfileId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        
        // Act
        UserProfileId userProfileId = UserProfileId.of(uuidString);
        
        // Assert
        assertNotNull(userProfileId);
        assertEquals(uuid, userProfileId.getValue());
    }
    
    @Test
    @DisplayName("Should generate new UserProfileId")
    void generate_ShouldCreateUniqueUserProfileId() {
        // Act
        UserProfileId userProfileId1 = UserProfileId.generate();
        UserProfileId userProfileId2 = UserProfileId.generate();
        
        // Assert
        assertNotNull(userProfileId1);
        assertNotNull(userProfileId2);
        assertNotEquals(userProfileId1, userProfileId2);
    }
    
    @Test
    @DisplayName("Should throw exception for null UUID")
    void of_WithNullUUID_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> UserProfileId.of((UUID) null)
        );
    }
    
    @Test
    @DisplayName("Should throw exception for invalid UUID string")
    void of_WithInvalidUUIDString_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> UserProfileId.of("not-a-uuid")
        );
    }
    
    @Test
    @DisplayName("Equal UserProfileIds should be equal")
    void equals_WithSameUUID_ShouldBeEqual() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UserProfileId userProfileId1 = UserProfileId.of(uuid);
        UserProfileId userProfileId2 = UserProfileId.of(uuid);
        
        // Assert
        assertEquals(userProfileId1, userProfileId2);
        assertEquals(userProfileId1.hashCode(), userProfileId2.hashCode());
    }
    
    @Test
    @DisplayName("Different UserProfileIds should not be equal")
    void equals_WithDifferentUUIDs_ShouldNotBeEqual() {
        // Arrange
        UserProfileId userProfileId1 = UserProfileId.generate();
        UserProfileId userProfileId2 = UserProfileId.generate();
        
        // Assert
        assertNotEquals(userProfileId1, userProfileId2);
        assertNotEquals(userProfileId1.hashCode(), userProfileId2.hashCode());
    }
    
    @Test
    @DisplayName("toString should return string representation of UUID")
    void toString_ShouldReturnStringRepresentationOfUUID() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UserProfileId userProfileId = UserProfileId.of(uuid);
        
        // Assert
        assertEquals(uuid.toString(), userProfileId.toString());
    }
} 