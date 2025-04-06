package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.RefreshTokenUseCase.RefreshTokenCommand;
import com.wrappedup.backend.domain.port.in.RefreshTokenUseCase.RefreshTokenResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenUseCaseTest {

    @Test
    @DisplayName("Should throw exception when refreshToken is null in RefreshTokenCommand")
    void refreshTokenCommand_WithNullRefreshToken_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new RefreshTokenCommand(null));
    }

    @Test
    @DisplayName("Should throw exception when refreshToken is blank in RefreshTokenCommand")
    void refreshTokenCommand_WithBlankRefreshToken_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new RefreshTokenCommand(""));
        assertThrows(IllegalArgumentException.class, () -> new RefreshTokenCommand("   "));
    }

    @Test
    @DisplayName("Should create RefreshTokenCommand with valid parameters")
    void refreshTokenCommand_WithValidParameters_ShouldCreateInstance() {
        RefreshTokenCommand command = new RefreshTokenCommand("valid.refresh.token");
        assertNotNull(command);
        assertEquals("valid.refresh.token", command.refreshToken());
    }

    @Test
    @DisplayName("Should throw exception when user is null in RefreshTokenResult")
    void refreshTokenResult_WithNullUser_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, 
            () -> new RefreshTokenResult(null, "accessToken", "refreshToken"));
    }

    @Test
    @DisplayName("Should throw exception when accessToken is null in RefreshTokenResult")
    void refreshTokenResult_WithNullAccessToken_ShouldThrowException() {
        User mockUser = createMockUser();
        assertThrows(IllegalArgumentException.class, 
            () -> new RefreshTokenResult(mockUser, null, "refreshToken"));
    }

    @Test
    @DisplayName("Should throw exception when accessToken is blank in RefreshTokenResult")
    void refreshTokenResult_WithBlankAccessToken_ShouldThrowException() {
        User mockUser = createMockUser();
        assertThrows(IllegalArgumentException.class, 
            () -> new RefreshTokenResult(mockUser, "", "refreshToken"));
        assertThrows(IllegalArgumentException.class, 
            () -> new RefreshTokenResult(mockUser, "  ", "refreshToken"));
    }

    @Test
    @DisplayName("Should throw exception when refreshToken is null in RefreshTokenResult")
    void refreshTokenResult_WithNullRefreshToken_ShouldThrowException() {
        User mockUser = createMockUser();
        assertThrows(IllegalArgumentException.class, 
            () -> new RefreshTokenResult(mockUser, "accessToken", null));
    }

    @Test
    @DisplayName("Should throw exception when refreshToken is blank in RefreshTokenResult")
    void refreshTokenResult_WithBlankRefreshToken_ShouldThrowException() {
        User mockUser = createMockUser();
        assertThrows(IllegalArgumentException.class, 
            () -> new RefreshTokenResult(mockUser, "accessToken", ""));
        assertThrows(IllegalArgumentException.class, 
            () -> new RefreshTokenResult(mockUser, "accessToken", "  "));
    }

    @Test
    @DisplayName("Should create RefreshTokenResult with valid parameters")
    void refreshTokenResult_WithValidParameters_ShouldCreateInstance() {
        User mockUser = createMockUser();
        RefreshTokenResult result = new RefreshTokenResult(mockUser, "accessToken", "refreshToken");
        assertNotNull(result);
        assertEquals(mockUser, result.user());
        assertEquals("accessToken", result.accessToken());
        assertEquals("refreshToken", result.refreshToken());
    }

    @Test
    @DisplayName("Should call refreshToken with the provided command")
    void refreshToken_ShouldCallWithProvidedCommand() {
        // Create a mock implementation of the interface
        RefreshTokenUseCase useCase = Mockito.mock(RefreshTokenUseCase.class);
        
        // Create a test command
        RefreshTokenCommand command = new RefreshTokenCommand("valid.refresh.token");
        
        // Mock a token refresh response
        User mockUser = createMockUser();
        RefreshTokenResult mockResult = new RefreshTokenResult(mockUser, "new.access.token", "new.refresh.token");
        when(useCase.refreshToken(command)).thenReturn(mockResult);
        
        // Call the method
        RefreshTokenResult result = useCase.refreshToken(command);
        
        // Verify the method was called with the correct command
        verify(useCase).refreshToken(command);
        
        // Verify the result
        assertNotNull(result);
        assertEquals(mockUser, result.user());
        assertEquals("new.access.token", result.accessToken());
        assertEquals("new.refresh.token", result.refreshToken());
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