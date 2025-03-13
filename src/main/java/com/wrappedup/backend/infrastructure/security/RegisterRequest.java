package com.wrappedup.backend.infrastructure.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank
    String username,
    
    @NotBlank
    @Email
    String email,
    
    @NotBlank
    String password
) {} 