package com.wrappedup.backend.infrastructure.adapter.security;

import com.wrappedup.backend.domain.port.out.JwtTokenPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtUserIdAdapterTest {

    @Mock
    private JwtTokenPort jwtTokenPort;

    @InjectMocks
    private JwtUserIdAdapter jwtUserIdAdapter;

    @Test
    void extractUserId_ShouldReturnUuid_WhenTokenIsValid() {
        // Arrange
        String validToken = "valid.jwt.token";
        UUID expectedUuid = UUID.randomUUID();
        String validUuidString = expectedUuid.toString();
        
        when(jwtTokenPort.validateTokenAndGetUserId(validToken))
                .thenReturn(Optional.of(validUuidString));

        // Act
        UUID result = jwtUserIdAdapter.extractUserId(validToken);

        // Assert
        assertEquals(expectedUuid, result);
        verify(jwtTokenPort).validateTokenAndGetUserId(validToken);
    }

    @Test
    void extractUserId_ShouldThrowException_WhenTokenValidationFails() {
        // Arrange
        String invalidToken = "invalid.token";
        when(jwtTokenPort.validateTokenAndGetUserId(invalidToken))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jwtUserIdAdapter.extractUserId(invalidToken));
        assertEquals("Invalid token", exception.getMessage());
        verify(jwtTokenPort).validateTokenAndGetUserId(invalidToken);
    }

    @Test
    void extractUserId_ShouldThrowException_WhenIdFormatIsInvalid() {
        // Arrange
        String tokenWithInvalidUuid = "token.with.invalid.uuid";
        when(jwtTokenPort.validateTokenAndGetUserId(tokenWithInvalidUuid))
                .thenReturn(Optional.of("not-a-valid-uuid"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jwtUserIdAdapter.extractUserId(tokenWithInvalidUuid));
        assertEquals("Invalid ID format. IDs must be valid UUIDs.", exception.getMessage());
        verify(jwtTokenPort).validateTokenAndGetUserId(tokenWithInvalidUuid);
    }

    @Test
    void extractUserId_ShouldThrowException_WhenUnexpectedErrorOccurs() {
        // Arrange
        String problematicToken = "problematic.token";
        when(jwtTokenPort.validateTokenAndGetUserId(problematicToken))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> jwtUserIdAdapter.extractUserId(problematicToken));
        assertEquals("Could not process token", exception.getMessage());
        verify(jwtTokenPort).validateTokenAndGetUserId(problematicToken);
    }
} 