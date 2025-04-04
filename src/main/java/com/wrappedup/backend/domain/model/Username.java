package com.wrappedup.backend.domain.model;

import java.util.Objects;

/**
 * Value object representing a username.
 */
public class Username {
    private final String value;
    
    public Username(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (value.length() < 3 || value.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return Objects.equals(value, username.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
} 