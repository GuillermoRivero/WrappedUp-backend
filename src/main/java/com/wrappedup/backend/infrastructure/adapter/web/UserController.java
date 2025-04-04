package com.wrappedup.backend.infrastructure.adapter.web;

import com.wrappedup.backend.domain.exception.UserAlreadyExistsException;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.port.in.AuthenticateUserUseCase;
import com.wrappedup.backend.domain.port.in.GetUserUseCase;
import com.wrappedup.backend.domain.port.in.RefreshTokenUseCase;
import com.wrappedup.backend.domain.port.in.RegisterUserUseCase;
import com.wrappedup.backend.domain.port.out.JwtTokenPort;
import com.wrappedup.backend.infrastructure.adapter.security.DomainUserDetailsService.DomainUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user-related operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final GetUserUseCase getUserUseCase;
    private final JwtTokenPort jwtTokenPort;
    
    /**
     * DTO for user registration requests.
     */
    record RegisterUserRequest(
            @NotBlank @Size(min = 3, max = 50) String username,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, max = 100) String password
    ) {}
    
    /**
     * DTO for authentication requests.
     */
    record AuthenticationRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}
    
    /**
     * DTO for token refresh requests.
     */
    record RefreshTokenRequest(
            @NotBlank String refreshToken
    ) {}
    
    /**
     * DTO for authentication responses.
     * Includes both 'token' (for frontend compatibility) and accessToken/refreshToken fields
     */
    record AuthenticationResponse(
            String userId,
            String username,
            String email,
            String role,
            String token,         // Added for frontend compatibility
            String accessToken,   
            String refreshToken
    ) {
        // Static factory method for creating response with token field mapping to accessToken
        public static AuthenticationResponse fromAuthResult(User user, String accessToken, String refreshToken) {
            return new AuthenticationResponse(
                user.getId().toString(),
                user.getUsername().getValue(),
                user.getEmail().getValue(),
                user.getRole().name(),
                accessToken,      // Maps accessToken to token field for frontend
                accessToken,
                refreshToken
            );
        }
    }
    
    /**
     * DTO for user responses.
     */
    record UserResponse(
            String id,
            String username,
            String email,
            String role,
            boolean enabled
    ) {}
    
    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        try {
            var command = new RegisterUserUseCase.RegisterUserCommand(
                    request.username(),
                    request.email(),
                    request.password()
            );
            
            var userId = registerUserUseCase.registerUser(command);
            
            // After registration, we should authenticate the user to return tokens
            try {
                var authCommand = new AuthenticateUserUseCase.AuthenticationCommand(
                    request.email(),
                    request.password()
                );
                
                var result = authenticateUserUseCase.authenticate(authCommand);
                User user = result.user();
                
                var response = AuthenticationResponse.fromAuthResult(
                    user,
                    result.accessToken(),
                    result.refreshToken()
                );
                
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } catch (Exception e) {
                // If auto-login fails, just return the user ID as before
                log.warn("Auto-login after registration failed: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.CREATED).body(userId.toString());
            }
        } catch (UserAlreadyExistsException e) {
            log.debug("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            log.debug("User registration validation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * Authenticate a user.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationRequest request) {
        try {
            var command = new AuthenticateUserUseCase.AuthenticationCommand(
                    request.email(),
                    request.password()
            );
            
            var result = authenticateUserUseCase.authenticate(command);
            User user = result.user();
            
            var response = AuthenticationResponse.fromAuthResult(
                user,
                result.accessToken(),
                result.refreshToken()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.debug("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    /**
     * Refresh authentication tokens.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            var command = new RefreshTokenUseCase.RefreshTokenCommand(request.refreshToken());
            var result = refreshTokenUseCase.refreshToken(command);
            User user = result.user();
            
            var response = AuthenticationResponse.fromAuthResult(
                user,
                result.accessToken(),
                result.refreshToken()
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.debug("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    /**
     * Get the current authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal DomainUserDetails userDetails) {
        try {
            // Get authentication from context
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                // Get email from authentication name
                String email = auth.getName();
                
                if (email != null && !email.isEmpty()) {
                    // Find the user by email
                    var userOpt = getUserUseCase.getUserByEmail(email);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        
                        // Generate fresh tokens
                        String accessToken = jwtTokenPort.generateAccessToken(user);
                        String refreshToken = jwtTokenPort.generateRefreshToken(user);
                        
                        // Create response
                        var response = AuthenticationResponse.fromAuthResult(
                            user,
                            accessToken,
                            refreshToken
                        );
                        
                        return ResponseEntity.ok(response);
                    }
                }
            }
            
            // User not found or authentication missing
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        } catch (Exception e) {
            log.error("Error processing /me request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fetching user data");
        }
    }
} 