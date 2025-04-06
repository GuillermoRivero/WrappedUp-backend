package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    @DisplayName("Should have exactly two roles defined")
    void shouldHaveExactlyTwoRoles() {
        // Act
        Role[] roles = Role.values();
        
        // Assert
        assertEquals(2, roles.length);
    }
    
    @Test
    @DisplayName("Should have USER role")
    void shouldHaveUserRole() {
        // Act & Assert
        assertTrue(roleExists("USER"));
    }
    
    @Test
    @DisplayName("Should have ADMIN role")
    void shouldHaveAdminRole() {
        // Act & Assert
        assertTrue(roleExists("ADMIN"));
    }
    
    @Test
    @DisplayName("Should be able to convert from string to enum")
    void shouldConvertFromString() {
        // Act & Assert
        assertEquals(Role.USER, Role.valueOf("USER"));
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid role name")
    void shouldThrowExceptionForInvalidRole() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> Role.valueOf("INVALID_ROLE"));
    }
    
    @Test
    @DisplayName("Role enum constants should maintain their ordinal positions")
    void shouldMaintainOrdinalPositions() {
        // Act & Assert
        assertEquals(0, Role.USER.ordinal());
        assertEquals(1, Role.ADMIN.ordinal());
    }
    
    @Test
    @DisplayName("Role enum constants should have correct names")
    void shouldHaveCorrectNames() {
        // Act & Assert
        assertEquals("USER", Role.USER.name());
        assertEquals("ADMIN", Role.ADMIN.name());
    }
    
    // Helper method to check if a role exists
    private boolean roleExists(String roleName) {
        for (Role role : Role.values()) {
            if (role.name().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
} 