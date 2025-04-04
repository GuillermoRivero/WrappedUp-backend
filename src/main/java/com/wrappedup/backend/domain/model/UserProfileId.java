package com.wrappedup.backend.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique user profile identifier.
 */
public class UserProfileId {
    private final UUID value;
    
    private UserProfileId(UUID value) {
        this.value = Objects.requireNonNull(value, "User Profile ID cannot be null");
    }
    
    public static UserProfileId of(UUID value) {
        return new UserProfileId(value);
    }
    
    public static UserProfileId of(String value) {
        return new UserProfileId(UUID.fromString(value));
    }
    
    public static UserProfileId generate() {
        return new UserProfileId(UUID.randomUUID());
    }
    
    public UUID getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileId profileId = (UserProfileId) o;
        return Objects.equals(value, profileId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
} 