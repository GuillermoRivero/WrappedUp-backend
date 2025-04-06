package com.wrappedup.backend.infrastructure.config;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.out.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationConfigTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationConfig applicationConfig;

    @Test
    @DisplayName("Should create UserDetailsService")
    void userDetailsService_ShouldReturnUserDetailsService() {
        // Arrange
        String email = "test@example.com";
        User mockUser = createMockUser(email);
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(mockUser));
        
        // Act
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        
        // Assert
        assertNotNull(userDetailsService);
        assertDoesNotThrow(() -> userDetailsService.loadUserByUsername(email));
    }
    
    @Test
    @DisplayName("UserDetailsService should throw UsernameNotFoundException when user not found")
    void userDetailsService_ShouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());
        
        // Act & Assert
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
    }
    
    @Test
    @DisplayName("UserDetailsService should throw UsernameNotFoundException when email is invalid")
    void userDetailsService_ShouldThrowUsernameNotFoundExceptionWhenEmailIsInvalid() {
        // Arrange
        String invalidEmail = "invalid-email";
        
        // Act & Assert
        UserDetailsService userDetailsService = applicationConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(invalidEmail));
    }
    
    @Test
    @DisplayName("Should create AuthenticationProvider")
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {
        // Act
        AuthenticationProvider authProvider = applicationConfig.authenticationProvider();
        
        // Assert
        assertNotNull(authProvider);
        assertTrue(authProvider instanceof DaoAuthenticationProvider);
    }
    
    @Test
    @DisplayName("Should create PasswordEncoder")
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // Act
        var passwordEncoder = applicationConfig.passwordEncoder();
        
        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }
    
    @Test
    @DisplayName("Should create RestTemplate")
    void restTemplate_ShouldReturnRestTemplate() {
        // Act
        RestTemplate restTemplate = applicationConfig.restTemplate();
        
        // Assert
        assertNotNull(restTemplate);
        assertEquals(RestTemplate.class, restTemplate.getClass());
    }
    
    private User createMockUser(String email) {
        LocalDateTime now = LocalDateTime.now();
        return User.reconstitute(
                UserId.fromUUID(UUID.randomUUID()),
                new Username("testuser"),
                new Email(email),
                "hashedPassword",
                User.Role.USER,
                true,
                now,
                now
        );
    }
} 