package com.wrappedup.backend.infrastructure.security;

import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.UserProfile;
import com.wrappedup.backend.domain.port.UserProfileRepository;
import com.wrappedup.backend.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        var user = new User(
            request.username(),
            request.email(),
            passwordEncoder.encode(request.password())
        );
        
        user = userRepository.save(user);
        
        UserProfile profile = new UserProfile(user);
        profile.setFullName(request.username());
        profile.setBio("");
        profile.setFavoriteGenres(new ArrayList<>());
        profile.setReadingGoal(12);
        profile.setPreferredLanguage("English");
        profile.setPublicProfile(true); 
        profile.setSocialLinks(new HashMap<>());
        
        userProfileRepository.save(profile);
        
        var token = jwtService.generateToken(user);
        
        return AuthenticationResponse.of(
            token,
            user.getId(),
            user.getRealUsername(),
            user.getEmail(),
            user.getRole()
        );
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            if (request.email() == null || request.email().isEmpty()) {
                log.warn("Authentication failed: Email is required");
                throw new IllegalArgumentException("Email is required");
            }
            
            if (request.password() == null || request.password().isEmpty()) {
                log.warn("Authentication failed: Password is required");
                throw new IllegalArgumentException("Password is required");
            }
            
            var userOptional = userRepository.findByEmail(request.email());
            if (userOptional.isEmpty()) {
                log.warn("Authentication failed: User with email {} not found", request.email());
                throw new IllegalArgumentException("User not found with email: " + request.email());
            }
            
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.email(),
                    request.password()
                )
            );
            
            User user = userOptional.get();
            var token = jwtService.generateToken(user);
                        
            return AuthenticationResponse.of(
                token,
                user.getId(),
                user.getRealUsername(),
                user.getEmail(),
                user.getRole()
            );
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            log.warn("Authentication failed: Invalid credentials for email: {}", request.email());
            throw new IllegalArgumentException("Invalid email or password");
        } catch (org.springframework.security.authentication.LockedException e) {
            log.warn("Authentication failed: Account is locked for email: {}", request.email());
            throw new IllegalArgumentException("Your account has been locked. Please contact support.");
        } catch (org.springframework.security.authentication.DisabledException e) {
            log.warn("Authentication failed: Account is disabled for email: {}", request.email());
            throw new IllegalArgumentException("Your account has been disabled. Please contact support.");
        } catch (Exception e) {
            log.error("Authentication failed with unexpected error for email: {}", request.email(), e);
            throw new IllegalArgumentException("Authentication failed: " + e.getMessage());
        }
    }

    public AuthenticationResponse getCurrentUser(User user) {
        var token = jwtService.generateToken(user);
        
        return AuthenticationResponse.of(
            token,
            user.getId(),
            user.getRealUsername(),
            user.getEmail(),
            user.getRole()
        );
    }
} 