package com.wrappedup.backend.infrastructure.controller;

import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.infrastructure.security.AuthenticationRequest;
import com.wrappedup.backend.infrastructure.security.AuthenticationService;
import com.wrappedup.backend.infrastructure.security.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(service.register(request));
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Registration error", e);
            return createErrorResponse("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            return ResponseEntity.ok(service.authenticate(request));
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            
            if (message.contains("not found")) {
                return createErrorResponse(message, HttpStatus.NOT_FOUND);
            } else if (message.contains("Invalid email or password")) {
                return createErrorResponse(message, HttpStatus.UNAUTHORIZED);
            } else if (message.contains("locked") || message.contains("disabled")) {
                return createErrorResponse(message, HttpStatus.FORBIDDEN);
            } else {
                return createErrorResponse(message, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("Authentication error", e);
            return createErrorResponse("Authentication failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        try {
            if (user == null) {
                return createErrorResponse("User not authenticated", HttpStatus.UNAUTHORIZED);
            }
            return ResponseEntity.ok(service.getCurrentUser(user));
        } catch (Exception e) {
            log.error("Error getting current user", e);
            return createErrorResponse("Error retrieving user details: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        return new ResponseEntity<>(error, status);
    }
} 