package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    @DisplayName("Should create valid email")
    void constructor_WithValidEmail_ShouldCreateEmail() {
        // Act
        Email email = new Email("test@example.com");
        
        // Assert
        assertEquals("test@example.com", email.getValue());
    }
    
    @Test
    @DisplayName("Should convert email to lowercase")
    void constructor_WithMixedCaseEmail_ShouldConvertToLowercase() {
        // Act
        Email email = new Email("Test.User@Example.COM");
        
        // Assert
        assertEquals("test.user@example.com", email.getValue());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "user@domain.com", 
        "user.name@domain.com", 
        "user+tag@domain.com", 
        "user-name@domain.com", 
        "user123@domain.com", 
        "user@sub.domain.com"
    })
    @DisplayName("Should accept valid email formats")
    void constructor_WithVariousValidFormats_ShouldCreateEmail(String validEmail) {
        // Act
        Email email = new Email(validEmail);
        
        // Assert
        assertNotNull(email);
        assertEquals(validEmail.toLowerCase(), email.getValue());
    }
    
    @Test
    @DisplayName("Should throw exception for null email")
    void constructor_WithNullEmail_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Email(null)
        );
        
        assertEquals("Email cannot be null or blank", exception.getMessage());
    }
    
    @Test
    @DisplayName("Should throw exception for blank email")
    void constructor_WithBlankEmail_ShouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Email(" ")
        );
        
        assertEquals("Email cannot be null or blank", exception.getMessage());
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "plaintext", 
        "missing@tld", 
        "@missing-username.com", 
        "user@", 
        "user@.com", 
        "user@domain@domain.com", 
        "user name@domain.com"
    })
    @DisplayName("Should throw exception for invalid email formats")
    void constructor_WithInvalidFormats_ShouldThrowException(String invalidEmail) {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Email(invalidEmail)
        );
        
        assertEquals("Invalid email format", exception.getMessage());
    }
    
    @Test
    @DisplayName("Equal emails should be equal")
    void equals_WithSameEmail_ShouldBeEqual() {
        // Arrange
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("test@example.com");
        
        // Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
    
    @Test
    @DisplayName("Different emails should not be equal")
    void equals_WithDifferentEmails_ShouldNotBeEqual() {
        // Arrange
        Email email1 = new Email("test1@example.com");
        Email email2 = new Email("test2@example.com");
        
        // Assert
        assertNotEquals(email1, email2);
        assertNotEquals(email1.hashCode(), email2.hashCode());
    }
    
    @Test
    @DisplayName("Case-insensitive emails should be equal")
    void equals_WithDifferentCase_ShouldBeEqual() {
        // Arrange
        Email email1 = new Email("test@example.com");
        Email email2 = new Email("TEST@EXAMPLE.COM");
        
        // Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
    
    @Test
    @DisplayName("toString should return email value")
    void toString_ShouldReturnEmailValue() {
        // Arrange
        String emailValue = "test@example.com";
        Email email = new Email(emailValue);
        
        // Act & Assert
        assertEquals(emailValue, email.toString());
    }
} 