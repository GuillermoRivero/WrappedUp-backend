package com.wrappedup.backend.infrastructure.controller;

import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.infrastructure.security.AuthenticationRequest;
import com.wrappedup.backend.infrastructure.security.AuthenticationResponse;
import com.wrappedup.backend.infrastructure.security.AuthenticationService;
import com.wrappedup.backend.infrastructure.security.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthenticationResponse> getCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(service.getCurrentUser(user));
    }
} 