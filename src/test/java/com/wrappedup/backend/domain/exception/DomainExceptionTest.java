package com.wrappedup.backend.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DomainExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void constructor_WithMessage_ShouldSetMessage() {
        // Arrange
        String expectedMessage = "Test domain exception";
        
        // Act
        DomainException exception = new DomainException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    @DisplayName("Should create exception with message and cause")
    void constructor_WithMessageAndCause_ShouldSetMessageAndCause() {
        // Arrange
        String expectedMessage = "Test domain exception with cause";
        Throwable expectedCause = new RuntimeException("Original cause");
        
        // Act
        DomainException exception = new DomainException(expectedMessage, expectedCause);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertSame(expectedCause, exception.getCause());
    }
} 