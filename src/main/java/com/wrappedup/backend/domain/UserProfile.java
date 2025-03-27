package com.wrappedup.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Basic Information
    @Column(length = 100)
    private String fullName;

    @Column(length = 500)
    private String bio;

    @Column(name = "user_image_url")
    private String userImageUrl;

    // Reading Preferences
    @ElementCollection
    @CollectionTable(name = "user_favorite_genres", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Column(name = "genre")
    private List<String> favoriteGenres;

    @Column(name = "reading_goal")
    private Integer readingGoal;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    // Social Features
    @Column(name = "is_public_profile")
    private boolean isPublicProfile = true;

    @ElementCollection
    @CollectionTable(name = "user_social_links", joinColumns = @JoinColumn(name = "user_profile_id"))
    @MapKeyColumn(name = "platform")
    @Column(name = "url")
    private Map<String, String> socialLinks;

    private String location;

    // Timestamps
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UserProfile(User user) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
} 