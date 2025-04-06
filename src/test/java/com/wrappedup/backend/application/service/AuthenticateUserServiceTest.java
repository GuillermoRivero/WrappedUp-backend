package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.AuthenticateUserUseCase.AuthenticationCommand;
import com.wrappedup.backend.domain.port.in.AuthenticateUserUseCase.AuthenticationResult;
import com.wrappedup.backend.domain.port.out.HashPasswordPort;
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
class AuthenticateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashPasswordPort hashPasswordPort;

    @Mock
    private JwtTokenPort jwtTokenPort;

    @InjectMocks
    private AuthenticateUserService authenticateUserService;

    private User validUser;
    private AuthenticationCommand validCommand;
    private final String EMAIL = "test@example.com";
    private final String USERNAME = "testuser";
    private final String PASSWORD = "password123";
    private final String PASSWORD_HASH = "hashedPassword";
    private final String ACCESS_TOKEN = "access.token.jwt";
    private final String REFRESH_TOKEN = "refresh.token.jwt";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();
        
        // Using the static reconstitute method for User creation
        validUser = User.reconstitute(
                UserId.fromUUID(uuid),
                new Username(USERNAME),
                new Email(EMAIL),
                PASSWORD_HASH,
                User.Role.USER,
                true,
                now,
                now
        );

        validCommand = new AuthenticationCommand(EMAIL, PASSWORD);
    }

    @Test
    @DisplayName("Should successfully authenticate user with valid credentials")
    void authenticate_WithValidCredentials_ShouldReturnAuthenticationResult() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(hashPasswordPort.verifyPassword(anyString(), anyString())).thenReturn(true);
        when(jwtTokenPort.generateAccessToken(any(User.class))).thenReturn(ACCESS_TOKEN);
        when(jwtTokenPort.generateRefreshToken(any(User.class))).thenReturn(REFRESH_TOKEN);

        // Act
        AuthenticationResult result = authenticateUserService.authenticate(validCommand);

        // Assert
        assertNotNull(result);
        assertEquals(validUser, result.user());
        assertEquals(ACCESS_TOKEN, result.accessToken());
        assertEquals(REFRESH_TOKEN, result.refreshToken());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void authenticate_WithNonExistentUser_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authenticateUserService.authenticate(validCommand)
        );
        assertTrue(exception.getMessage().contains("Invalid email or password"));
    }

    @Test
    @DisplayName("Should throw exception when user account is disabled")
    void authenticate_WithDisabledAccount_ShouldThrowException() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        User disabledUser = User.reconstitute(
                validUser.getId(),
                validUser.getUsername(),
                validUser.getEmail(),
                validUser.getPasswordHash(),
                User.Role.USER,
                false,
                now,
                now
        );
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(disabledUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authenticateUserService.authenticate(validCommand)
        );
        // The service returns "Invalid email or password" instead of "User account is disabled" for security reasons
        assertTrue(exception.getMessage().contains("Invalid email or password"));
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void authenticate_WithInvalidPassword_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(validUser));
        when(hashPasswordPort.verifyPassword(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authenticateUserService.authenticate(validCommand)
        );
        assertTrue(exception.getMessage().contains("Invalid email or password"));
    }

    @Test
    @DisplayName("Should throw exception when email format is invalid")
    void authenticate_WithInvalidEmailFormat_ShouldThrowException() {
        // Arrange
        AuthenticationCommand invalidCommand = new AuthenticationCommand("invalid-email", PASSWORD);
        
        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> authenticateUserService.authenticate(invalidCommand)
        );
    }
} 