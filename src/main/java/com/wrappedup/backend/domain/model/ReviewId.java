package com.wrappedup.backend.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a review identifier.
 */
public class ReviewId {
    private final UUID value;
    
    private ReviewId(UUID value) {
        this.value = Objects.requireNonNull(value, "Review ID value cannot be null");
    }
    
    /**
     * Creates a new ReviewId from a UUID.
     */
    public static ReviewId fromUUID(UUID id) {
        return new ReviewId(id);
    }
    
    /**
     * Creates a new ReviewId from a string representation of a UUID.
     */
    public static ReviewId fromString(String id) {
        return new ReviewId(UUID.fromString(id));
    }
    
    /**
     * Generates a new random ReviewId.
     */
    public static ReviewId generate() {
        return new ReviewId(UUID.randomUUID());
    }
    
    /**
     * Gets the UUID value of this ReviewId.
     */
    public UUID getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewId reviewId = (ReviewId) o;
        return Objects.equals(value, reviewId.value);
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