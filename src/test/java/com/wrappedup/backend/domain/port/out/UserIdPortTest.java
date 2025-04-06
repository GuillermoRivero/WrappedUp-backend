package com.wrappedup.backend.domain.port.out;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdPortTest {

    @Test
    @DisplayName("Should extract user ID from valid token")
    void extractUserId_ShouldReturnUuidWhenTokenIsValid() {
        // Create a mock implementation of the interface
        UserIdPort userIdPort = Mockito.mock(UserIdPort.class);
        
        // Create test data
        String token = "valid.token.jwt";
        UUID expectedUuid = UUID.randomUUID();
        
        // Mock the extractUserId behavior
        when(userIdPort.extractUserId(token)).thenReturn(expectedUuid);
        
        // Call the method
        UUID result = userIdPort.extractUserId(token);
        
        // Verify the method was called with the correct token
        verify(userIdPort).extractUserId(token);
        
        // Verify the result
        assertEquals(expectedUuid, result);
    }
    
    @Test
    @DisplayName("Should throw exception when token is invalid")
    void extractUserId_ShouldThrowExceptionWhenTokenIsInvalid() {
        // Create a mock implementation of the interface
        UserIdPort userIdPort = Mockito.mock(UserIdPort.class);
        
        // Create test data
        String invalidToken = "invalid.token.jwt";
        
        // Mock the extractUserId behavior to throw exception
        when(userIdPort.extractUserId(invalidToken)).thenThrow(new IllegalArgumentException("Invalid token"));
        
        // Verify the exception is thrown
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userIdPort.extractUserId(invalidToken);
        });
        
        // Verify the exception message
        assertEquals("Invalid token", exception.getMessage());
    }
} 