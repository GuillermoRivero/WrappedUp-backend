package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.RefreshTokenUseCase.RefreshTokenCommand;
import com.wrappedup.backend.domain.port.in.RefreshTokenUseCase.RefreshTokenResult;
import com.wrappedup.backend.domain.port.out.JwtTokenPort;
import com.wrappedup.backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private JwtTokenPort jwtTokenPort;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User validUser;
    private final String USER_ID = UUID.randomUUID().toString();
    private final String REFRESH_TOKEN = "refresh.token.jwt";
    private final String NEW_ACCESS_TOKEN = "new.access.token.jwt";
    private final String NEW_REFRESH_TOKEN = "new.refresh.token.jwt";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        validUser = User.reconstitute(
                UserId.of(USER_ID),
                new Username("testuser"),
                new Email("test@example.com"),
                "passwordHash",
                User.Role.USER,
                true,
                now,
                now
        );
    }

    @Test
    @DisplayName("Should successfully refresh token with valid token")
    void refreshToken_WithValidToken_ShouldReturnNewTokens() {
        // Arrange
        RefreshTokenCommand command = new RefreshTokenCommand(REFRESH_TOKEN);
        
        when(jwtTokenPort.isTokenExpired(REFRESH_TOKEN)).thenReturn(false);
        when(jwtTokenPort.validateTokenAndGetUserId(REFRESH_TOKEN)).thenReturn(Optional.of(USER_ID));
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(validUser));
        when(jwtTokenPort.generateAccessToken(validUser)).thenReturn(NEW_ACCESS_TOKEN);
        when(jwtTokenPort.generateRefreshToken(validUser)).thenReturn(NEW_REFRESH_TOKEN);

        // Act
        RefreshTokenResult result = refreshTokenService.refreshToken(command);

        // Assert
        assertNotNull(result);
        assertEquals(validUser, result.user());
        assertEquals(NEW_ACCESS_TOKEN, result.accessToken());
        assertEquals(NEW_REFRESH_TOKEN, result.refreshToken());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is expired")
    void refreshToken_WithExpiredToken_ShouldThrowException() {
        // Arrange
        RefreshTokenCommand command = new RefreshTokenCommand(REFRESH_TOKEN);
        when(jwtTokenPort.isTokenExpired(REFRESH_TOKEN)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.refreshToken(command)
        );
        assertTrue(exception.getMessage().contains("Refresh token is expired"));
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void refreshToken_WithInvalidToken_ShouldThrowException() {
        // Arrange
        RefreshTokenCommand command = new RefreshTokenCommand(REFRESH_TOKEN);
        when(jwtTokenPort.isTokenExpired(REFRESH_TOKEN)).thenReturn(false);
        when(jwtTokenPort.validateTokenAndGetUserId(REFRESH_TOKEN)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.refreshToken(command)
        );
        assertTrue(exception.getMessage().contains("Invalid refresh token"));
    }

    @Test
    @DisplayName("Should throw exception when user is not found")
    void refreshToken_WithValidTokenButUserNotFound_ShouldThrowException() {
        // Arrange
        RefreshTokenCommand command = new RefreshTokenCommand(REFRESH_TOKEN);
        when(jwtTokenPort.isTokenExpired(REFRESH_TOKEN)).thenReturn(false);
        when(jwtTokenPort.validateTokenAndGetUserId(REFRESH_TOKEN)).thenReturn(Optional.of(USER_ID));
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.refreshToken(command)
        );
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should throw exception when user account is disabled")
    void refreshToken_WithValidTokenButDisabledUser_ShouldThrowException() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        User disabledUser = User.reconstitute(
                UserId.of(USER_ID),
                new Username("testuser"),
                new Email("test@example.com"),
                "passwordHash",
                User.Role.USER,
                false,
                now,
                now
        );
        
        RefreshTokenCommand command = new RefreshTokenCommand(REFRESH_TOKEN);
        when(jwtTokenPort.isTokenExpired(REFRESH_TOKEN)).thenReturn(false);
        when(jwtTokenPort.validateTokenAndGetUserId(REFRESH_TOKEN)).thenReturn(Optional.of(USER_ID));
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(disabledUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.refreshToken(command)
        );
        assertTrue(exception.getMessage().contains("User account is disabled"));
    }
} 