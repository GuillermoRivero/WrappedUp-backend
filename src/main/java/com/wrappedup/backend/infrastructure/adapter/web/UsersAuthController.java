package com.wrappedup.backend.infrastructure.adapter.web;

import com.wrappedup.backend.domain.port.in.AuthenticateUserUseCase;
import com.wrappedup.backend.domain.port.in.RefreshTokenUseCase;
import com.wrappedup.backend.domain.port.in.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller that maintains backward compatibility with the original API endpoints.
 * This delegates to the authentication controller methods with the original URL structure.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UsersAuthController {
    
    private final UserController authController;
    
    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserController.RegisterUserRequest request) {
        return authController.registerUser(request);
    }
    
    /**
     * Authenticate a user.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserController.AuthenticationRequest request) {
        return authController.login(request);
    }
    
    /**
     * Refresh authentication tokens.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody UserController.RefreshTokenRequest request) {
        return authController.refreshToken(request);
    }
} 