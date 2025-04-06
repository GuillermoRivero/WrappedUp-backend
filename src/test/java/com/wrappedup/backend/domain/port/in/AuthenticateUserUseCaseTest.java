package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.AuthenticateUserUseCase.AuthenticationCommand;
import com.wrappedup.backend.domain.port.in.AuthenticateUserUseCase.AuthenticationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticateUserUseCaseTest {

    @Test
    @DisplayName("Should throw exception when email is null in AuthenticationCommand")
    void authenticationCommand_WithNullEmail_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new AuthenticationCommand(null, "password"));
    }

    @Test
    @DisplayName("Should throw exception when email is blank in AuthenticationCommand")
    void authenticationCommand_WithBlankEmail_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new AuthenticationCommand("", "password"));
        assertThrows(IllegalArgumentException.class, () -> new AuthenticationCommand("  ", "password"));
    }

    @Test
    @DisplayName("Should throw exception when password is null in AuthenticationCommand")
    void authenticationCommand_WithNullPassword_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new AuthenticationCommand("email@example.com", null));
    }

    @Test
    @DisplayName("Should throw exception when password is blank in AuthenticationCommand")
    void authenticationCommand_WithBlankPassword_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new AuthenticationCommand("email@example.com", ""));
        assertThrows(IllegalArgumentException.class, () -> new AuthenticationCommand("email@example.com", "   "));
    }

    @Test
    @DisplayName("Should create AuthenticationCommand with valid parameters")
    void authenticationCommand_WithValidParameters_ShouldCreateInstance() {
        AuthenticationCommand command = new AuthenticationCommand("email@example.com", "password");
        assertNotNull(command);
        assertNotNull(command.email());
        assertNotNull(command.password());
    }

    @Test
    @DisplayName("Should throw exception when user is null in AuthenticationResult")
    void authenticationResult_WithNullUser_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new AuthenticationResult(null, "accessToken", "refreshToken"));
    }

    @Test
    @DisplayName("Should throw exception when accessToken is null in AuthenticationResult")
    void authenticationResult_WithNullAccessToken_ShouldThrowException() {
        User mockUser = createMockUser();
        assertThrows(IllegalArgumentException.class, 
            () -> new AuthenticationResult(mockUser, null, "refreshToken"));
    }

    @Test
    @DisplayName("Should throw exception when accessToken is blank in AuthenticationResult")
    void authenticationResult_WithBlankAccessToken_ShouldThrowException() {
        User mockUser = createMockUser();
        assertThrows(IllegalArgumentException.class, 
            () -> new AuthenticationResult(mockUser, "", "refreshToken"));
        assertThrows(IllegalArgumentException.class, 
            () -> new AuthenticationResult(mockUser, "  ", "refreshToken"));
    }

    @Test
    @DisplayName("Should throw exception when refreshToken is null in AuthenticationResult")
    void authenticationResult_WithNullRefreshToken_ShouldThrowException() {
        User mockUser = createMockUser();
        assertThrows(IllegalArgumentException.class, 
            () -> new AuthenticationResult(mockUser, "accessToken", null));
    }

    @Test
    @DisplayName("Should throw exception when refreshToken is blank in AuthenticationResult")
    void authenticationResult_WithBlankRefreshToken_ShouldThrowException() {
        User mockUser = createMockUser();
        assertThrows(IllegalArgumentException.class, 
            () -> new AuthenticationResult(mockUser, "accessToken", ""));
        assertThrows(IllegalArgumentException.class, 
            () -> new AuthenticationResult(mockUser, "accessToken", "  "));
    }

    @Test
    @DisplayName("Should create AuthenticationResult with valid parameters")
    void authenticationResult_WithValidParameters_ShouldCreateInstance() {
        User mockUser = createMockUser();
        AuthenticationResult result = new AuthenticationResult(mockUser, "accessToken", "refreshToken");
        assertNotNull(result);
        assertNotNull(result.user());
        assertNotNull(result.accessToken());
        assertNotNull(result.refreshToken());
    }

    private User createMockUser() {
        LocalDateTime now = LocalDateTime.now();
        return User.reconstitute(
            UserId.generate(),
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