package com.wrappedup.backend.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BookPersistenceExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void constructor_WithMessage_ShouldSetMessage() {
        // Arrange
        String expectedMessage = "Failed to save book to database";
        
        // Act
        BookPersistenceException exception = new BookPersistenceException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    @DisplayName("Should create exception with message and cause")
    void constructor_WithMessageAndCause_ShouldSetMessageAndCause() {
        // Arrange
        String expectedMessage = "Failed to save book due to constraint violation";
        Throwable expectedCause = new RuntimeException("Database constraint violation");
        
        // Act
        BookPersistenceException exception = new BookPersistenceException(expectedMessage, expectedCause);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertSame(expectedCause, exception.getCause());
    }
    
    @Test
    @DisplayName("Should extend RuntimeException")
    void bookPersistenceException_ShouldExtendRuntimeException() {
        // Act
        BookPersistenceException exception = new BookPersistenceException("Test");
        
        // Assert
        assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
    }
} 