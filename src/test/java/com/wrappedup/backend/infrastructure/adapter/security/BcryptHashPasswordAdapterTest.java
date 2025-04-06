package com.wrappedup.backend.infrastructure.adapter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BcryptHashPasswordAdapterTest {

    private BcryptHashPasswordAdapter hashPasswordAdapter;

    @BeforeEach
    void setUp() {
        hashPasswordAdapter = new BcryptHashPasswordAdapter();
    }

    @Test
    void hashPassword_ShouldReturnEncodedValue() {
        // Arrange
        String plainPassword = "Password123!";

        // Act
        String hashedPassword = hashPasswordAdapter.hashPassword(plainPassword);

        // Assert
        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
    }

    @Test
    void verifyPassword_ShouldReturnTrue_WhenPasswordMatches() {
        // Arrange
        String plainPassword = "Password123!";
        String hashedPassword = hashPasswordAdapter.hashPassword(plainPassword);

        // Act
        boolean result = hashPasswordAdapter.verifyPassword(plainPassword, hashedPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void verifyPassword_ShouldReturnFalse_WhenPasswordDoesNotMatch() {
        // Arrange
        String plainPassword = "Password123!";
        String wrongPassword = "WrongPassword123!";
        String hashedPassword = hashPasswordAdapter.hashPassword(plainPassword);

        // Act
        boolean result = hashPasswordAdapter.verifyPassword(wrongPassword, hashedPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void hashPassword_ShouldGenerateDifferentHashes_ForSamePassword() {
        // Arrange
        String plainPassword = "Password123!";

        // Act
        String hashedPassword1 = hashPasswordAdapter.hashPassword(plainPassword);
        String hashedPassword2 = hashPasswordAdapter.hashPassword(plainPassword);

        // Assert
        assertNotEquals(hashedPassword1, hashedPassword2);
    }
} 