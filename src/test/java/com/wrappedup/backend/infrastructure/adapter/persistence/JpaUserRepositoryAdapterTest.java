package com.wrappedup.backend.infrastructure.adapter.persistence;

import com.wrappedup.backend.domain.exception.UserAlreadyExistsException;
import com.wrappedup.backend.domain.model.Email;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.UserJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private JpaUserRepositoryAdapter adapter;

    @Test
    void save_ShouldReturnSavedUser_WhenSavingNewUser() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = createTestUser(id);
        UserJpaEntity entity = createTestUserEntity(id);
        
        when(userJpaRepository.existsById(id)).thenReturn(false);
        when(userJpaRepository.existsByUsername(user.getUsername().getValue())).thenReturn(false);
        when(userJpaRepository.existsByEmail(user.getEmail().getValue())).thenReturn(false);
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenReturn(entity);
        
        // Act
        User result = adapter.save(user);
        
        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId().getValue());
        assertEquals(user.getUsername().getValue(), result.getUsername().getValue());
        assertEquals(user.getEmail().getValue(), result.getEmail().getValue());
        verify(userJpaRepository).save(any(UserJpaEntity.class));
    }
    
    @Test
    void save_ShouldReturnSavedUser_WhenUpdatingExistingUser() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = createTestUser(id);
        UserJpaEntity existingEntity = createTestUserEntity(id);
        existingEntity.setCreatedAt(LocalDateTime.now().minusDays(10));
        UserJpaEntity savedEntity = createTestUserEntity(id);
        savedEntity.setCreatedAt(existingEntity.getCreatedAt());
        
        when(userJpaRepository.existsById(id)).thenReturn(true);
        when(userJpaRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenReturn(savedEntity);
        
        // Act
        User result = adapter.save(user);
        
        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId().getValue());
        assertEquals(existingEntity.getCreatedAt(), result.getCreatedAt());
        verify(userJpaRepository).save(any(UserJpaEntity.class));
    }
    
    @Test
    void save_ShouldThrowUserAlreadyExistsException_WhenUsernameIsTaken() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = createTestUser(id);
        
        when(userJpaRepository.existsById(id)).thenReturn(false);
        when(userJpaRepository.existsByUsername(user.getUsername().getValue())).thenReturn(true);
        
        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> adapter.save(user));
        verify(userJpaRepository, never()).save(any(UserJpaEntity.class));
    }
    
    @Test
    void save_ShouldThrowUserAlreadyExistsException_WhenEmailIsRegistered() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = createTestUser(id);
        
        when(userJpaRepository.existsById(id)).thenReturn(false);
        when(userJpaRepository.existsByUsername(user.getUsername().getValue())).thenReturn(false);
        when(userJpaRepository.existsByEmail(user.getEmail().getValue())).thenReturn(true);
        
        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> adapter.save(user));
        verify(userJpaRepository, never()).save(any(UserJpaEntity.class));
    }
    
    @Test
    void save_ShouldThrowDataIntegrityViolationException_WhenDatabaseConflictOccurs() {
        // Arrange
        UUID id = UUID.randomUUID();
        User user = createTestUser(id);
        
        when(userJpaRepository.existsById(id)).thenReturn(false);
        when(userJpaRepository.existsByUsername(user.getUsername().getValue())).thenReturn(false);
        when(userJpaRepository.existsByEmail(user.getEmail().getValue())).thenReturn(false);
        when(userJpaRepository.save(any(UserJpaEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Database conflict"));
        
        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> adapter.save(user));
    }
    
    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserJpaEntity entity = createTestUserEntity(id);
        
        when(userJpaRepository.findById(id)).thenReturn(Optional.of(entity));
        
        // Act
        Optional<User> result = adapter.findById(UserId.of(id));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId().getValue());
        verify(userJpaRepository).findById(id);
    }
    
    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        
        when(userJpaRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = adapter.findById(UserId.of(id));
        
        // Assert
        assertFalse(result.isPresent());
        verify(userJpaRepository).findById(id);
    }
    
    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserJpaEntity entity = createTestUserEntity(id);
        String username = "testuser";
        
        when(userJpaRepository.findByUsername(username)).thenReturn(Optional.of(entity));
        
        // Act
        Optional<User> result = adapter.findByUsername(new Username(username));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId().getValue());
        verify(userJpaRepository).findByUsername(username);
    }
    
    @Test
    void findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistent";
        
        when(userJpaRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = adapter.findByUsername(new Username(username));
        
        // Assert
        assertFalse(result.isPresent());
        verify(userJpaRepository).findByUsername(username);
    }
    
    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        UserJpaEntity entity = createTestUserEntity(id);
        String email = "test@example.com";
        
        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        
        // Act
        Optional<User> result = adapter.findByEmail(new Email(email));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId().getValue());
        verify(userJpaRepository).findByEmail(email);
    }
    
    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        
        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // Act
        Optional<User> result = adapter.findByEmail(new Email(email));
        
        // Assert
        assertFalse(result.isPresent());
        verify(userJpaRepository).findByEmail(email);
    }
    
    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // Arrange
        String username = "testuser";
        
        when(userJpaRepository.existsByUsername(username)).thenReturn(true);
        
        // Act
        boolean result = adapter.existsByUsername(new Username(username));
        
        // Assert
        assertTrue(result);
        verify(userJpaRepository).existsByUsername(username);
    }
    
    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        // Arrange
        String username = "nonexistent";
        
        when(userJpaRepository.existsByUsername(username)).thenReturn(false);
        
        // Act
        boolean result = adapter.existsByUsername(new Username(username));
        
        // Assert
        assertFalse(result);
        verify(userJpaRepository).existsByUsername(username);
    }
    
    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        String email = "test@example.com";
        
        when(userJpaRepository.existsByEmail(email)).thenReturn(true);
        
        // Act
        boolean result = adapter.existsByEmail(new Email(email));
        
        // Assert
        assertTrue(result);
        verify(userJpaRepository).existsByEmail(email);
    }
    
    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";
        
        when(userJpaRepository.existsByEmail(email)).thenReturn(false);
        
        // Act
        boolean result = adapter.existsByEmail(new Email(email));
        
        // Assert
        assertFalse(result);
        verify(userJpaRepository).existsByEmail(email);
    }

    private User createTestUser(UUID id) {
        return User.reconstitute(
                UserId.of(id),
                new Username("testuser"),
                new Email("test@example.com"),
                "hashedpassword",
                User.Role.USER,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private UserJpaEntity createTestUserEntity(UUID id) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(id);
        entity.setUsername("testuser");
        entity.setEmail("test@example.com");
        entity.setPassword("hashedpassword");
        entity.setRole("USER");
        entity.setEnabled(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
} 