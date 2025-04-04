package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.RefreshTokenUseCase;
import com.wrappedup.backend.domain.port.out.JwtTokenPort;
import com.wrappedup.backend.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation of the RefreshTokenUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService implements RefreshTokenUseCase {
    
    private final JwtTokenPort jwtTokenPort;
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public RefreshTokenResult refreshToken(RefreshTokenCommand command) {
        log.debug("Refreshing token");
        
        String refreshToken = command.refreshToken();
        
        // Validate refresh token
        if (jwtTokenPort.isTokenExpired(refreshToken)) {
            log.debug("Refresh token is expired");
            throw new IllegalArgumentException("Refresh token is expired");
        }
        
        // Extract user ID from token
        Optional<String> userId = jwtTokenPort.validateTokenAndGetUserId(refreshToken);
        if (userId.isEmpty()) {
            log.debug("Invalid refresh token");
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        // Find user
        Optional<User> optionalUser = userRepository.findById(UserId.of(userId.get()));
        if (optionalUser.isEmpty()) {
            log.debug("User not found for token");
            throw new IllegalArgumentException("User not found");
        }
        
        User user = optionalUser.get();
        
        // Check if user is enabled
        if (!user.isEnabled()) {
            log.debug("User account is disabled");
            throw new IllegalArgumentException("User account is disabled");
        }
        
        // Generate new tokens
        String newAccessToken = jwtTokenPort.generateAccessToken(user);
        String newRefreshToken = jwtTokenPort.generateRefreshToken(user);
        
        log.info("Token refreshed successfully for user: {}", user.getUsername());
        
        return new RefreshTokenResult(user, newAccessToken, newRefreshToken);
    }
} 