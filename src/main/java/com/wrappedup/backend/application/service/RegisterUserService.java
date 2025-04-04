package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.exception.UserAlreadyExistsException;
import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.UserProfile;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.RegisterUserUseCase;
import com.wrappedup.backend.domain.port.in.UserProfileUseCase;
import com.wrappedup.backend.domain.port.out.HashPasswordPort;
import com.wrappedup.backend.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of the RegisterUserUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterUserService implements RegisterUserUseCase {
    
    private final UserRepository userRepository;
    private final HashPasswordPort hashPasswordPort;
    private final UserProfileUseCase userProfileUseCase;
    
    @Override
    @Transactional
    public UserId registerUser(RegisterUserCommand command) throws UserAlreadyExistsException {
        log.debug("Registering new user with username: {}", command.username());
        
        try {
            // Validate username and email uniqueness
            Username username = new Username(command.username());
            Email email = new Email(command.email());
            
            if (userRepository.existsByUsername(username)) {
                log.debug("Username already exists: {}", command.username());
                throw new UserAlreadyExistsException(UserAlreadyExistsException.USERNAME_EXISTS);
            }
            
            if (userRepository.existsByEmail(email)) {
                log.debug("Email already exists: {}", command.email());
                throw new UserAlreadyExistsException(UserAlreadyExistsException.EMAIL_EXISTS);
            }
            
            // Hash password
            String hashedPassword = hashPasswordPort.hashPassword(command.password());
            
            // Create and save user
            User user = User.createNewUser(
                    command.username(),
                    command.email(),
                    hashedPassword
            );
            
            User savedUser = userRepository.save(user);
            log.info("User registered successfully with ID: {}", savedUser.getId());
            
            User createdUser = userRepository.findById(user.getId()).orElseThrow();

            createUserProfile(createdUser.getId());
            
            return savedUser.getId();
        } catch (UserAlreadyExistsException e) {
            // Rethrow domain exceptions directly
            throw e;
        } catch (Exception e) {
            // Log and wrap all other exceptions
            log.error("Failed to register user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to register user due to an internal error", e);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void createUserProfile(UserId userId) {
        try {
            UserProfile profile = userProfileUseCase.createProfile(userId);
            log.info("Created profile for user with ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to create profile for user: {}", e.getMessage(), e);
            // Silently catch the exception to avoid affecting the main transaction
        }
    }
} 