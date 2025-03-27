package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.UserProfile;
import com.wrappedup.backend.domain.port.UserProfileRepository;
import com.wrappedup.backend.domain.port.UserRepository;
import com.wrappedup.backend.dto.UpdateUserProfileDto;
import com.wrappedup.backend.dto.UserProfileDto;
import com.wrappedup.backend.mapper.UserProfileMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserProfileServiceImplTest {

    private static final UUID TEST_USER_ID = UUID.fromString("f2295dea-c8cb-4493-ae1c-dbfebbfc4f3a");
    private static final UUID TEST_PROFILE_ID = UUID.fromString("a1234567-e89b-12d3-a456-426614174000");
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private User testUser;
    private UserProfile testProfile;
    private UserProfileDto testProfileDto;
    private UpdateUserProfileDto updateProfileDto;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User(TEST_USERNAME, TEST_EMAIL, "password");
        testUser.setId(TEST_USER_ID);

        // Setup test profile
        testProfile = new UserProfile(testUser);
        testProfile.setId(TEST_PROFILE_ID);
        testProfile.setFullName("Test User");
        testProfile.setBio("This is a test bio");
        testProfile.setUserImageUrl("https://example.com/image.jpg");
        testProfile.setFavoriteGenres(List.of("Fantasy", "Science Fiction"));
        testProfile.setReadingGoal(50);
        testProfile.setPreferredLanguage("English");
        testProfile.setPublicProfile(true);
        Map<String, String> socialLinks = new HashMap<>();
        socialLinks.put("twitter", "https://twitter.com/testuser");
        testProfile.setSocialLinks(socialLinks);
        testProfile.setLocation("Test City");

        // Setup test profile DTO
        testProfileDto = new UserProfileDto();
        testProfileDto.setUsername(TEST_USERNAME);
        testProfileDto.setEmail(TEST_EMAIL);
        testProfileDto.setFullName("Test User");
        testProfileDto.setBio("This is a test bio");
        testProfileDto.setUserImageUrl("https://example.com/image.jpg");
        testProfileDto.setFavoriteGenres(List.of("Fantasy", "Science Fiction"));
        testProfileDto.setReadingGoal(50);
        testProfileDto.setPreferredLanguage("English");
        testProfileDto.setPublicProfile(true);
        testProfileDto.setSocialLinks(socialLinks);
        testProfileDto.setLocation("Test City");

        // Setup update profile DTO
        updateProfileDto = new UpdateUserProfileDto();
        updateProfileDto.setFullName("Updated Name");
        updateProfileDto.setBio("Updated bio");
        updateProfileDto.setUserImageUrl("https://example.com/updated.jpg");
        updateProfileDto.setFavoriteGenres(List.of("Mystery", "Thriller"));
        updateProfileDto.setReadingGoal(75);
        updateProfileDto.setPreferredLanguage("Spanish");
        updateProfileDto.setPublicProfile(true);
        Map<String, String> updatedLinks = new HashMap<>();
        updatedLinks.put("instagram", "https://instagram.com/testuser");
        updateProfileDto.setSocialLinks(updatedLinks);
        updateProfileDto.setLocation("Updated City");

        // Configure mocks
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userProfileMapper.toDto(any(UserProfile.class))).thenReturn(testProfileDto);
    }

    @Test
    void shouldUpdateExistingProfile() {
        // Given
        when(userProfileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testProfile);

        // When
        UserProfileDto result = userProfileService.updateProfile(TEST_USER_ID, updateProfileDto);

        // Then
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
        assertEquals(TEST_EMAIL, result.getEmail());
        
        // Verify interactions
        verify(userRepository).findById(TEST_USER_ID);
        verify(userProfileRepository).findByUserId(TEST_USER_ID);
        verify(userProfileRepository).save(any(UserProfile.class));
        verify(userProfileMapper).toDto(testProfile);
    }

    @Test
    void shouldCreateNewProfileWhenUpdatingNonExistentProfile() {
        // Given
        when(userProfileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());
        
        UserProfile newProfile = new UserProfile(testUser);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(newProfile);

        // When
        UserProfileDto result = userProfileService.updateProfile(TEST_USER_ID, updateProfileDto);

        // Then
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
        assertEquals(TEST_EMAIL, result.getEmail());
        
        verify(userRepository).findById(TEST_USER_ID);
        verify(userProfileRepository).findByUserId(TEST_USER_ID);
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
        verify(userProfileMapper).toDto(any(UserProfile.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userProfileService.updateProfile(TEST_USER_ID, updateProfileDto)
        );
        assertTrue(exception.getMessage().contains("User not found"));
        
        // Verify interactions
        verify(userRepository).findById(TEST_USER_ID);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void shouldGetProfile() {
        // Given
        when(userProfileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

        // When
        Optional<UserProfileDto> result = userProfileService.getProfile(TEST_USER_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(TEST_USERNAME, result.get().getUsername());
        assertEquals(TEST_EMAIL, result.get().getEmail());
        
        // Verify interactions
        verify(userProfileRepository).findByUserId(TEST_USER_ID);
        verify(userProfileMapper).toDto(testProfile);
    }

    @Test
    void shouldReturnEmptyWhenProfileNotFound() {
        // Given
        when(userProfileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.empty());

        // When
        Optional<UserProfileDto> result = userProfileService.getProfile(TEST_USER_ID);

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(userProfileRepository).findByUserId(TEST_USER_ID);
        verify(userProfileMapper, never()).toDto(any());
    }

    @Test
    void shouldGetPublicProfile() {
        // Given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

        // When
        Optional<UserProfileDto> result = userProfileService.getPublicProfile(TEST_USERNAME);

        // Then
        assertTrue(result.isPresent());
        assertEquals(TEST_USERNAME, result.get().getUsername());
        assertEquals(TEST_EMAIL, result.get().getEmail());
        
        // Verify interactions
        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(userProfileRepository).findByUserId(TEST_USER_ID);
        verify(userProfileMapper).toDto(testProfile);
    }

    @Test
    void shouldReturnEmptyWhenPublicProfileNotFound() {
        // Given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());

        // When
        Optional<UserProfileDto> result = userProfileService.getPublicProfile(TEST_USERNAME);

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(userProfileRepository, never()).findByUserId(any());
        verify(userProfileMapper, never()).toDto(any());
    }

    @Test
    void shouldReturnEmptyWhenProfileIsPrivate() {
        // Given
        testProfile.setPublicProfile(false);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(TEST_USER_ID)).thenReturn(Optional.of(testProfile));

        // When
        Optional<UserProfileDto> result = userProfileService.getPublicProfile(TEST_USERNAME);

        // Then
        assertFalse(result.isPresent());
        
        // Verify interactions
        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(userProfileRepository).findByUserId(TEST_USER_ID);
        verify(userProfileMapper, never()).toDto(any());
    }

    @Test
    void shouldCheckIfProfileExistsByUserId() {
        // Given
        when(userProfileRepository.existsByUserId(TEST_USER_ID)).thenReturn(true);

        // When
        boolean result = userProfileService.existsByUserId(TEST_USER_ID);

        // Then
        assertTrue(result);
        
        // Verify interactions
        verify(userProfileRepository).existsByUserId(TEST_USER_ID);
    }
} 