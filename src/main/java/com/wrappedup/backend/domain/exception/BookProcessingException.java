package com.wrappedup.backend.domain.exception;

/**
 * Excepción que se lanza cuando hay problemas generales al procesar un libro.
 * Puede encapsular errores de comunicación con APIs externas, errores de parsing,
 * o cualquier otro error que ocurra durante el procesamiento de un libro.
 */
public class BookProcessingException extends RuntimeException {
    public BookProcessingException(String message) {
        super(message);
    }
    
    public BookProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
} 