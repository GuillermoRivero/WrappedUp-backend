package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtTokenPortTest {

    @Test
    @DisplayName("Should generate access token")
    void generateAccessToken_ShouldReturnToken() {
        // Create a mock implementation of the interface
        JwtTokenPort jwtTokenPort = Mockito.mock(JwtTokenPort.class);
        
        // Create test data
        User user = createMockUser();
        String expectedToken = "access.token.jwt";
        
        // Mock the generateAccessToken behavior
        when(jwtTokenPort.generateAccessToken(user)).thenReturn(expectedToken);
        
        // Call the method
        String result = jwtTokenPort.generateAccessToken(user);
        
        // Verify the method was called with the correct user
        verify(jwtTokenPort).generateAccessToken(user);
        
        // Verify the result
        assertEquals(expectedToken, result);
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("Should generate refresh token")
    void generateRefreshToken_ShouldReturnToken() {
        // Create a mock implementation of the interface
        JwtTokenPort jwtTokenPort = Mockito.mock(JwtTokenPort.class);
        
        // Create test data
        User user = createMockUser();
        String expectedToken = "refresh.token.jwt";
        
        // Mock the generateRefreshToken behavior
        when(jwtTokenPort.generateRefreshToken(user)).thenReturn(expectedToken);
        
        // Call the method
        String result = jwtTokenPort.generateRefreshToken(user);
        
        // Verify the method was called with the correct user
        verify(jwtTokenPort).generateRefreshToken(user);
        
        // Verify the result
        assertEquals(expectedToken, result);
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("Should validate token and return user ID")
    void validateTokenAndGetUserId_ShouldReturnUserIdWhenValid() {
        // Create a mock implementation of the interface
        JwtTokenPort jwtTokenPort = Mockito.mock(JwtTokenPort.class);
        
        // Create test data
        String token = "valid.token.jwt";
        String userId = UUID.randomUUID().toString();
        Optional<String> expectedUserId = Optional.of(userId);
        
        // Mock the validateTokenAndGetUserId behavior
        when(jwtTokenPort.validateTokenAndGetUserId(token)).thenReturn(expectedUserId);
        
        // Call the method
        Optional<String> result = jwtTokenPort.validateTokenAndGetUserId(token);
        
        // Verify the method was called with the correct token
        verify(jwtTokenPort).validateTokenAndGetUserId(token);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(userId, result.get());
    }
    
    @Test
    @DisplayName("Should return empty when token is invalid")
    void validateTokenAndGetUserId_ShouldReturnEmptyWhenInvalid() {
        // Create a mock implementation of the interface
        JwtTokenPort jwtTokenPort = Mockito.mock(JwtTokenPort.class);
        
        // Create test data
        String invalidToken = "invalid.token.jwt";
        
        // Mock the validateTokenAndGetUserId behavior
        when(jwtTokenPort.validateTokenAndGetUserId(invalidToken)).thenReturn(Optional.empty());
        
        // Call the method
        Optional<String> result = jwtTokenPort.validateTokenAndGetUserId(invalidToken);
        
        // Verify the method was called with the correct token
        verify(jwtTokenPort).validateTokenAndGetUserId(invalidToken);
        
        // Verify the result
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should detect expired token")
    void isTokenExpired_ShouldReturnTrueWhenExpired() {
        // Create a mock implementation of the interface
        JwtTokenPort jwtTokenPort = Mockito.mock(JwtTokenPort.class);
        
        // Create test data
        String expiredToken = "expired.token.jwt";
        
        // Mock the isTokenExpired behavior
        when(jwtTokenPort.isTokenExpired(expiredToken)).thenReturn(true);
        
        // Call the method
        boolean result = jwtTokenPort.isTokenExpired(expiredToken);
        
        // Verify the method was called with the correct token
        verify(jwtTokenPort).isTokenExpired(expiredToken);
        
        // Verify the result
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should detect non-expired token")
    void isTokenExpired_ShouldReturnFalseWhenNotExpired() {
        // Create a mock implementation of the interface
        JwtTokenPort jwtTokenPort = Mockito.mock(JwtTokenPort.class);
        
        // Create test data
        String validToken = "valid.token.jwt";
        
        // Mock the isTokenExpired behavior
        when(jwtTokenPort.isTokenExpired(validToken)).thenReturn(false);
        
        // Call the method
        boolean result = jwtTokenPort.isTokenExpired(validToken);
        
        // Verify the method was called with the correct token
        verify(jwtTokenPort).isTokenExpired(validToken);
        
        // Verify the result
        assertFalse(result);
    }
    
    private User createMockUser() {
        LocalDateTime now = LocalDateTime.now();
        return User.reconstitute(
                UserId.fromUUID(UUID.randomUUID()),
                new Username("testuser"),
                new Email("test@example.com"),
                "hashedPassword",
                User.Role.USER,
                true,
                now,
                now
        );
    }
} 