package com.wrappedup.backend.infrastructure.adapter.jpa.adapter;

import com.wrappedup.backend.domain.model.*;
import com.wrappedup.backend.domain.port.out.UserRepository;
import com.wrappedup.backend.domain.exception.UserAlreadyExistsException;
import com.wrappedup.backend.infrastructure.adapter.jpa.entity.UserJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Adapter implementation of the UserRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaUserRepositoryAdapter implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    
    @Override
    @Transactional
    public User save(User user) {
        try {
            // Check if this is a new user by checking if it exists in the database
            boolean isExistingUser = user.getId() != null && userJpaRepository.existsById(user.getId().getValue());
            
            // For new users, check username and email uniqueness
            if (!isExistingUser) {
                if (userJpaRepository.existsByUsername(user.getUsername().getValue())) {
                    throw new UserAlreadyExistsException("Username is already taken");
                }
                
                if (userJpaRepository.existsByEmail(user.getEmail().getValue())) {
                    throw new UserAlreadyExistsException("Email is already registered");
                }
            }
            
            // Map domain model to JPA entity
            UserJpaEntity entity = new UserJpaEntity();
            
            // Always set the ID from the domain model - ID comes from the domain
            entity.setId(user.getId().getValue());
            entity.setUsername(user.getUsername().getValue());
            entity.setEmail(user.getEmail().getValue());
            entity.setPassword(user.getPasswordHash());
            entity.setRole(user.getRole().name());
            entity.setEnabled(user.isEnabled());
            
            // For new users, set creation time
            if (!isExistingUser) {
                entity.setCreatedAt(LocalDateTime.now());
            } else {
                // For existing users, preserve creation time
                userJpaRepository.findById(user.getId().getValue())
                        .ifPresent(existing -> entity.setCreatedAt(existing.getCreatedAt()));
            }
            
            entity.setUpdatedAt(LocalDateTime.now());
            
            // Save the entity
            UserJpaEntity savedEntity = userJpaRepository.save(entity);
            
            // Map back to domain model
            return mapToDomainEntity(savedEntity);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while saving user", e);
            throw new DataIntegrityViolationException("User could not be created due to a data conflict", e);
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error saving user", e);
            throw new RuntimeException("Error saving user: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.getValue())
                .map(this::mapToDomainEntity);
    }
    
    @Override
    public Optional<User> findByUsername(Username username) {
        return userJpaRepository.findByUsername(username.getValue())
                .map(this::mapToDomainEntity);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.getValue())
                .map(this::mapToDomainEntity);
    }
    
    @Override
    public boolean existsByUsername(Username username) {
        return userJpaRepository.existsByUsername(username.getValue());
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.getValue());
    }
    
    /**
     * Maps a JPA entity to a domain User entity.
     */
    private User mapToDomainEntity(UserJpaEntity entity) {
        return User.reconstitute(
                UserId.of(entity.getId()),
                new Username(entity.getUsername()),
                new Email(entity.getEmail()),
                entity.getPassword(),
                User.Role.valueOf(entity.getRole()),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
} 