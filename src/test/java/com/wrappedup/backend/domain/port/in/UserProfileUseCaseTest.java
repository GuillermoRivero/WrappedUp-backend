package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.UserProfileUseCase.UpdateProfileCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserProfileUseCaseTest {

    @Test
    @DisplayName("Should throw exception when userId is null in UpdateProfileCommand")
    void updateProfileCommand_WithNullUserId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateProfileCommand(
                null, 
                "John Doe", 
                "Book lover", 
                "profile.jpg", 
                Arrays.asList("Fantasy", "Sci-Fi"), 
                52, 
                "English", 
                true, 
                new HashMap<>(), 
                "New York"
            )
        );
    }
    
    @Test
    @DisplayName("Should create UpdateProfileCommand with valid parameters")
    void updateProfileCommand_WithValidParameters_ShouldCreateInstance() {
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        String fullName = "John Doe";
        String bio = "Book lover";
        String userImageUrl = "profile.jpg";
        var favoriteGenres = Arrays.asList("Fantasy", "Sci-Fi");
        Integer readingGoal = 52;
        String preferredLanguage = "English";
        Boolean isPublicProfile = true;
        Map<String, String> socialLinks = new HashMap<>();
        socialLinks.put("twitter", "johndoe");
        String location = "New York";
        
        UpdateProfileCommand command = new UpdateProfileCommand(
            userId, fullName, bio, userImageUrl, favoriteGenres, 
            readingGoal, preferredLanguage, isPublicProfile, socialLinks, location
        );
        
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertEquals(fullName, command.fullName());
        assertEquals(bio, command.bio());
        assertEquals(userImageUrl, command.userImageUrl());
        assertEquals(favoriteGenres, command.favoriteGenres());
        assertEquals(readingGoal, command.readingGoal());
        assertEquals(preferredLanguage, command.preferredLanguage());
        assertEquals(isPublicProfile, command.isPublicProfile());
        assertEquals(socialLinks, command.socialLinks());
        assertEquals(location, command.location());
    }
    
    @Test
    @DisplayName("Should create UpdateProfileCommand with minimum required parameters")
    void updateProfileCommand_WithMinimumRequiredParameters_ShouldCreateInstance() {
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        UpdateProfileCommand command = new UpdateProfileCommand(
            userId, null, null, null, null, null, null, null, null, null
        );
        
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertNull(command.fullName());
        assertNull(command.bio());
        assertNull(command.userImageUrl());
        assertNull(command.favoriteGenres());
        assertNull(command.readingGoal());
        assertNull(command.preferredLanguage());
        assertNull(command.isPublicProfile());
        assertNull(command.socialLinks());
        assertNull(command.location());
    }
    
    @Test
    @DisplayName("Should call createProfile with the provided UserId")
    void createProfile_ShouldCallWithProvidedUserId() {
        // Create a mock implementation of the interface
        UserProfileUseCase useCase = Mockito.mock(UserProfileUseCase.class);
        
        // Create a test UserId
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Mock a profile response
        UserProfile mockProfile = Mockito.mock(UserProfile.class);
        when(useCase.createProfile(userId)).thenReturn(mockProfile);
        
        // Call the method
        UserProfile result = useCase.createProfile(userId);
        
        // Verify the method was called with the correct UserId
        verify(useCase).createProfile(userId);
        
        // Verify the result
        assertEquals(mockProfile, result);
    }
    
    @Test
    @DisplayName("Should call updateProfile with the provided command")
    void updateProfile_ShouldCallWithProvidedCommand() {
        // Create a mock implementation of the interface
        UserProfileUseCase useCase = Mockito.mock(UserProfileUseCase.class);
        
        // Create a test command
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        UpdateProfileCommand command = new UpdateProfileCommand(
            userId, "John Doe", "Book lover", "profile.jpg", 
            Arrays.asList("Fantasy", "Sci-Fi"), 52, "English", 
            true, new HashMap<>(), "New York"
        );
        
        // Mock a profile response
        UserProfile mockProfile = Mockito.mock(UserProfile.class);
        when(useCase.updateProfile(command)).thenReturn(mockProfile);
        
        // Call the method
        UserProfile result = useCase.updateProfile(command);
        
        // Verify the method was called with the correct command
        verify(useCase).updateProfile(command);
        
        // Verify the result
        assertEquals(mockProfile, result);
    }
    
    @Test
    @DisplayName("Should call getProfileByUserId with the provided UserId")
    void getProfileByUserId_ShouldCallWithProvidedUserId() {
        // Create a mock implementation of the interface
        UserProfileUseCase useCase = Mockito.mock(UserProfileUseCase.class);
        
        // Create a test UserId
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Mock a profile response
        UserProfile mockProfile = Mockito.mock(UserProfile.class);
        when(useCase.getProfileByUserId(userId)).thenReturn(Optional.of(mockProfile));
        
        // Call the method
        Optional<UserProfile> result = useCase.getProfileByUserId(userId);
        
        // Verify the method was called with the correct UserId
        verify(useCase).getProfileByUserId(userId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockProfile, result.get());
    }
    
    @Test
    @DisplayName("Should call getPublicProfileByUsername with the provided Username")
    void getPublicProfileByUsername_ShouldCallWithProvidedUsername() {
        // Create a mock implementation of the interface
        UserProfileUseCase useCase = Mockito.mock(UserProfileUseCase.class);
        
        // Create a test Username
        Username username = new Username("testuser");
        
        // Mock a profile response
        UserProfile mockProfile = Mockito.mock(UserProfile.class);
        when(useCase.getPublicProfileByUsername(username)).thenReturn(Optional.of(mockProfile));
        
        // Call the method
        Optional<UserProfile> result = useCase.getPublicProfileByUsername(username);
        
        // Verify the method was called with the correct Username
        verify(useCase).getPublicProfileByUsername(username);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockProfile, result.get());
    }
    
    @Test
    @DisplayName("Should call hasProfile with the provided UserId")
    void hasProfile_ShouldCallWithProvidedUserId() {
        // Create a mock implementation of the interface
        UserProfileUseCase useCase = Mockito.mock(UserProfileUseCase.class);
        
        // Create a test UserId
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Mock a result
        when(useCase.hasProfile(userId)).thenReturn(true);
        
        // Call the method
        boolean result = useCase.hasProfile(userId);
        
        // Verify the method was called with the correct UserId
        verify(useCase).hasProfile(userId);
        
        // Verify the result
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should call deleteProfile with the provided UserId")
    void deleteProfile_ShouldCallWithProvidedUserId() {
        // Create a mock implementation of the interface
        UserProfileUseCase useCase = Mockito.mock(UserProfileUseCase.class);
        
        // Create a test UserId
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Call the method
        useCase.deleteProfile(userId);
        
        // Verify the method was called with the correct UserId
        verify(useCase).deleteProfile(userId);
    }
} 