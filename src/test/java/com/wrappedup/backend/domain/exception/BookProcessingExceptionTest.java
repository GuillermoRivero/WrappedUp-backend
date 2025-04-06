package com.wrappedup.backend.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class BookProcessingExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void constructor_WithMessage_ShouldSetMessage() {
        // Arrange
        String expectedMessage = "Error processing book";
        
        // Act
        BookProcessingException exception = new BookProcessingException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    @DisplayName("Should create exception with message and cause")
    void constructor_WithMessageAndCause_ShouldSetMessageAndCause() {
        // Arrange
        String expectedMessage = "Error processing book with cause";
        Throwable expectedCause = new RuntimeException("Original processing error");
        
        // Act
        BookProcessingException exception = new BookProcessingException(expectedMessage, expectedCause);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertSame(expectedCause, exception.getCause());
    }
    
    @Test
    @DisplayName("Should extend RuntimeException")
    void bookProcessingException_ShouldExtendRuntimeException() {
        // Act
        BookProcessingException exception = new BookProcessingException("Test");
        
        // Assert
        assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
    }
} 