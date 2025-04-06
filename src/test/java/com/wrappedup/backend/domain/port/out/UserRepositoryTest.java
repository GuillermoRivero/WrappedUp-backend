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

class UserRepositoryTest {

    @Test
    @DisplayName("Should save a user")
    void save_ShouldReturnSavedUser() {
        // Create a mock implementation of the interface
        UserRepository repository = Mockito.mock(UserRepository.class);
        
        // Create a test user
        User user = createMockUser();
        
        // Mock the save behavior
        when(repository.save(user)).thenReturn(user);
        
        // Call the method
        User result = repository.save(user);
        
        // Verify the method was called with the correct user
        verify(repository).save(user);
        
        // Verify the result
        assertEquals(user, result);
    }
    
    @Test
    @DisplayName("Should find a user by ID")
    void findById_ShouldReturnUserWhenFound() {
        // Create a mock implementation of the interface
        UserRepository repository = Mockito.mock(UserRepository.class);
        
        // Create a test user and ID
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        User user = createMockUser();
        
        // Mock the findById behavior
        when(repository.findById(userId)).thenReturn(Optional.of(user));
        
        // Call the method
        Optional<User> result = repository.findById(userId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(userId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }
    
    @Test
    @DisplayName("Should return empty when user not found by ID")
    void findById_ShouldReturnEmptyWhenNotFound() {
        // Create a mock implementation of the interface
        UserRepository repository = Mockito.mock(UserRepository.class);
        
        // Create a test ID
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Mock the findById behavior
        when(repository.findById(userId)).thenReturn(Optional.empty());
        
        // Call the method
        Optional<User> result = repository.findById(userId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(userId);
        
        // Verify the result
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should find a user by username")
    void findByUsername_ShouldReturnUserWhenFound() {
        // Create a mock implementation of the interface
        UserRepository repository = Mockito.mock(UserRepository.class);
        
        // Create a test user and username
        Username username = new Username("testuser");
        User user = createMockUser();
        
        // Mock the findByUsername behavior
        when(repository.findByUsername(username)).thenReturn(Optional.of(user));
        
        // Call the method
        Optional<User> result = repository.findByUsername(username);
        
        // Verify the method was called with the correct username
        verify(repository).findByUsername(username);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }
    
    @Test
    @DisplayName("Should find a user by email")
    void findByEmail_ShouldReturnUserWhenFound() {
        // Create a mock implementation of the interface
        UserRepository repository = Mockito.mock(UserRepository.class);
        
        // Create a test user and email
        Email email = new Email("test@example.com");
        User user = createMockUser();
        
        // Mock the findByEmail behavior
        when(repository.findByEmail(email)).thenReturn(Optional.of(user));
        
        // Call the method
        Optional<User> result = repository.findByEmail(email);
        
        // Verify the method was called with the correct email
        verify(repository).findByEmail(email);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }
    
    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername_ShouldReturnTrueWhenExists() {
        // Create a mock implementation of the interface
        UserRepository repository = Mockito.mock(UserRepository.class);
        
        // Create a test username
        Username username = new Username("testuser");
        
        // Mock the existsByUsername behavior
        when(repository.existsByUsername(username)).thenReturn(true);
        
        // Call the method
        boolean result = repository.existsByUsername(username);
        
        // Verify the method was called with the correct username
        verify(repository).existsByUsername(username);
        
        // Verify the result
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_ShouldReturnTrueWhenExists() {
        // Create a mock implementation of the interface
        UserRepository repository = Mockito.mock(UserRepository.class);
        
        // Create a test email
        Email email = new Email("test@example.com");
        
        // Mock the existsByEmail behavior
        when(repository.existsByEmail(email)).thenReturn(true);
        
        // Call the method
        boolean result = repository.existsByEmail(email);
        
        // Verify the method was called with the correct email
        verify(repository).existsByEmail(email);
        
        // Verify the result
        assertTrue(result);
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