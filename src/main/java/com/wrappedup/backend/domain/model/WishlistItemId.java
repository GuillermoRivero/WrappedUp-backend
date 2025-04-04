package com.wrappedup.backend.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing the unique identifier of a wishlist item.
 */
public final class WishlistItemId {
    private final UUID value;
    
    private WishlistItemId(UUID value) {
        this.value = Objects.requireNonNull(value, "WishlistItemId value cannot be null");
    }
    
    /**
     * Creates a WishlistItemId from a UUID.
     *
     * @param uuid the UUID value
     * @return a new WishlistItemId
     */
    public static WishlistItemId fromUUID(UUID uuid) {
        return new WishlistItemId(uuid);
    }
    
    /**
     * Creates a WishlistItemId from a string representation of a UUID.
     *
     * @param uuid the string representation of a UUID
     * @return a new WishlistItemId
     */
    public static WishlistItemId fromString(String uuid) {
        return new WishlistItemId(UUID.fromString(uuid));
    }
    
    /**
     * Generates a new random WishlistItemId.
     *
     * @return a new random WishlistItemId
     */
    public static WishlistItemId generate() {
        return new WishlistItemId(UUID.randomUUID());
    }
    
    /**
     * Returns the UUID value of this WishlistItemId.
     *
     * @return the UUID value
     */
    public UUID getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WishlistItemId that = (WishlistItemId) o;
        return Objects.equals(value, that.value);
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