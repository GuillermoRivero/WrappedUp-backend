package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileTest {

    private final UserProfileId profileId = UserProfileId.generate();
    private final UserId userId = UserId.generate();
    private final String fullName = "John Doe";
    private final String bio = "Book lover and coffee addict";
    private final String userImageUrl = "https://example.com/profile.jpg";
    private final List<String> favoriteGenres = Arrays.asList("Fiction", "Mystery", "Fantasy");
    private final Integer readingGoal = 50;
    private final String preferredLanguage = "English";
    private final boolean isPublicProfile = true;
    private final Map<String, String> socialLinks = new HashMap<String, String>() {{
        put("twitter", "https://twitter.com/johndoe");
        put("instagram", "https://instagram.com/johndoe");
    }};
    private final String location = "New York, USA";
    private final LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    private final LocalDateTime updatedAt = LocalDateTime.now();
    
    @Test
    @DisplayName("Should create a new profile with minimal fields")
    void createNewProfile_ShouldCreateProfileWithMinimalFields() {
        // Act
        UserProfile profile = UserProfile.createNewProfile(userId);
        
        // Assert
        assertNotNull(profile);
        assertNotNull(profile.getId());
        assertEquals(userId, profile.getUserId());
        assertNull(profile.getFullName());
        assertNull(profile.getBio());
        assertNull(profile.getUserImageUrl());
        assertNotNull(profile.getFavoriteGenres());
        assertTrue(profile.getFavoriteGenres().isEmpty());
        assertNull(profile.getReadingGoal());
        assertNull(profile.getPreferredLanguage());
        assertTrue(profile.isPublicProfile());
        assertNotNull(profile.getSocialLinks());
        assertTrue(profile.getSocialLinks().isEmpty());
        assertNull(profile.getLocation());
        assertNotNull(profile.getCreatedAt());
        assertNotNull(profile.getUpdatedAt());
        assertEquals(profile.getCreatedAt(), profile.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should reconstitute an existing profile with all fields")
    void reconstitute_ShouldCreateProfileWithAllFields() {
        // Act
        UserProfile profile = UserProfile.reconstitute(
                profileId, userId, fullName, bio, userImageUrl,
                favoriteGenres, readingGoal, preferredLanguage, isPublicProfile,
                socialLinks, location, createdAt, updatedAt
        );
        
        // Assert
        assertNotNull(profile);
        assertEquals(profileId, profile.getId());
        assertEquals(userId, profile.getUserId());
        assertEquals(fullName, profile.getFullName());
        assertEquals(bio, profile.getBio());
        assertEquals(userImageUrl, profile.getUserImageUrl());
        assertEquals(favoriteGenres, profile.getFavoriteGenres());
        assertEquals(readingGoal, profile.getReadingGoal());
        assertEquals(preferredLanguage, profile.getPreferredLanguage());
        assertEquals(isPublicProfile, profile.isPublicProfile());
        assertEquals(socialLinks, profile.getSocialLinks());
        assertEquals(location, profile.getLocation());
        assertEquals(createdAt, profile.getCreatedAt());
        assertEquals(updatedAt, profile.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should update profile with new information")
    void updateProfile_ShouldUpdateProfileFields() {
        // Arrange
        UserProfile profile = UserProfile.reconstitute(
                profileId, userId, fullName, bio, userImageUrl,
                favoriteGenres, readingGoal, preferredLanguage, isPublicProfile,
                socialLinks, location, createdAt, updatedAt
        );
        
        String newFullName = "John Smith";
        String newBio = "Updated bio";
        List<String> newGenres = Arrays.asList("Science Fiction", "Biography");
        Integer newReadingGoal = 75;
        boolean newIsPublic = false;
        Map<String, String> newSocialLinks = new HashMap<>();
        newSocialLinks.put("linkedin", "https://linkedin.com/in/johnsmith");
        String newLocation = "San Francisco, USA";
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        profile.updateProfile(
                newFullName, newBio, null, newGenres, newReadingGoal,
                null, newIsPublic, newSocialLinks, newLocation
        );
        
        // Assert
        assertEquals(profileId, profile.getId());
        assertEquals(userId, profile.getUserId());
        assertEquals(newFullName, profile.getFullName());
        assertEquals(newBio, profile.getBio());
        assertEquals(userImageUrl, profile.getUserImageUrl());
        assertEquals(newGenres, profile.getFavoriteGenres());
        assertEquals(newReadingGoal, profile.getReadingGoal());
        assertEquals(preferredLanguage, profile.getPreferredLanguage());
        assertEquals(newIsPublic, profile.isPublicProfile());
        assertEquals(newSocialLinks, profile.getSocialLinks());
        assertEquals(newLocation, profile.getLocation());
        assertEquals(createdAt, profile.getCreatedAt());
        assertTrue(profile.getUpdatedAt().isAfter(beforeUpdate) || 
                profile.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should make profile public")
    void makeProfilePublic_ShouldSetIsPublicProfileToTrue() {
        // Arrange
        UserProfile profile = UserProfile.reconstitute(
                profileId, userId, fullName, bio, userImageUrl,
                favoriteGenres, readingGoal, preferredLanguage, false,
                socialLinks, location, createdAt, updatedAt
        );
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        profile.makeProfilePublic();
        
        // Assert
        assertTrue(profile.isPublicProfile());
        assertTrue(profile.getUpdatedAt().isAfter(beforeUpdate) || 
                profile.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should make profile private")
    void makeProfilePrivate_ShouldSetIsPublicProfileToFalse() {
        // Arrange
        UserProfile profile = UserProfile.reconstitute(
                profileId, userId, fullName, bio, userImageUrl,
                favoriteGenres, readingGoal, preferredLanguage, true,
                socialLinks, location, createdAt, updatedAt
        );
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        profile.makeProfilePrivate();
        
        // Assert
        assertFalse(profile.isPublicProfile());
        assertTrue(profile.getUpdatedAt().isAfter(beforeUpdate) || 
                profile.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Equal profiles should be equal")
    void equals_WithSameId_ShouldBeEqual() {
        // Arrange
        UserProfile profile1 = UserProfile.reconstitute(
                profileId, userId, fullName, bio, userImageUrl,
                favoriteGenres, readingGoal, preferredLanguage, isPublicProfile,
                socialLinks, location, createdAt, updatedAt
        );
        
        UserProfile profile2 = UserProfile.reconstitute(
                profileId, userId, "Different Name", "Different Bio", null,
                Arrays.asList("Different"), null, null, false,
                new HashMap<>(), null, createdAt, updatedAt
        );
        
        // Assert
        assertEquals(profile1, profile2);
        assertEquals(profile1.hashCode(), profile2.hashCode());
    }
    
    @Test
    @DisplayName("Profiles with different IDs should not be equal")
    void equals_WithDifferentIds_ShouldNotBeEqual() {
        // Arrange
        UserProfile profile1 = UserProfile.reconstitute(
                profileId, userId, fullName, bio, userImageUrl,
                favoriteGenres, readingGoal, preferredLanguage, isPublicProfile,
                socialLinks, location, createdAt, updatedAt
        );
        
        UserProfile profile2 = UserProfile.reconstitute(
                UserProfileId.generate(), userId, fullName, bio, userImageUrl,
                favoriteGenres, readingGoal, preferredLanguage, isPublicProfile,
                socialLinks, location, createdAt, updatedAt
        );
        
        // Assert
        assertNotEquals(profile1, profile2);
        assertNotEquals(profile1.hashCode(), profile2.hashCode());
    }
    
    @Test
    @DisplayName("Should handle defensive copies for collections")
    void collections_ShouldBeDefensivelyCopied() {
        // Arrange
        List<String> genresList = Arrays.asList("Fiction", "Mystery");
        Map<String, String> links = new HashMap<>();
        links.put("twitter", "https://twitter.com/test");
        
        UserProfile profile = UserProfile.reconstitute(
                profileId, userId, fullName, bio, userImageUrl,
                genresList, readingGoal, preferredLanguage, isPublicProfile,
                links, location, createdAt, updatedAt
        );
        
        List<String> genresFromProfile = profile.getFavoriteGenres();
        Map<String, String> linksFromProfile = profile.getSocialLinks();
        
        // Act
        genresFromProfile.add("Horror");  // Should not affect the original
        linksFromProfile.put("facebook", "https://facebook.com/test");  // Should not affect the original
        
        // Assert
        assertEquals(2, profile.getFavoriteGenres().size());
        assertEquals(1, profile.getSocialLinks().size());
        assertFalse(profile.getFavoriteGenres().contains("Horror"));
        assertFalse(profile.getSocialLinks().containsKey("facebook"));
    }
} 