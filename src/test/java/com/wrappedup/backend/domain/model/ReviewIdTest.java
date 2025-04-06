package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReviewIdTest {

    @Test
    @DisplayName("Should create ReviewId from existing UUID")
    void fromUUID_WithValidUUID_ShouldCreateReviewId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        // Act
        ReviewId reviewId = ReviewId.fromUUID(uuid);
        
        // Assert
        assertNotNull(reviewId);
        assertEquals(uuid, reviewId.getValue());
    }
    
    @Test
    @DisplayName("Should create ReviewId from UUID string")
    void fromString_WithValidUUIDString_ShouldCreateReviewId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        
        // Act
        ReviewId reviewId = ReviewId.fromString(uuidString);
        
        // Assert
        assertNotNull(reviewId);
        assertEquals(uuid, reviewId.getValue());
    }
    
    @Test
    @DisplayName("Should generate new ReviewId")
    void generate_ShouldCreateUniqueReviewId() {
        // Act
        ReviewId reviewId1 = ReviewId.generate();
        ReviewId reviewId2 = ReviewId.generate();
        
        // Assert
        assertNotNull(reviewId1);
        assertNotNull(reviewId2);
        assertNotEquals(reviewId1, reviewId2);
    }
    
    @Test
    @DisplayName("Should throw exception for null UUID")
    void fromUUID_WithNullUUID_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> ReviewId.fromUUID(null)
        );
    }
    
    @Test
    @DisplayName("Should throw exception for invalid UUID string")
    void fromString_WithInvalidUUIDString_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> ReviewId.fromString("not-a-uuid")
        );
    }
    
    @Test
    @DisplayName("Equal ReviewIds should be equal")
    void equals_WithSameUUID_ShouldBeEqual() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        ReviewId reviewId1 = ReviewId.fromUUID(uuid);
        ReviewId reviewId2 = ReviewId.fromUUID(uuid);
        
        // Assert
        assertEquals(reviewId1, reviewId2);
        assertEquals(reviewId1.hashCode(), reviewId2.hashCode());
    }
    
    @Test
    @DisplayName("Different ReviewIds should not be equal")
    void equals_WithDifferentUUIDs_ShouldNotBeEqual() {
        // Arrange
        ReviewId reviewId1 = ReviewId.generate();
        ReviewId reviewId2 = ReviewId.generate();
        
        // Assert
        assertNotEquals(reviewId1, reviewId2);
        assertNotEquals(reviewId1.hashCode(), reviewId2.hashCode());
    }
    
    @Test
    @DisplayName("toString should return string representation of UUID")
    void toString_ShouldReturnStringRepresentationOfUUID() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        ReviewId reviewId = ReviewId.fromUUID(uuid);
        
        // Assert
        assertEquals(uuid.toString(), reviewId.toString());
    }
} 