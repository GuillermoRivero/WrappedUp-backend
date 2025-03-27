package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.UserProfile;
import com.wrappedup.backend.domain.port.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaUserProfileRepository.class})
class JpaUserProfileRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserProfileRepository userProfileRepository;

    private User testUser;
    private UserProfile testProfile;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        // Create and persist test user
        testUser = new User("testuser", "test@example.com", "password");
        testUser = entityManager.persistAndFlush(testUser);
        testUserId = testUser.getId();

        // Create and persist test profile
        testProfile = new UserProfile(testUser);
        testProfile.setFullName("Test User");
        testProfile.setBio("This is a test bio");
        testProfile.setUserImageUrl("https://example.com/image.jpg");
        
        // Use mutable collections instead of immutable ones
        List<String> genres = new ArrayList<>();
        genres.add("Fantasy");
        genres.add("Science Fiction");
        testProfile.setFavoriteGenres(genres);
        
        testProfile.setReadingGoal(50);
        testProfile.setPreferredLanguage("English");
        testProfile.setPublicProfile(true);
        
        Map<String, String> socialLinks = new HashMap<>();
        socialLinks.put("twitter", "https://twitter.com/testuser");
        testProfile.setSocialLinks(socialLinks);
        testProfile.setLocation("Test City");
        
        testProfile = entityManager.persistAndFlush(testProfile);
    }

    @Test
    void shouldFindByUserId() {
        // When
        Optional<UserProfile> foundProfile = userProfileRepository.findByUserId(testUserId);
        
        // Then
        assertTrue(foundProfile.isPresent());
        assertEquals(testProfile.getId(), foundProfile.get().getId());
        assertEquals("Test User", foundProfile.get().getFullName());
        assertEquals("This is a test bio", foundProfile.get().getBio());
        assertEquals(testUser.getId(), foundProfile.get().getUser().getId());
    }
    
    @Test
    void shouldReturnEmptyWhenFindingByNonExistentUserId() {
        // When
        Optional<UserProfile> foundProfile = userProfileRepository.findByUserId(UUID.randomUUID());
        
        // Then
        assertFalse(foundProfile.isPresent());
    }

    @Test
    void shouldFindByUserUsername() {
        // When
        Optional<UserProfile> foundProfile = userProfileRepository.findByUserUsername("testuser");
        
        // Then
        assertTrue(foundProfile.isPresent());
        assertEquals(testProfile.getId(), foundProfile.get().getId());
        assertEquals("Test User", foundProfile.get().getFullName());
    }
    
    @Test
    void shouldReturnEmptyWhenFindingByNonExistentUsername() {
        // When
        Optional<UserProfile> foundProfile = userProfileRepository.findByUserUsername("nonexistentuser");
        
        // Then
        assertFalse(foundProfile.isPresent());
    }

    @Test
    void shouldCheckIfExistsByUserId() {
        // When
        boolean exists = userProfileRepository.existsByUserId(testUserId);
        
        // Then
        assertTrue(exists);
    }
    
    @Test
    void shouldReturnFalseWhenCheckingExistenceForNonExistentUserId() {
        // When
        boolean exists = userProfileRepository.existsByUserId(UUID.randomUUID());
        
        // Then
        assertFalse(exists);
    }

    @Test
    void shouldSaveProfile() {
        // Given
        User newUser = new User("newuser", "new@example.com", "password");
        newUser = entityManager.persistAndFlush(newUser);
        
        UserProfile newProfile = new UserProfile(newUser);
        newProfile.setFullName("New User");
        newProfile.setBio("New user bio");
        
        // When
        UserProfile savedProfile = userProfileRepository.save(newProfile);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        assertNotNull(savedProfile.getId());
        
        Optional<UserProfile> foundProfile = userProfileRepository.findById(savedProfile.getId());
        assertTrue(foundProfile.isPresent());
        assertEquals("New User", foundProfile.get().getFullName());
        assertEquals("New user bio", foundProfile.get().getBio());
    }

    @Test
    void shouldDeleteProfile() {
        // When
        userProfileRepository.deleteById(testProfile.getId());
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Optional<UserProfile> foundProfile = userProfileRepository.findById(testProfile.getId());
        assertFalse(foundProfile.isPresent());
    }

    @Test
    void shouldUpdateProfile() {
        // Given
        testProfile.setFullName("Updated Name");
        testProfile.setBio("Updated bio");
        testProfile.setPublicProfile(false);
        
        // Use mutable collections for the update
        List<String> updatedGenres = new ArrayList<>();
        updatedGenres.add("Mystery");
        updatedGenres.add("Thriller");
        testProfile.setFavoriteGenres(updatedGenres);
        
        // When
        UserProfile updatedProfile = userProfileRepository.save(testProfile);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        Optional<UserProfile> foundProfile = userProfileRepository.findById(testProfile.getId());
        assertTrue(foundProfile.isPresent());
        assertEquals("Updated Name", foundProfile.get().getFullName());
        assertEquals("Updated bio", foundProfile.get().getBio());
        assertFalse(foundProfile.get().isPublicProfile());
        assertEquals(2, foundProfile.get().getFavoriteGenres().size());
        assertTrue(foundProfile.get().getFavoriteGenres().contains("Mystery"));
        assertTrue(foundProfile.get().getFavoriteGenres().contains("Thriller"));
    }
} 