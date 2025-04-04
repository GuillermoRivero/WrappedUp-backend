package com.wrappedup.backend.domain.port.out;

/**
 * Output port for password hashing operations.
 */
public interface HashPasswordPort {
    /**
     * Hashes a plain-text password.
     *
     * @param plainPassword The plain-text password to hash
     * @return The hashed password
     */
    String hashPassword(String plainPassword);

    /**
     * Verifies if a plain-text password matches a hashed password.
     *
     * @param plainPassword The plain-text password to check
     * @param hashedPassword The hashed password to verify against
     * @return true if the passwords match, false otherwise
     */
    boolean verifyPassword(String plainPassword, String hashedPassword);
} 