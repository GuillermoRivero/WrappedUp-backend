package com.wrappedup.backend.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique user identifier.
 */
public class UserId {
    private final UUID value;
    
    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "User ID cannot be null");
    }
    
    public static UserId of(UUID value) {
        return new UserId(value);
    }
    
    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }
    
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
    
    /**
     * Creates a UserId from a UUID.
     *
     * @param uuid the UUID value
     * @return a new UserId
     */
    public static UserId fromUUID(UUID uuid) {
        return new UserId(uuid);
    }
    
    public UUID getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
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