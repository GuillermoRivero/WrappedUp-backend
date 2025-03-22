package com.wrappedup.backend.domain.exception;

/**
 * Excepción que se lanza cuando hay problemas al persistir un libro en la base de datos.
 * Indica errores específicos de persistencia como problemas de concurrencia, 
 * errores de integridad de datos, etc.
 */
public class BookPersistenceException extends RuntimeException {
    public BookPersistenceException(String message) {
        super(message);
    }
    
    public BookPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
} 