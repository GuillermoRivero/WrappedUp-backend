package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetUserUseCaseTest {

    @Test
    @DisplayName("Should call getUserById with the provided UserId")
    void getUserById_ShouldCallWithProvidedUserId() {
        // Create a mock implementation of the interface
        GetUserUseCase useCase = Mockito.mock(GetUserUseCase.class);
        
        // Create a test UserId
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Mock a user response
        User mockUser = Mockito.mock(User.class);
        when(useCase.getUserById(userId)).thenReturn(Optional.of(mockUser));
        
        // Call the method
        Optional<User> result = useCase.getUserById(userId);
        
        // Verify the method was called with the correct UserId
        verify(useCase).getUserById(userId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }
    
    @Test
    @DisplayName("Should call getUserByUsername with the provided username")
    void getUserByUsername_ShouldCallWithProvidedUsername() {
        // Create a mock implementation of the interface
        GetUserUseCase useCase = Mockito.mock(GetUserUseCase.class);
        
        // Create a test username
        String username = "testuser";
        
        // Mock a user response
        User mockUser = Mockito.mock(User.class);
        when(useCase.getUserByUsername(username)).thenReturn(Optional.of(mockUser));
        
        // Call the method
        Optional<User> result = useCase.getUserByUsername(username);
        
        // Verify the method was called with the correct username
        verify(useCase).getUserByUsername(username);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }
    
    @Test
    @DisplayName("Should call getUserByEmail with the provided email")
    void getUserByEmail_ShouldCallWithProvidedEmail() {
        // Create a mock implementation of the interface
        GetUserUseCase useCase = Mockito.mock(GetUserUseCase.class);
        
        // Create a test email
        String email = "test@example.com";
        
        // Mock a user response
        User mockUser = Mockito.mock(User.class);
        when(useCase.getUserByEmail(email)).thenReturn(Optional.of(mockUser));
        
        // Call the method
        Optional<User> result = useCase.getUserByEmail(email);
        
        // Verify the method was called with the correct email
        verify(useCase).getUserByEmail(email);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }
} 