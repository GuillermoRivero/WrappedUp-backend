package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserService getUserService;

    private User testUser;
    private UserId userId;
    private Username username;
    private Email email;
    private final String USERNAME_VALUE = "testuser";
    private final String EMAIL_VALUE = "test@example.com";

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        userId = UserId.fromUUID(UUID.randomUUID());
        username = new Username(USERNAME_VALUE);
        email = new Email(EMAIL_VALUE);
        
        testUser = User.reconstitute(
                userId,
                username,
                email,
                "passwordHash",
                User.Role.USER,
                true,
                now,
                now
        );
    }

    @Test
    @DisplayName("Should return user when found by ID")
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = getUserService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    @DisplayName("Should return empty when user not found by ID")
    void getUserById_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

        // Act
        Optional<User> result = getUserService.getUserById(userId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return user when found by username")
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = getUserService.getUserByUsername(USERNAME_VALUE);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    @DisplayName("Should return empty when user not found by username")
    void getUserByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.empty());

        // Act
        Optional<User> result = getUserService.getUserByUsername(USERNAME_VALUE);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for invalid username format")
    void getUserByUsername_WithInvalidFormat_ShouldReturnEmpty() {
        // Act
        Optional<User> result = getUserService.getUserByUsername("a"); // Too short

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return user when found by email")
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = getUserService.getUserByEmail(EMAIL_VALUE);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void getUserByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

        // Act
        Optional<User> result = getUserService.getUserByEmail(EMAIL_VALUE);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty for invalid email format")
    void getUserByEmail_WithInvalidFormat_ShouldReturnEmpty() {
        // Act
        Optional<User> result = getUserService.getUserByEmail("invalid-email");

        // Assert
        assertTrue(result.isEmpty());
    }
} 