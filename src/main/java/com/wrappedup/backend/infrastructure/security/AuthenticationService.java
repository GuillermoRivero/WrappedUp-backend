package com.wrappedup.backend.infrastructure.security;

import com.wrappedup.backend.domain.Role;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = new User(
            request.username(),
            request.email(),
            passwordEncoder.encode(request.password())
        );
        
        user = userRepository.save(user);
        var token = jwtService.generateToken(user);
        
        return AuthenticationResponse.of(
            token,
            user.getId(),
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
        
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
        var token = jwtService.generateToken(user);
        
        return AuthenticationResponse.of(
            token,
            user.getId(),
            user.getEmail(),
            user.getRole()
        );
    }

    public AuthenticationResponse getCurrentUser(User user) {
        var token = jwtService.generateToken(user);
        
        return AuthenticationResponse.of(
            token,
            user.getId(),
            user.getEmail(),
            user.getRole()
        );
    }
} 