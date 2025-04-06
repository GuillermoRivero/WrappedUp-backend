package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookIdTest {

    @Test
    @DisplayName("Should create BookId from existing UUID")
    void fromUUID_WithValidUUID_ShouldCreateBookId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        // Act
        BookId bookId = BookId.fromUUID(uuid);
        
        // Assert
        assertNotNull(bookId);
        assertEquals(uuid, bookId.getValue());
    }
    
    @Test
    @DisplayName("Should create BookId from UUID string")
    void of_WithValidUUIDString_ShouldCreateBookId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        
        // Act
        BookId bookId = BookId.of(uuidString);
        
        // Assert
        assertNotNull(bookId);
        assertEquals(uuid, bookId.getValue());
    }
    
    @Test
    @DisplayName("Should create BookId from UUID object")
    void of_WithUUIDObject_ShouldCreateBookId() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        
        // Act
        BookId bookId = BookId.of(uuid);
        
        // Assert
        assertNotNull(bookId);
        assertEquals(uuid, bookId.getValue());
    }
    
    @Test
    @DisplayName("Should generate new BookId")
    void generate_ShouldCreateUniqueBookId() {
        // Act
        BookId bookId1 = BookId.generate();
        BookId bookId2 = BookId.generate();
        
        // Assert
        assertNotNull(bookId1);
        assertNotNull(bookId2);
        assertNotEquals(bookId1, bookId2);
    }
    
    @Test
    @DisplayName("Should throw exception for null UUID")
    void fromUUID_WithNullUUID_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> BookId.fromUUID(null)
        );
    }
    
    @Test
    @DisplayName("Should throw exception for invalid UUID string")
    void of_WithInvalidUUIDString_ShouldThrowException() {
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> BookId.of("not-a-uuid")
        );
    }
    
    @Test
    @DisplayName("Equal BookIds should be equal")
    void equals_WithSameUUID_ShouldBeEqual() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        BookId bookId1 = BookId.fromUUID(uuid);
        BookId bookId2 = BookId.fromUUID(uuid);
        
        // Assert
        assertEquals(bookId1, bookId2);
        assertEquals(bookId1.hashCode(), bookId2.hashCode());
    }
    
    @Test
    @DisplayName("Different BookIds should not be equal")
    void equals_WithDifferentUUIDs_ShouldNotBeEqual() {
        // Arrange
        BookId bookId1 = BookId.generate();
        BookId bookId2 = BookId.generate();
        
        // Assert
        assertNotEquals(bookId1, bookId2);
        assertNotEquals(bookId1.hashCode(), bookId2.hashCode());
    }
    
    @Test
    @DisplayName("toString should return string representation of UUID")
    void toString_ShouldReturnStringRepresentationOfUUID() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        BookId bookId = BookId.fromUUID(uuid);
        
        // Assert
        assertEquals(uuid.toString(), bookId.toString());
    }
} 