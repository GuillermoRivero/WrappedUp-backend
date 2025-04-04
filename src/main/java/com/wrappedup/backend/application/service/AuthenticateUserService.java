package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.port.in.AuthenticateUserUseCase;
import com.wrappedup.backend.domain.port.out.HashPasswordPort;
import com.wrappedup.backend.domain.port.out.JwtTokenPort;
import com.wrappedup.backend.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation of the AuthenticateUserUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticateUserService implements AuthenticateUserUseCase {
    
    private final UserRepository userRepository;
    private final HashPasswordPort hashPasswordPort;
    private final JwtTokenPort jwtTokenPort;
    
    @Override
    @Transactional(readOnly = true)
    public AuthenticationResult authenticate(AuthenticationCommand command) {
        log.debug("Authenticating user with email: {}", command.email());
        
        // Find user by email
        try {
            Email email = new Email(command.email());
            Optional<User> optionalUser = userRepository.findByEmail(email);
            
            // Check if user exists
            if (optionalUser.isEmpty()) {
                log.debug("Authentication failed: User not found with email: {}", command.email());
                throw new IllegalArgumentException("Invalid email or password");
            }
            
            User user = optionalUser.get();
            
            // Check if user is enabled
            if (!user.isEnabled()) {
                log.debug("Authentication failed: User account is disabled: {}", command.email());
                throw new IllegalArgumentException("User account is disabled");
            }
            
            // Validate password
            if (!hashPasswordPort.verifyPassword(command.password(), user.getPasswordHash())) {
                log.debug("Authentication failed: Invalid password for email: {}", command.email());
                throw new IllegalArgumentException("Invalid email or password");
            }
            
            // Generate tokens
            String accessToken = jwtTokenPort.generateAccessToken(user);
            String refreshToken = jwtTokenPort.generateRefreshToken(user);
            
            log.info("User authenticated successfully: {}", user.getUsername());
            
            return new AuthenticationResult(user, accessToken, refreshToken);
            
        } catch (IllegalArgumentException e) {
            log.debug("Authentication failed: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
} 