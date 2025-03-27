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
        
        // Create default profile
        UserProfile profile = new UserProfile(user);
        profile.setFullName(request.username()); // Default to username
        profile.setBio(""); // Empty bio
        profile.setFavoriteGenres(new ArrayList<>()); // Empty genres
        profile.setReadingGoal(12); // Default: 1 book per month
        profile.setPreferredLanguage("English"); // Default language
        profile.setPublicProfile(true); // Public by default
        profile.setSocialLinks(new HashMap<>()); // Empty social links
        
        userProfileRepository.save(profile);
        
        log.info("Created default profile for user: {}", user.getId());
        
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
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
            )
        );
        
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
        var token = jwtService.generateToken(user);
        
        return AuthenticationResponse.of(
            token,
            user.getId(),
            user.getRealUsername(),
            user.getEmail(),
            user.getRole()
        );
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