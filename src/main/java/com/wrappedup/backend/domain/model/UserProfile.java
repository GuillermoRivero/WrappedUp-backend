package com.wrappedup.backend.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Domain entity representing a user profile in the system.
 * This is a pure domain object without infrastructure concerns.
 */
public class UserProfile {
    private final UserProfileId id;
    private final UserId userId;
    private String fullName;
    private String bio;
    private String userImageUrl;
    private List<String> favoriteGenres;
    private Integer readingGoal;
    private String preferredLanguage;
    private boolean isPublicProfile;
    private Map<String, String> socialLinks;
    private String location;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private UserProfile(
            UserProfileId id, 
            UserId userId, 
            String fullName, 
            String bio, 
            String userImageUrl, 
            List<String> favoriteGenres, 
            Integer readingGoal, 
            String preferredLanguage, 
            boolean isPublicProfile, 
            Map<String, String> socialLinks, 
            String location,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "Profile ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.fullName = fullName;
        this.bio = bio;
        this.userImageUrl = userImageUrl;
        this.favoriteGenres = favoriteGenres != null ? new ArrayList<>(favoriteGenres) : new ArrayList<>();
        this.readingGoal = readingGoal;
        this.preferredLanguage = preferredLanguage;
        this.isPublicProfile = isPublicProfile;
        this.socialLinks = socialLinks != null ? new HashMap<>(socialLinks) : new HashMap<>();
        this.location = location;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    /**
     * Creates a new user profile with generated ID.
     */
    public static UserProfile createNewProfile(UserId userId) {
        LocalDateTime now = LocalDateTime.now();
        return new UserProfile(
                UserProfileId.generate(),
                userId,
                null,
                null,
                null,
                new ArrayList<>(),
                null,
                null,
                true,
                new HashMap<>(),
                null,
                now,
                now
        );
    }
    
    /**
     * Reconstructs an existing user profile from persistence.
     */
    public static UserProfile reconstitute(
            UserProfileId id,
            UserId userId,
            String fullName,
            String bio,
            String userImageUrl,
            List<String> favoriteGenres,
            Integer readingGoal,
            String preferredLanguage,
            boolean isPublicProfile,
            Map<String, String> socialLinks,
            String location,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new UserProfile(
                id, 
                userId, 
                fullName, 
                bio, 
                userImageUrl, 
                favoriteGenres, 
                readingGoal, 
                preferredLanguage, 
                isPublicProfile, 
                socialLinks, 
                location, 
                createdAt, 
                updatedAt
        );
    }
    
    // Domain behavior methods
    
    public void updateProfile(
            String fullName,
            String bio,
            String userImageUrl,
            List<String> favoriteGenres,
            Integer readingGoal,
            String preferredLanguage,
            Boolean isPublicProfile,
            Map<String, String> socialLinks,
            String location) {
        if (fullName != null) {
            this.fullName = fullName;
        }
        if (bio != null) {
            this.bio = bio;
        }
        if (userImageUrl != null) {
            this.userImageUrl = userImageUrl;
        }
        if (favoriteGenres != null) {
            this.favoriteGenres = new ArrayList<>(favoriteGenres);
        }
        if (readingGoal != null) {
            this.readingGoal = readingGoal;
        }
        if (preferredLanguage != null) {
            this.preferredLanguage = preferredLanguage;
        }
        if (isPublicProfile != null) {
            this.isPublicProfile = isPublicProfile;
        }
        if (socialLinks != null) {
            this.socialLinks = new HashMap<>(socialLinks);
        }
        if (location != null) {
            this.location = location;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    public void makeProfilePublic() {
        this.isPublicProfile = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void makeProfilePrivate() {
        this.isPublicProfile = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    
    public UserProfileId getId() {
        return id;
    }
    
    public UserId getUserId() {
        return userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public String getBio() {
        return bio;
    }
    
    public String getUserImageUrl() {
        return userImageUrl;
    }
    
    public List<String> getFavoriteGenres() {
        return new ArrayList<>(favoriteGenres);
    }
    
    public Integer getReadingGoal() {
        return readingGoal;
    }
    
    public String getPreferredLanguage() {
        return preferredLanguage;
    }
    
    public boolean isPublicProfile() {
        return isPublicProfile;
    }
    
    public Map<String, String> getSocialLinks() {
        return new HashMap<>(socialLinks);
    }
    
    public String getLocation() {
        return location;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Object methods
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", isPublicProfile=" + isPublicProfile +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 