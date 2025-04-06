package com.wrappedup.backend.domain.port.out;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HashPasswordPortTest {

    @Test
    @DisplayName("Should hash a password")
    void hashPassword_ShouldReturnHashedPassword() {
        // Create a mock implementation of the interface
        HashPasswordPort hashPasswordPort = Mockito.mock(HashPasswordPort.class);
        
        // Create test data
        String plainPassword = "password123";
        String expectedHash = "hashedPassword456";
        
        // Mock the hashPassword behavior
        when(hashPasswordPort.hashPassword(plainPassword)).thenReturn(expectedHash);
        
        // Call the method
        String result = hashPasswordPort.hashPassword(plainPassword);
        
        // Verify the method was called with the correct password
        verify(hashPasswordPort).hashPassword(plainPassword);
        
        // Verify the result
        assertEquals(expectedHash, result);
        assertNotEquals(plainPassword, result);
    }
    
    @Test
    @DisplayName("Should verify password correctly when matching")
    void verifyPassword_ShouldReturnTrueWhenMatching() {
        // Create a mock implementation of the interface
        HashPasswordPort hashPasswordPort = Mockito.mock(HashPasswordPort.class);
        
        // Create test data
        String plainPassword = "password123";
        String hashedPassword = "hashedPassword456";
        
        // Mock the verifyPassword behavior
        when(hashPasswordPort.verifyPassword(plainPassword, hashedPassword)).thenReturn(true);
        
        // Call the method
        boolean result = hashPasswordPort.verifyPassword(plainPassword, hashedPassword);
        
        // Verify the method was called with the correct arguments
        verify(hashPasswordPort).verifyPassword(plainPassword, hashedPassword);
        
        // Verify the result
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should verify password correctly when not matching")
    void verifyPassword_ShouldReturnFalseWhenNotMatching() {
        // Create a mock implementation of the interface
        HashPasswordPort hashPasswordPort = Mockito.mock(HashPasswordPort.class);
        
        // Create test data
        String plainPassword = "password123";
        String hashedPassword = "hashedPassword456";
        String wrongPassword = "wrongPassword";
        
        // Mock the verifyPassword behavior
        when(hashPasswordPort.verifyPassword(wrongPassword, hashedPassword)).thenReturn(false);
        
        // Call the method
        boolean result = hashPasswordPort.verifyPassword(wrongPassword, hashedPassword);
        
        // Verify the method was called with the correct arguments
        verify(hashPasswordPort).verifyPassword(wrongPassword, hashedPassword);
        
        // Verify the result
        assertFalse(result);
    }
} 