package com.wrappedup.backend.infrastructure.exception;

import com.wrappedup.backend.domain.exception.UserAlreadyExistsException;
import org.hibernate.StaleStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().message());
    }

    @Test
    void handleUserAlreadyExistsException_ShouldReturnConflict() {
        // Arrange
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User already exists");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleUserAlreadyExistsException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already exists", response.getBody().message());
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbidden() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(ex);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You don't have permission to access this resource", response.getBody().message());
    }

    @Test
    void handleBadCredentialsException_ShouldReturnUnauthorized() {
        // Arrange
        BadCredentialsException ex = new BadCredentialsException("Bad credentials");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleBadCredentialsException(ex);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody().message());
    }

    @Test
    void handleValidationExceptions_ShouldReturnBadRequestWithValidationErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        
        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError("object", "email", "Email is required"));
        fieldErrors.add(new FieldError("object", "password", "Password is too short"));
        
        when(bindingResult.getAllErrors()).thenReturn(new ArrayList<>(fieldErrors));

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> errors = response.getBody();
        assertEquals(2, errors.size());
        assertEquals("Email is required", errors.get("email"));
        assertEquals("Password is too short", errors.get("password"));
    }

    @Test
    void handleConstraintViolationException_ShouldReturnBadRequest() {
        // Arrange
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        when(ex.getMessage()).thenReturn("Constraint violation");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleConstraintViolationException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Validation error"));
    }

    @Test
    void handleDataIntegrityViolationException_ShouldReturnConflict() {
        // Arrange
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Data integrity violation");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleDataIntegrityViolationException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Data conflict error"));
    }

    @Test
    void handleOptimisticLockingFailureException_ShouldReturnConflict() {
        // Arrange
        OptimisticLockingFailureException ex = new OptimisticLockingFailureException("Optimistic locking failure");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleOptimisticLockingFailureException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("database concurrency error"));
    }

    @Test
    void handleOptimisticLockException_ShouldReturnConflict() {
        // Arrange
        OptimisticLockException ex = new OptimisticLockException("Optimistic lock exception");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleOptimisticLockingFailureException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("database concurrency error"));
    }

    @Test
    void handleStaleStateException_ShouldReturnConflict() {
        // Arrange
        StaleStateException ex = new StaleStateException("Stale state exception");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleStaleStateException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("modified elsewhere"));
    }

    @Test
    void handleRuntimeException_WithVersionConflict_ShouldReturnConflict() {
        // Arrange
        RuntimeException ex = new RuntimeException("The row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): uninitialized version value");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleRuntimeException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Unable to process this request"));
    }

    @Test
    void handleRuntimeException_WithDetachedEntity_ShouldReturnConflict() {
        // Arrange
        RuntimeException ex = new RuntimeException("Detached entity passed to persist");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleRuntimeException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Unable to process this request"));
    }

    @Test
    void handleRuntimeException_WithGeneratedId_ShouldReturnConflict() {
        // Arrange
        RuntimeException ex = new RuntimeException("Error with generated id");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleRuntimeException(ex);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Unable to process this request"));
    }

    @Test
    void handleRuntimeException_Generic_ShouldReturnInternalServerError() {
        // Arrange
        RuntimeException ex = new RuntimeException("Some generic runtime error");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleRuntimeException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().message().contains("An unexpected error occurred"));
    }

    @Test
    void handleMethodArgumentTypeMismatchException_WithUUID_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        IllegalArgumentException cause = new IllegalArgumentException("Invalid UUID string");
        when(ex.getCause()).thenReturn(cause);
        when(ex.getMessage()).thenReturn("Failed to convert value");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Invalid ID format"));
    }

    @Test
    void handleMethodArgumentTypeMismatchException_Generic_ShouldReturnBadRequest() {
        // Arrange
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        IllegalArgumentException cause = new IllegalArgumentException("Some other error");
        when(ex.getCause()).thenReturn(cause);
        when(ex.getMessage()).thenReturn("Failed to convert value");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid parameter format", response.getBody().message());
    }

    @Test
    void handleGeneralException_ShouldReturnInternalServerError() {
        // Arrange
        Exception ex = new Exception("General exception");

        // Act
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = globalExceptionHandler.handleGeneralException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().message());
    }
} 