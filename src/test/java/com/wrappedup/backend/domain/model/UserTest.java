package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private final UserId userId = UserId.generate();
    private final Username username = new Username("testuser");
    private final Email email = new Email("test@example.com");
    private final String passwordHash = "hashedpassword123";
    private final User.Role role = User.Role.USER;
    private final boolean enabled = true;
    private final LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    private final LocalDateTime updatedAt = LocalDateTime.now();
    
    @Test
    @DisplayName("Should create a new user with all fields")
    void createNewUser_ShouldCreateUserWithAllFields() {
        // Act
        User user = User.createNewUser(
                username.getValue(), 
                email.getValue(), 
                passwordHash
        );
        
        // Assert
        assertNotNull(user);
        assertNotNull(user.getId());
        assertNotNull(user.getUsername());
        assertEquals(username.getValue(), user.getUsername().getValue());
        assertNotNull(user.getEmail());
        assertEquals(email.getValue(), user.getEmail().getValue());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(User.Role.USER, user.getRole());
        assertTrue(user.isEnabled());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertEquals(user.getCreatedAt(), user.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should reconstitute an existing user with all fields")
    void reconstitute_ShouldCreateUserWithAllFields() {
        // Act
        User user = User.reconstitute(
                userId, username, email, passwordHash, role, enabled, createdAt, updatedAt
        );
        
        // Assert
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHash, user.getPasswordHash());
        assertEquals(role, user.getRole());
        assertEquals(enabled, user.isEnabled());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should enable a user")
    void enable_ShouldSetEnabledToTrue() {
        // Arrange
        User disabledUser = User.reconstitute(
                userId, username, email, passwordHash, role, false, createdAt, updatedAt
        );
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        disabledUser.enable();
        
        // Assert
        assertTrue(disabledUser.isEnabled());
        assertTrue(disabledUser.getUpdatedAt().isAfter(beforeUpdate) || 
                disabledUser.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should disable a user")
    void disable_ShouldSetEnabledToFalse() {
        // Arrange
        User enabledUser = User.reconstitute(
                userId, username, email, passwordHash, role, true, createdAt, updatedAt
        );
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        enabledUser.disable();
        
        // Assert
        assertFalse(enabledUser.isEnabled());
        assertTrue(enabledUser.getUpdatedAt().isAfter(beforeUpdate) || 
                enabledUser.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should update password")
    void updatePassword_ShouldChangePasswordHash() {
        // Arrange
        User user = User.reconstitute(
                userId, username, email, passwordHash, role, enabled, createdAt, updatedAt
        );
        String newPasswordHash = "newhashedpassword456";
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        user.updatePassword(newPasswordHash);
        
        // Assert
        assertEquals(newPasswordHash, user.getPasswordHash());
        assertTrue(user.getUpdatedAt().isAfter(beforeUpdate) || 
                user.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should grant admin role")
    void grantAdminRole_ShouldChangeRoleToAdmin() {
        // Arrange
        User user = User.reconstitute(
                userId, username, email, passwordHash, User.Role.USER, enabled, createdAt, updatedAt
        );
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        user.grantAdminRole();
        
        // Assert
        assertEquals(User.Role.ADMIN, user.getRole());
        assertTrue(user.isAdmin());
        assertTrue(user.getUpdatedAt().isAfter(beforeUpdate) || 
                user.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Should revoke admin role")
    void revokeAdminRole_ShouldChangeRoleToUser() {
        // Arrange
        User user = User.reconstitute(
                userId, username, email, passwordHash, User.Role.ADMIN, enabled, createdAt, updatedAt
        );
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        user.revokeAdminRole();
        
        // Assert
        assertEquals(User.Role.USER, user.getRole());
        assertFalse(user.isAdmin());
        assertTrue(user.getUpdatedAt().isAfter(beforeUpdate) || 
                user.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Equal users should be equal")
    void equals_WithSameId_ShouldBeEqual() {
        // Arrange
        User user1 = User.reconstitute(
                userId, username, email, passwordHash, role, enabled, createdAt, updatedAt
        );
        
        User user2 = User.reconstitute(
                userId, username, email, passwordHash, role, enabled, createdAt, updatedAt
        );
        
        // Assert
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
    
    @Test
    @DisplayName("Users with different IDs should not be equal")
    void equals_WithDifferentIds_ShouldNotBeEqual() {
        // Arrange
        User user1 = User.reconstitute(
                userId, username, email, passwordHash, role, enabled, createdAt, updatedAt
        );
        
        User user2 = User.reconstitute(
                UserId.generate(), username, email, passwordHash, role, enabled, createdAt, updatedAt
        );
        
        // Assert
        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
    }
} 