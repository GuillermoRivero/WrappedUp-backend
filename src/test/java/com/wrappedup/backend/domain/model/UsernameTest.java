package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

    @Test
    @DisplayName("Should create valid username")
    void constructor_WithValidUsername_ShouldCreateUsername() {
        // Act
        Username username = new Username("testuser");
        
        // Assert
        assertEquals("testuser", username.getValue());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "user123", 
        "test_user", 
        "user-name", 
        "User Name", 
        "abc" // Minimum length
    })
    @DisplayName("Should accept valid usernames")
    void constructor_WithValidUsernames_ShouldCreateUsername(String validUsername) {
        // Act
        Username username = new Username(validUsername);
        
        // Assert
        assertNotNull(username);
        assertEquals(validUsername, username.getValue());
    }
    
    @Test
    @DisplayName("Should throw exception for null username")
    void constructor_WithNullUsername_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Username(null)
        );
        
        assertEquals("Username cannot be null or blank", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception for blank username")
    void constructor_WithBlankUsername_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Username(" ")
        );
        
        assertEquals("Username cannot be null or blank", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception for too short username")
    void constructor_WithTooShortUsername_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Username("ab") // Only 2 characters
        );
        
        assertEquals("Username must be between 3 and 50 characters", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception for too long username")
    void constructor_WithTooLongUsername_ShouldThrowException() {
        // Arrange
        String tooLongUsername = "a".repeat(51); // 51 characters
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Username(tooLongUsername)
        );
        
        assertEquals("Username must be between 3 and 50 characters", exception.getMessage());
    }
    
    @Test
    @DisplayName("Equal usernames should be equal")
    void equals_WithSameUsername_ShouldBeEqual() {
        // Arrange
        Username username1 = new Username("testuser");
        Username username2 = new Username("testuser");
        
        // Assert
        assertEquals(username1, username2);
        assertEquals(username1.hashCode(), username2.hashCode());
    }
    
    @Test
    @DisplayName("Different usernames should not be equal")
    void equals_WithDifferentUsernames_ShouldNotBeEqual() {
        // Arrange
        Username username1 = new Username("testuser1");
        Username username2 = new Username("testuser2");
        
        // Assert
        assertNotEquals(username1, username2);
        assertNotEquals(username1.hashCode(), username2.hashCode());
    }
    
    @Test
    @DisplayName("Case-sensitive usernames should not be equal")
    void equals_WithDifferentCase_ShouldNotBeEqual() {
        // Arrange
        Username username1 = new Username("testuser");
        Username username2 = new Username("TestUser");
        
        // Assert
        assertNotEquals(username1, username2);
        assertNotEquals(username1.hashCode(), username2.hashCode());
    }
    
    @Test
    @DisplayName("toString should return username value")
    void toString_ShouldReturnUsernameValue() {
        // Arrange
        String usernameValue = "testuser";
        Username username = new Username(usernameValue);
        
        // Act & Assert
        assertEquals(usernameValue, username.toString());
    }
} 