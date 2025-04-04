package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.port.in.GetUserUseCase;
import com.wrappedup.backend.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation of the GetUserUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserService implements GetUserUseCase {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UserId id) {
        log.debug("Getting user by ID: {}", id);
        return userRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        log.debug("Getting user by username: {}", username);
        try {
            Username usernameObj = new Username(username);
            return userRepository.findByUsername(usernameObj);
        } catch (IllegalArgumentException e) {
            log.debug("Invalid username format: {}", username);
            return Optional.empty();
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        log.debug("Getting user by email: {}", email);
        try {
            Email emailObj = new Email(email);
            return userRepository.findByEmail(emailObj);
        } catch (IllegalArgumentException e) {
            log.debug("Invalid email format: {}", email);
            return Optional.empty();
        }
    }
} 