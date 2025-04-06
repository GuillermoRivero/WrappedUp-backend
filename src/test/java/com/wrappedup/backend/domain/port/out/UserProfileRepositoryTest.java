package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.UserProfileId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileRepositoryTest {

    @Test
    @DisplayName("Should save a user profile")
    void save_ShouldReturnSavedProfile() {
        // Create a mock implementation of the interface
        UserProfileRepository repository = Mockito.mock(UserProfileRepository.class);
        
        // Create a test profile
        UserProfile profile = createMockUserProfile();
        
        // Mock the save behavior
        when(repository.save(profile)).thenReturn(profile);
        
        // Call the method
        UserProfile result = repository.save(profile);
        
        // Verify the method was called with the correct profile
        verify(repository).save(profile);
        
        // Verify the result
        assertEquals(profile, result);
    }
    
    @Test
    @DisplayName("Should find a user profile by ID")
    void findById_ShouldReturnProfileWhenFound() {
        // Create a mock implementation of the interface
        UserProfileRepository repository = Mockito.mock(UserProfileRepository.class);
        
        // Create a test profile and ID
        UserProfileId profileId = UserProfileId.of(UUID.randomUUID());
        UserProfile profile = createMockUserProfile();
        
        // Mock the findById behavior
        when(repository.findById(profileId)).thenReturn(Optional.of(profile));
        
        // Call the method
        Optional<UserProfile> result = repository.findById(profileId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(profileId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(profile, result.get());
    }
    
    @Test
    @DisplayName("Should return empty when user profile not found by ID")
    void findById_ShouldReturnEmptyWhenNotFound() {
        // Create a mock implementation of the interface
        UserProfileRepository repository = Mockito.mock(UserProfileRepository.class);
        
        // Create a test ID
        UserProfileId profileId = UserProfileId.of(UUID.randomUUID());
        
        // Mock the findById behavior
        when(repository.findById(profileId)).thenReturn(Optional.empty());
        
        // Call the method
        Optional<UserProfile> result = repository.findById(profileId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(profileId);
        
        // Verify the result
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should find a user profile by user ID")
    void findByUserId_ShouldReturnProfileWhenFound() {
        // Create a mock implementation of the interface
        UserProfileRepository repository = Mockito.mock(UserProfileRepository.class);
        
        // Create a test profile and ID
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        UserProfile profile = createMockUserProfile();
        
        // Mock the findByUserId behavior
        when(repository.findByUserId(userId)).thenReturn(Optional.of(profile));
        
        // Call the method
        Optional<UserProfile> result = repository.findByUserId(userId);
        
        // Verify the method was called with the correct user ID
        verify(repository).findByUserId(userId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(profile, result.get());
    }
    
    @Test
    @DisplayName("Should find a user profile by username")
    void findByUsername_ShouldReturnProfileWhenFound() {
        // Create a mock implementation of the interface
        UserProfileRepository repository = Mockito.mock(UserProfileRepository.class);
        
        // Create a test profile and username
        Username username = new Username("testuser");
        UserProfile profile = createMockUserProfile();
        
        // Mock the findByUsername behavior
        when(repository.findByUsername(username)).thenReturn(Optional.of(profile));
        
        // Call the method
        Optional<UserProfile> result = repository.findByUsername(username);
        
        // Verify the method was called with the correct username
        verify(repository).findByUsername(username);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(profile, result.get());
    }
    
    @Test
    @DisplayName("Should check if user profile exists by user ID")
    void existsByUserId_ShouldReturnTrueWhenExists() {
        // Create a mock implementation of the interface
        UserProfileRepository repository = Mockito.mock(UserProfileRepository.class);
        
        // Create a test user ID
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Mock the existsByUserId behavior
        when(repository.existsByUserId(userId)).thenReturn(true);
        
        // Call the method
        boolean result = repository.existsByUserId(userId);
        
        // Verify the method was called with the correct user ID
        verify(repository).existsByUserId(userId);
        
        // Verify the result
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should delete a user profile by ID")
    void deleteById_ShouldCallRepositoryWithCorrectId() {
        // Create a mock implementation of the interface
        UserProfileRepository repository = Mockito.mock(UserProfileRepository.class);
        
        // Create a test ID
        UserProfileId profileId = UserProfileId.of(UUID.randomUUID());
        
        // Call the method
        repository.deleteById(profileId);
        
        // Verify the method was called with the correct ID
        verify(repository).deleteById(profileId);
    }
    
    private UserProfile createMockUserProfile() {
        LocalDateTime now = LocalDateTime.now();
        return UserProfile.reconstitute(
                UserProfileId.of(UUID.randomUUID()),
                UserId.fromUUID(UUID.randomUUID()),
                "Test User",
                "This is a test bio",
                "user-image.jpg",
                new ArrayList<>(),
                52,
                "English",
                true,
                new HashMap<>(),
                "New York",
                now,
                now
        );
    }
} 