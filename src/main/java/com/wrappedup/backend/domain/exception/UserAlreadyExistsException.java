package com.wrappedup.backend.domain.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 */
public class UserAlreadyExistsException extends DomainException {
    
    public static final String USERNAME_EXISTS = "A user with this username already exists";
    public static final String EMAIL_EXISTS = "A user with this email already exists";
    
    public UserAlreadyExistsException(String message) {
        super(message);
    }
} 