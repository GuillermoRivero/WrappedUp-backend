package com.wrappedup.backend.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void constructor_WithMessage_ShouldSetMessage() {
        // Arrange
        String expectedMessage = "Custom user exists message";
        
        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException(expectedMessage);
        
        // Assert
        assertEquals(expectedMessage, exception.getMessage());
    }
    
    @Test
    @DisplayName("Should have correct constant for username exists message")
    void usernameExistsConstant_ShouldHaveCorrectMessage() {
        // Assert
        assertEquals("A user with this username already exists", UserAlreadyExistsException.USERNAME_EXISTS);
    }
    
    @Test
    @DisplayName("Should have correct constant for email exists message")
    void emailExistsConstant_ShouldHaveCorrectMessage() {
        // Assert
        assertEquals("A user with this email already exists", UserAlreadyExistsException.EMAIL_EXISTS);
    }
    
    @Test
    @DisplayName("Should create exception with username exists constant")
    void constructor_WithUsernameExistsConstant_ShouldSetCorrectMessage() {
        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException(UserAlreadyExistsException.USERNAME_EXISTS);
        
        // Assert
        assertEquals("A user with this username already exists", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should create exception with email exists constant")
    void constructor_WithEmailExistsConstant_ShouldSetCorrectMessage() {
        // Act
        UserAlreadyExistsException exception = new UserAlreadyExistsException(UserAlreadyExistsException.EMAIL_EXISTS);
        
        // Assert
        assertEquals("A user with this email already exists", exception.getMessage());
    }
} 