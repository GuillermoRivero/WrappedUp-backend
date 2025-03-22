package com.wrappedup.backend.infrastructure.security;

import com.wrappedup.backend.domain.Role;

import java.util.UUID;

public record AuthenticationResponse(
    String token,
    UUID id,
    String username,
    String email,
    Role role
) {
    public static AuthenticationResponse of(String token, UUID id, String username, String email, Role role) {
        return new AuthenticationResponse(token, id, username, email, role);
    }
} 