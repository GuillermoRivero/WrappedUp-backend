package com.wrappedup.backend.domain.exception;

/**
 * Excepción que se lanza cuando un libro no se encuentra en el repositorio local
 * ni en OpenLibrary.
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }
    
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 