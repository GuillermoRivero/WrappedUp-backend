package com.wrappedup.backend.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BookNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void constructor_WithMessage_ShouldSetMessage() {
        // Arrange
        String expectedMessage = "Book not found with ID: 123";
        
        // Act
        BookNotFoundException exception = new BookNotFoundException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    @DisplayName("Should create exception with message and cause")
    void constructor_WithMessageAndCause_ShouldSetMessageAndCause() {
        // Arrange
        String expectedMessage = "Book not found with ISBN: 123456789";
        Throwable expectedCause = new RuntimeException("Database lookup error");
        
        // Act
        BookNotFoundException exception = new BookNotFoundException(expectedMessage, expectedCause);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertSame(expectedCause, exception.getCause());
    }
    
    @Test
    @DisplayName("Should extend RuntimeException")
    void bookNotFoundException_ShouldExtendRuntimeException() {
        // Act
        BookNotFoundException exception = new BookNotFoundException("Test");
        
        // Assert
        assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
    }
} 