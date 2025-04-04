package com.wrappedup.backend.infrastructure.adapter.security;

import com.wrappedup.backend.domain.port.out.HashPasswordPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adapter implementation of the HashPasswordPort using BCrypt.
 */
@Component
public class BcryptHashPasswordAdapter implements HashPasswordPort {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public BcryptHashPasswordAdapter() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @Override
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    @Override
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
} 