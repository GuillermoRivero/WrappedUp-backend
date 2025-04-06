package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.exception.UserAlreadyExistsException;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.RegisterUserUseCase.RegisterUserCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterUserUseCaseTest {

    @Test
    @DisplayName("Should throw exception when username is null in RegisterUserCommand")
    void registerUserCommand_WithNullUsername_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand(null, "test@example.com", "password123")
        );
    }

    @Test
    @DisplayName("Should throw exception when username is blank in RegisterUserCommand")
    void registerUserCommand_WithBlankUsername_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand("", "test@example.com", "password123")
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand("   ", "test@example.com", "password123")
        );
    }

    @Test
    @DisplayName("Should throw exception when email is null in RegisterUserCommand")
    void registerUserCommand_WithNullEmail_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand("testuser", null, "password123")
        );
    }

    @Test
    @DisplayName("Should throw exception when email is blank in RegisterUserCommand")
    void registerUserCommand_WithBlankEmail_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand("testuser", "", "password123")
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand("testuser", "   ", "password123")
        );
    }

    @Test
    @DisplayName("Should throw exception when password is null in RegisterUserCommand")
    void registerUserCommand_WithNullPassword_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand("testuser", "test@example.com", null)
        );
    }

    @Test
    @DisplayName("Should throw exception when password is blank in RegisterUserCommand")
    void registerUserCommand_WithBlankPassword_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand("testuser", "test@example.com", "")
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new RegisterUserCommand("testuser", "test@example.com", "   ")
        );
    }

    @Test
    @DisplayName("Should create RegisterUserCommand with valid parameters")
    void registerUserCommand_WithValidParameters_ShouldCreateInstance() {
        RegisterUserCommand command = new RegisterUserCommand("testuser", "test@example.com", "password123");
        
        assertNotNull(command);
        assertEquals("testuser", command.username());
        assertEquals("test@example.com", command.email());
        assertEquals("password123", command.password());
    }

    @Test
    @DisplayName("Should call registerUser with the provided command")
    void registerUser_ShouldCallWithProvidedCommand() {
        // Create a mock implementation of the interface
        RegisterUserUseCase useCase = Mockito.mock(RegisterUserUseCase.class);
        
        // Create a test command
        RegisterUserCommand command = new RegisterUserCommand("testuser", "test@example.com", "password123");
        
        // Mock a user ID response
        UserId mockUserId = UserId.fromUUID(UUID.randomUUID());
        when(useCase.registerUser(command)).thenReturn(mockUserId);
        
        // Call the method
        UserId result = useCase.registerUser(command);
        
        // Verify the method was called with the correct command
        verify(useCase).registerUser(command);
        
        // Verify the result
        assertEquals(mockUserId, result);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when user already exists")
    void registerUser_WithExistingUser_ShouldThrowException() {
        // Create a mock implementation of the interface
        RegisterUserUseCase useCase = Mockito.mock(RegisterUserUseCase.class);
        
        // Create a test command
        RegisterUserCommand command = new RegisterUserCommand("existinguser", "existing@example.com", "password123");
        
        // Mock an exception
        when(useCase.registerUser(command)).thenThrow(new UserAlreadyExistsException("User already exists"));
        
        // Call the method and verify the exception
        UserAlreadyExistsException exception = assertThrows(
            UserAlreadyExistsException.class,
            () -> useCase.registerUser(command)
        );
        
        assertEquals("User already exists", exception.getMessage());
    }
} 