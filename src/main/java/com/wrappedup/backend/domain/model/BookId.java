package com.wrappedup.backend.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a unique book identifier.
 */
public class BookId {
    private final UUID value;
    
    private BookId(UUID value) {
        this.value = Objects.requireNonNull(value, "Book ID cannot be null");
    }
    
    public static BookId of(UUID value) {
        return new BookId(value);
    }
    
    public static BookId of(String value) {
        return new BookId(UUID.fromString(value));
    }
    
    public static BookId generate() {
        return new BookId(UUID.randomUUID());
    }
    
    /**
     * Creates a BookId from a UUID.
     *
     * @param uuid the UUID value
     * @return a new BookId
     */
    public static BookId fromUUID(UUID uuid) {
        return new BookId(uuid);
    }
    
    public UUID getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookId bookId = (BookId) o;
        return Objects.equals(value, bookId.value);
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