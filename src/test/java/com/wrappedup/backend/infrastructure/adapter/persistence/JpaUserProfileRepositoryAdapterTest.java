package com.wrappedup.backend.infrastructure.adapter.persistence;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.UserProfileId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.out.UserRepository;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.UserProfileJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.repository.UserJpaRepository;
import com.wrappedup.backend.infrastructure.adapter.persistence.repository.UserProfileJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JpaUserProfileRepositoryAdapterTest {
    
    @Mock
    private UserProfileJpaRepository userProfileJpaRepository;
    
    @Mock
    private UserJpaRepository userJpaRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private JpaUserProfileRepositoryAdapter adapter;
    
    private UUID profileId;
    private UUID userId;
    private UserJpaEntity userJpaEntity;
    private UserProfileJpaEntity profileJpaEntity;
    private UserProfile testUserProfile;
    private User testUser;
    
    @BeforeEach
    void setUp() {
        profileId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        userJpaEntity = new UserJpaEntity();
        userJpaEntity.setId(userId);
        userJpaEntity.setEmail("test@example.com");
        userJpaEntity.setUsername("testuser");
        
        profileJpaEntity = new UserProfileJpaEntity();
        profileJpaEntity.setId(profileId);
        profileJpaEntity.setUser(userJpaEntity);
        profileJpaEntity.setFullName("Test User");
        profileJpaEntity.setBio("Test bio");
        profileJpaEntity.setUserImageUrl("https://example.com/image.jpg");
        profileJpaEntity.setFavoriteGenres(Collections.singletonList("fiction"));
        profileJpaEntity.setReadingGoal(52);
        profileJpaEntity.setPreferredLanguage("en");
        profileJpaEntity.setPublicProfile(true);
        Map<String, String> socialLinks = new HashMap<>();
        socialLinks.put("twitter", "https://twitter.com/testuser");
        profileJpaEntity.setSocialLinks(socialLinks);
        profileJpaEntity.setLocation("Test City");
        profileJpaEntity.setCreatedAt(LocalDateTime.now());
        profileJpaEntity.setUpdatedAt(LocalDateTime.now());
        
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(userJpaEntity));
        
        testUser = User.reconstitute(
                UserId.of(userId.toString()),
                new Username("testuser"),
                new Email("test@example.com"),
                "hashedPassword",
                User.Role.USER,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(testUser));

        testUserProfile = UserProfile.reconstitute(
                UserProfileId.of(profileId.toString()),
                UserId.of(userId.toString()),
                "Test User",
                "Test bio",
                "https://example.com/image.jpg",
                Collections.singletonList("fiction"),
                52,
                "en",
                true,
                Collections.singletonMap("twitter", "https://twitter.com/testuser"),
                "Test City",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
    
    @Test
    void save_ShouldReturnSavedUserProfile() {
        // Arrange
        when(userProfileJpaRepository.save(any(UserProfileJpaEntity.class))).thenReturn(profileJpaEntity);
        
        // Act
        UserProfile result = adapter.save(testUserProfile);
        
        // Assert
        assertNotNull(result);
        assertEquals(profileId.toString(), result.getId().getValue().toString());
        assertEquals(testUserProfile.getFullName(), result.getFullName());
        assertEquals(testUserProfile.getBio(), result.getBio());
        verify(userProfileJpaRepository).save(any(UserProfileJpaEntity.class));
    }
    
    @Test
    void findById_ShouldReturnUserProfile_WhenProfileExists() {
        // Arrange
        when(userProfileJpaRepository.findById(profileId)).thenReturn(Optional.of(profileJpaEntity));
        
        // Act
        Optional<UserProfile> result = adapter.findById(UserProfileId.of(profileId.toString()));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(profileId.toString(), result.get().getId().getValue().toString());
        assertEquals(userId.toString(), result.get().getUserId().getValue().toString());
        assertEquals(profileJpaEntity.getFullName(), result.get().getFullName());
        verify(userProfileJpaRepository).findById(profileId);
    }
    
    @Test
    void findById_ShouldReturnEmpty_WhenProfileDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userProfileJpaRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        // Act
        Optional<UserProfile> result = adapter.findById(UserProfileId.of(nonExistentId.toString()));
        
        // Assert
        assertFalse(result.isPresent());
        verify(userProfileJpaRepository).findById(nonExistentId);
    }
    
    @Test
    void findByUserId_ShouldReturnUserProfile_WhenProfileExists() {
        // Arrange
        when(userProfileJpaRepository.findByUserId(userId)).thenReturn(Optional.of(profileJpaEntity));
        
        // Act
        Optional<UserProfile> result = adapter.findByUserId(UserId.of(userId.toString()));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(profileId.toString(), result.get().getId().getValue().toString());
        assertEquals(userId.toString(), result.get().getUserId().getValue().toString());
        assertEquals(profileJpaEntity.getFullName(), result.get().getFullName());
        verify(userProfileJpaRepository).findByUserId(userId);
    }
    
    @Test
    void findByUserId_ShouldReturnEmpty_WhenProfileDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userProfileJpaRepository.findByUserId(nonExistentId)).thenReturn(Optional.empty());
        
        // Act
        Optional<UserProfile> result = adapter.findByUserId(UserId.of(nonExistentId.toString()));
        
        // Assert
        assertFalse(result.isPresent());
        verify(userProfileJpaRepository).findByUserId(nonExistentId);
    }
    
    @Test
    void findByUsername_ShouldReturnUserProfile_WhenProfileExists() {
        // Arrange
        String username = "testuser";
        when(userProfileJpaRepository.findByUserUsername(username)).thenReturn(Optional.of(profileJpaEntity));
        
        // Act
        Optional<UserProfile> result = adapter.findByUsername(new Username(username));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(profileId.toString(), result.get().getId().getValue().toString());
        assertEquals(userId.toString(), result.get().getUserId().getValue().toString());
        assertEquals(profileJpaEntity.getFullName(), result.get().getFullName());
        verify(userProfileJpaRepository).findByUserUsername(username);
    }
    
    @Test
    void findByUsername_ShouldReturnEmpty_WhenProfileDoesNotExist() {
        // Arrange
        String username = "nonexistentuser";
        when(userProfileJpaRepository.findByUserUsername(username)).thenReturn(Optional.empty());
        
        // Act
        Optional<UserProfile> result = adapter.findByUsername(new Username(username));
        
        // Assert
        assertFalse(result.isPresent());
        verify(userProfileJpaRepository).findByUserUsername(username);
    }
    
    @Test
    void existsByUserId_ShouldReturnTrue_WhenProfileExists() {
        // Arrange
        when(userProfileJpaRepository.existsByUserId(userId)).thenReturn(true);
        
        // Act
        boolean result = adapter.existsByUserId(UserId.of(userId.toString()));
        
        // Assert
        assertTrue(result);
        verify(userProfileJpaRepository).existsByUserId(userId);
    }
    
    @Test
    void existsByUserId_ShouldReturnFalse_WhenProfileDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(userProfileJpaRepository.existsByUserId(nonExistentId)).thenReturn(false);
        
        // Act
        boolean result = adapter.existsByUserId(UserId.of(nonExistentId.toString()));
        
        // Assert
        assertFalse(result);
        verify(userProfileJpaRepository).existsByUserId(nonExistentId);
    }
    
    @Test
    void deleteById_ShouldDeleteProfile() {
        // Arrange
        doNothing().when(userProfileJpaRepository).deleteById(profileId);
        
        // Act
        adapter.deleteById(UserProfileId.of(profileId.toString()));
        
        // Assert
        verify(userProfileJpaRepository).deleteById(profileId);
    }
    
    @Test
    void mapToDomainEntity_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());
        when(userProfileJpaRepository.findById(profileId)).thenReturn(Optional.of(profileJpaEntity));
        
        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            adapter.findById(UserProfileId.of(profileId.toString()));
        });
        
        assertTrue(exception.getMessage().contains("UserProfile exists but User not found"));
    }
} 