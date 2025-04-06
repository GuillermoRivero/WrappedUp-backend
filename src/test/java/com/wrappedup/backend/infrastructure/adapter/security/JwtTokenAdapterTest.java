package com.wrappedup.backend.infrastructure.adapter.security;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenAdapterTest {

    private JwtTokenAdapter jwtTokenAdapter;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create with short expiration for testing
        jwtTokenAdapter = new JwtTokenAdapter(
                "test-secret-key-that-is-long-enough-for-hs512-signature-algorithm-test-secret-key-that-is-long-enough-for-hs512-signature-algorithm",
                5000, // 5 seconds for access token
                10000  // 10 seconds for refresh token
        );

        // Create a test user
        UserId userId = UserId.of(UUID.randomUUID());
        Username username = new Username("testuser");
        Email email = new Email("test@example.com");
        testUser = User.reconstitute(
                userId,
                username,
                email,
                "hashedPassword",
                User.Role.USER,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        // Act
        String token = jwtTokenAdapter.generateAccessToken(testUser);

        // Assert
        assertNotNull(token);
        Optional<String> userId = jwtTokenAdapter.validateTokenAndGetUserId(token);
        assertTrue(userId.isPresent());
        assertEquals(testUser.getId().getValue().toString(), userId.get());
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        // Act
        String token = jwtTokenAdapter.generateRefreshToken(testUser);

        // Assert
        assertNotNull(token);
        Optional<String> userId = jwtTokenAdapter.validateTokenAndGetUserId(token);
        assertTrue(userId.isPresent());
        assertEquals(testUser.getId().getValue().toString(), userId.get());
    }

    @Test
    void validateTokenAndGetUserId_ShouldReturnEmpty_ForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.value";

        // Act
        Optional<String> userId = jwtTokenAdapter.validateTokenAndGetUserId(invalidToken);

        // Assert
        assertFalse(userId.isPresent());
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_ForExpiredToken() throws Exception {
        // Arrange
        // Create adapter with very short expiration
        JwtTokenAdapter shortExpirationAdapter = new JwtTokenAdapter(
                "test-secret-key-that-is-long-enough-for-hs512-signature-algorithm-test-secret-key-that-is-long-enough-for-hs512-signature-algorithm",
                1, // 1ms expiration 
                1  // 1ms expiration
        );
        
        String token = shortExpirationAdapter.generateAccessToken(testUser);
        
        // Wait to ensure token expires
        Thread.sleep(10);

        // Act
        boolean isExpired = shortExpirationAdapter.isTokenExpired(token);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void isTokenExpired_ShouldReturnFalse_ForValidToken() {
        // Arrange
        String token = jwtTokenAdapter.generateAccessToken(testUser);

        // Act
        boolean isExpired = jwtTokenAdapter.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }
} 