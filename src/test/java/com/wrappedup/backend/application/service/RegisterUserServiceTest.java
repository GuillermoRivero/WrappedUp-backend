package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.exception.UserAlreadyExistsException;
import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.RegisterUserUseCase.RegisterUserCommand;
import com.wrappedup.backend.domain.port.in.UserProfileUseCase;
import com.wrappedup.backend.domain.port.out.HashPasswordPort;
import com.wrappedup.backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashPasswordPort hashPasswordPort;

    @Mock
    private UserProfileUseCase userProfileUseCase;

    @Spy
    @InjectMocks
    private RegisterUserService registerUserService;

    private RegisterUserCommand validCommand;
    private User savedUser;
    private UserId userId;
    
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final String HASHED_PASSWORD = "hashedPassword";

    @BeforeEach
    void setUp() {
        validCommand = new RegisterUserCommand(USERNAME, EMAIL, PASSWORD);
        userId = UserId.generate();
        savedUser = User.createNewUser(USERNAME, EMAIL, HASHED_PASSWORD);
        
        // Use reflection to set the ID manually since we need a known ID for tests
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedUser, userId);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void registerUser_WithValidData_ShouldRegisterUser() throws Exception {
        // Arrange
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(hashPasswordPort.hashPassword(PASSWORD)).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(savedUser));
        doNothing().when(registerUserService).createUserProfile(any(UserId.class));

        // Act
        UserId result = registerUserService.registerUser(validCommand);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result);
        verify(userRepository).existsByUsername(any(Username.class));
        verify(userRepository).existsByEmail(any(Email.class));
        verify(hashPasswordPort).hashPassword(PASSWORD);
        verify(userRepository).save(any(User.class));
        verify(registerUserService).createUserProfile(userId);
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void registerUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> registerUserService.registerUser(validCommand)
        );
        assertEquals(UserAlreadyExistsException.USERNAME_EXISTS, exception.getMessage());
        verify(userRepository).existsByUsername(any(Username.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername(any(Username.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> registerUserService.registerUser(validCommand)
        );
        assertEquals(UserAlreadyExistsException.EMAIL_EXISTS, exception.getMessage());
        verify(userRepository).existsByUsername(any(Username.class));
        verify(userRepository).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should create user profile for registered user")
    void createUserProfile_ShouldCreateProfile() {
        // Arrange
        when(userProfileUseCase.createProfile(userId)).thenReturn(null);

        // Act
        registerUserService.createUserProfile(userId);

        // Assert
        verify(userProfileUseCase).createProfile(userId);
    }

    @Test
    @DisplayName("Should handle profile creation exceptions gracefully")
    void createUserProfile_WhenExceptionOccurs_ShouldHandleGracefully() {
        // Arrange
        when(userProfileUseCase.createProfile(userId)).thenThrow(new RuntimeException("Profile creation failed"));

        // Act - This should not throw an exception
        registerUserService.createUserProfile(userId);

        // Assert
        verify(userProfileUseCase).createProfile(userId);
    }
} 