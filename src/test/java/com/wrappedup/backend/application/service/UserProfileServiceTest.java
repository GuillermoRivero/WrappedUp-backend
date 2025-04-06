package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.UserProfileId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.GetUserUseCase;
import com.wrappedup.backend.domain.port.in.UserProfileUseCase.UpdateProfileCommand;
import com.wrappedup.backend.domain.port.out.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private GetUserUseCase getUserUseCase;

    @InjectMocks
    private UserProfileService userProfileService;

    @Captor
    private ArgumentCaptor<UserProfile> profileCaptor;

    private UserId userId;
    private User user;
    private UserProfile userProfile;
    private UserProfileId profileId;
    private Username username;

    @BeforeEach
    void setUp() {
        userId = UserId.generate();
        profileId = UserProfileId.generate();
        username = new Username("testuser");

        // Set up user
        user = User.createNewUser("testuser", "test@example.com", "password123");
        ReflectionTestUtils.setField(user, "id", userId);

        // Set up user profile
        userProfile = UserProfile.createNewProfile(userId);
        ReflectionTestUtils.setField(userProfile, "id", profileId);
    }

    @Test
    @DisplayName("Should create a new profile when user exists and profile doesn't exist")
    void createProfile_WhenUserExistsAndProfileDoesNotExist_ShouldCreateProfile() {
        // Arrange
        when(getUserUseCase.getUserById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        // Act
        UserProfile result = userProfileService.createProfile(userId);

        // Assert
        verify(getUserUseCase).getUserById(userId);
        verify(userProfileRepository).findByUserId(userId);
        verify(userProfileRepository).save(any(UserProfile.class));

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    @DisplayName("Should return existing profile when it already exists")
    void createProfile_WhenProfileAlreadyExists_ShouldReturnExistingProfile() {
        // Arrange
        when(getUserUseCase.getUserById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // Act
        UserProfile result = userProfileService.createProfile(userId);

        // Assert
        verify(getUserUseCase).getUserById(userId);
        verify(userProfileRepository).findByUserId(userId);
        verify(userProfileRepository, never()).save(any(UserProfile.class));

        assertNotNull(result);
        assertEquals(userProfile, result);
    }

    @Test
    @DisplayName("Should throw exception when creating profile for non-existent user")
    void createProfile_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(getUserUseCase.getUserById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userProfileService.createProfile(userId)
        );

        verify(getUserUseCase).getUserById(userId);
        verify(userProfileRepository, never()).findByUserId(any());
        verify(userProfileRepository, never()).save(any());

        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should update existing profile with new values")
    void updateProfile_WithExistingProfile_ShouldUpdateProfile() {
        // Arrange
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String fullName = "John Doe";
        String bio = "Test bio";
        String imageUrl = "https://example.com/image.jpg";
        List<String> genres = Arrays.asList("Fiction", "Mystery");
        Integer readingGoal = 50;
        String language = "English";
        boolean isPublic = true;
        Map<String, String> socialLinks = new HashMap<>();
        socialLinks.put("twitter", "https://twitter.com/johndoe");
        String location = "New York, USA";

        UpdateProfileCommand command = new UpdateProfileCommand(
                userId, fullName, bio, imageUrl, genres, readingGoal, language, isPublic, socialLinks, location
        );

        // Act
        UserProfile result = userProfileService.updateProfile(command);

        // Assert
        verify(userProfileRepository).findByUserId(userId);
        verify(userProfileRepository).save(profileCaptor.capture());

        UserProfile capturedProfile = profileCaptor.getValue();
        assertNotNull(result);
        assertEquals(fullName, result.getFullName());
        assertEquals(bio, result.getBio());
        assertEquals(imageUrl, result.getUserImageUrl());
        assertEquals(genres, result.getFavoriteGenres());
        assertEquals(readingGoal, result.getReadingGoal());
        assertEquals(language, result.getPreferredLanguage());
        assertEquals(isPublic, result.isPublicProfile());
        assertEquals(socialLinks, result.getSocialLinks());
        assertEquals(location, result.getLocation());
    }

    @Test
    @DisplayName("Should create new profile when updating non-existent profile")
    void updateProfile_WithNonExistentProfile_ShouldCreateAndUpdateProfile() {
        // Arrange
        UserProfile newProfile = UserProfile.createNewProfile(userId);
        
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(getUserUseCase.getUserById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        UpdateProfileCommand command = new UpdateProfileCommand(
                userId, "John Doe", null, null, null, null, null, null, null, null
        );

        // Act
        UserProfile result = userProfileService.updateProfile(command);

        // Assert
        verify(userProfileRepository).findByUserId(userId);
        verify(userProfileRepository, times(2)).save(any(UserProfile.class));

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should get profile by user ID when it exists")
    void getProfileByUserId_WhenProfileExists_ShouldReturnProfile() {
        // Arrange
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));

        // Act
        Optional<UserProfile> result = userProfileService.getProfileByUserId(userId);

        // Assert
        verify(userProfileRepository).findByUserId(userId);
        assertTrue(result.isPresent());
        assertEquals(userProfile, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when profile doesn't exist by user ID")
    void getProfileByUserId_WhenProfileDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserProfile> result = userProfileService.getProfileByUserId(userId);

        // Assert
        verify(userProfileRepository).findByUserId(userId);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should get public profile by username when it exists and is public")
    void getPublicProfileByUsername_WhenProfileExistsAndIsPublic_ShouldReturnProfile() {
        // Arrange
        // Make profile public
        userProfile.makeProfilePublic();
        
        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.of(userProfile));

        // Act
        Optional<UserProfile> result = userProfileService.getPublicProfileByUsername(username);

        // Assert
        verify(userProfileRepository).findByUsername(username);
        assertTrue(result.isPresent());
        assertEquals(userProfile, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when profile exists but is private")
    void getPublicProfileByUsername_WhenProfileExistsButIsPrivate_ShouldReturnEmpty() {
        // Arrange
        // Make profile private
        userProfile.makeProfilePrivate();
        
        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.of(userProfile));

        // Act
        Optional<UserProfile> result = userProfileService.getPublicProfileByUsername(username);

        // Assert
        verify(userProfileRepository).findByUsername(username);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return empty optional when profile doesn't exist by username")
    void getPublicProfileByUsername_WhenProfileDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(userProfileRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<UserProfile> result = userProfileService.getPublicProfileByUsername(username);

        // Assert
        verify(userProfileRepository).findByUsername(username);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should check if user has profile")
    void hasProfile_ShouldReturnCorrectResult() {
        // Arrange
        when(userProfileRepository.existsByUserId(userId)).thenReturn(true);

        // Act
        boolean result = userProfileService.hasProfile(userId);

        // Assert
        verify(userProfileRepository).existsByUserId(userId);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should delete profile when it exists")
    void deleteProfile_WhenProfileExists_ShouldDeleteProfile() {
        // Arrange
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(userProfile));
        doNothing().when(userProfileRepository).deleteById(profileId);

        // Act
        userProfileService.deleteProfile(userId);

        // Assert
        verify(userProfileRepository).findByUserId(userId);
        verify(userProfileRepository).deleteById(profileId);
    }

    @Test
    @DisplayName("Should not delete profile when it doesn't exist")
    void deleteProfile_WhenProfileDoesNotExist_ShouldDoNothing() {
        // Arrange
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // Act
        userProfileService.deleteProfile(userId);

        // Assert
        verify(userProfileRepository).findByUserId(userId);
        verify(userProfileRepository, never()).deleteById(any());
    }
} 