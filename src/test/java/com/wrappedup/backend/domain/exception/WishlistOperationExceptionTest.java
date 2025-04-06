package com.wrappedup.backend.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class WishlistOperationExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void constructor_WithMessage_ShouldSetMessage() {
        // Arrange
        String expectedMessage = "Error during wishlist operation";
        
        // Act
        WishlistOperationException exception = new WishlistOperationException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    @DisplayName("Should create exception with message and cause")
    void constructor_WithMessageAndCause_ShouldSetMessageAndCause() {
        // Arrange
        String expectedMessage = "Error during wishlist operation with cause";
        Throwable expectedCause = new RuntimeException("Original wishlist error");
        
        // Act
        WishlistOperationException exception = new WishlistOperationException(expectedMessage, expectedCause);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
        assertSame(expectedCause, exception.getCause());
    }
    
    @Test
    @DisplayName("Should extend RuntimeException")
    void wishlistOperationException_ShouldExtendRuntimeException() {
        // Act
        WishlistOperationException exception = new WishlistOperationException("Test");
        
        // Assert
        assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
    }
} 