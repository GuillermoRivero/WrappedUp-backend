package com.wrappedup.backend.infrastructure.adapter.security;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.port.in.GetUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserDetailsService implementation that uses the domain model User.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DomainUserDetailsService implements UserDetailsService {
    
    private final GetUserUseCase getUserUseCase;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Authenticating user with email: {}", email);
        
        try {
            Optional<User> userOpt = getUserUseCase.getUserByEmail(email);
            
            if (userOpt.isEmpty()) {
                log.debug("User not found with email: {}", email);
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            
            User user = userOpt.get();
            
            // Check if user is enabled
            if (!user.isEnabled()) {
                log.debug("User account is disabled: {}", email);
                throw new UsernameNotFoundException("User account is disabled");
            }
            
            return new DomainUserDetails(user);
        } catch (IllegalArgumentException e) {
            log.debug("Invalid email format: {}", email);
            throw new UsernameNotFoundException("Invalid email format");
        }
    }
    
    /**
     * Custom UserDetails implementation that wraps our domain User.
     */
    public static class DomainUserDetails implements UserDetails {
        private final User user;
        
        public DomainUserDetails(User user) {
            this.user = user;
        }
        
        @Override
        public List<SimpleGrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }
        
        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }
        
        @Override
        public String getUsername() {
            return user.getEmail().getValue();
        }
        
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        @Override
        public boolean isEnabled() {
            return user.isEnabled();
        }
        
        public User getUser() {
            return user;
        }
        
        public String getId() {
            return user.getId().toString();
        }
    }
} 