package com.wrappedup.backend.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain entity representing a user in the system.
 * This is a pure domain object without infrastructure concerns.
 */
public class User {
    private final UserId id;
    private final Username username;
    private final Email email;
    private String passwordHash;
    private Role role;
    private boolean enabled;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private User(
            UserId id,
            Username username,
            Email email,
            String passwordHash,
            Role role,
            boolean enabled,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.enabled = enabled;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    /**
     * Creates a new user with a generated ID.
     */
    public static User createNewUser(
            String username,
            String email,
            String passwordHash) {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                UserId.generate(),
                new Username(username),
                new Email(email),
                passwordHash,
                Role.USER,
                true,
                now,
                now
        );
    }
    
    /**
     * Reconstructs an existing user from persistence.
     */
    public static User reconstitute(
            UserId id,
            Username username,
            Email email,
            String passwordHash,
            Role role,
            boolean enabled,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new User(
                id,
                username,
                email,
                passwordHash,
                role,
                enabled,
                createdAt,
                updatedAt
        );
    }
    
    // Domain behavior methods
    
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "Password hash cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void grantAdminRole() {
        this.role = Role.ADMIN;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void revokeAdminRole() {
        this.role = Role.USER;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    
    public UserId getId() {
        return id;
    }
    
    public Username getUsername() {
        return username;
    }
    
    public Email getEmail() {
        return email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public Role getRole() {
        return role;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }
    
    // Object methods
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username=" + username +
                ", email=" + email +
                ", role=" + role +
                ", enabled=" + enabled +
                '}';
    }
    
    /**
     * User roles in the system.
     */
    public enum Role {
        USER,
        ADMIN
    }
} 