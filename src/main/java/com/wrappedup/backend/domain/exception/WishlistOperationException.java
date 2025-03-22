package com.wrappedup.backend.domain.exception;

/**
 * Excepción que se lanza cuando ocurre un error durante una operación en la lista de deseos.
 * Puede encapsular diferentes tipos de errores, como errores de persistencia, errores de búsqueda
 * de libros, etc.
 */
public class WishlistOperationException extends RuntimeException {
    public WishlistOperationException(String message) {
        super(message);
    }
    
    public WishlistOperationException(String message, Throwable cause) {
        super(message, cause);
    }
} 