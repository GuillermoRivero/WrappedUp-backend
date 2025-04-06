package com.wrappedup.backend.infrastructure.adapter.persistence;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.WishlistItemJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.repository.WishlistItemJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaWishlistItemRepositoryAdapterTest {

    @Mock
    private WishlistItemJpaRepository jpaRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private JpaWishlistItemRepositoryAdapter adapter;

    private UUID wishlistItemId;
    private UUID userId;
    private UUID bookId;
    private WishlistItem testWishlistItem;
    private WishlistItemJpaEntity testJpaEntity;

    @BeforeEach
    void setUp() {
        wishlistItemId = UUID.randomUUID();
        userId = UUID.randomUUID();
        bookId = UUID.randomUUID();
        
        testWishlistItem = WishlistItem.reconstitute(
                WishlistItemId.fromUUID(wishlistItemId),
                UserId.fromUUID(userId),
                BookId.fromUUID(bookId),
                "Test description",
                3,
                true,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now()
        );
        
        testJpaEntity = WishlistItemJpaEntity.builder()
                .id(wishlistItemId)
                .userId(userId)
                .bookId(bookId)
                .description("Test description")
                .priority(3)
                .isPublic(true)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Set the entityManager using ReflectionTestUtils to work around @PersistenceContext
        ReflectionTestUtils.setField(adapter, "entityManager", entityManager);
    }

    @Test
    void save_ShouldReturnSavedItem_WhenSavingNewItem() {
        // Arrange
        when(jpaRepository.existsById(wishlistItemId)).thenReturn(false);
        when(jpaRepository.save(any(WishlistItemJpaEntity.class))).thenReturn(testJpaEntity);
        
        // Act
        WishlistItem result = adapter.save(testWishlistItem);
        
        // Assert
        assertNotNull(result);
        assertEquals(wishlistItemId, result.getId().getValue());
        assertEquals(userId, result.getUserId().getValue());
        assertEquals(bookId, result.getBookId().getValue());
        assertEquals(testWishlistItem.getDescription(), result.getDescription());
        assertEquals(testWishlistItem.getPriority(), result.getPriority());
        assertEquals(testWishlistItem.isPublic(), result.isPublic());
        verify(jpaRepository).save(any(WishlistItemJpaEntity.class));
        verify(entityManager, never()).merge(any());
        verify(entityManager, never()).flush();
    }
    
    @Test
    void save_ShouldReturnSavedItem_WhenUpdatingExistingItem() {
        // Arrange
        when(jpaRepository.existsById(wishlistItemId)).thenReturn(true);
        when(entityManager.merge(any(WishlistItemJpaEntity.class))).thenReturn(testJpaEntity);
        
        // Act
        WishlistItem result = adapter.save(testWishlistItem);
        
        // Assert
        assertNotNull(result);
        assertEquals(wishlistItemId, result.getId().getValue());
        assertEquals(userId, result.getUserId().getValue());
        assertEquals(bookId, result.getBookId().getValue());
        assertEquals(testWishlistItem.getDescription(), result.getDescription());
        assertEquals(testWishlistItem.getPriority(), result.getPriority());
        assertEquals(testWishlistItem.isPublic(), result.isPublic());
        verify(jpaRepository, never()).save(any(WishlistItemJpaEntity.class));
        verify(entityManager).merge(any(WishlistItemJpaEntity.class));
        verify(entityManager).flush();
    }

    @Test
    void findById_ShouldReturnItem_WhenItemExists() {
        // Arrange
        when(jpaRepository.findById(wishlistItemId)).thenReturn(Optional.of(testJpaEntity));
        
        // Act
        Optional<WishlistItem> result = adapter.findById(WishlistItemId.fromUUID(wishlistItemId));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(wishlistItemId, result.get().getId().getValue());
        assertEquals(userId, result.get().getUserId().getValue());
        assertEquals(bookId, result.get().getBookId().getValue());
        verify(jpaRepository).findById(wishlistItemId);
    }
    
    @Test
    void findById_ShouldReturnEmpty_WhenItemDoesNotExist() {
        // Arrange
        when(jpaRepository.findById(wishlistItemId)).thenReturn(Optional.empty());
        
        // Act
        Optional<WishlistItem> result = adapter.findById(WishlistItemId.fromUUID(wishlistItemId));
        
        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository).findById(wishlistItemId);
    }
    
    @Test
    void findAllByUserId_ShouldReturnItems_WhenUserHasItems() {
        // Arrange
        List<WishlistItemJpaEntity> entities = Arrays.asList(
                testJpaEntity,
                WishlistItemJpaEntity.builder()
                        .id(UUID.randomUUID())
                        .userId(userId)
                        .bookId(UUID.randomUUID())
                        .description("Another book")
                        .priority(2)
                        .isPublic(false)
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        
        when(jpaRepository.findAllByUserId(userId)).thenReturn(entities);
        
        // Act
        List<WishlistItem> result = adapter.findAllByUserId(UserId.fromUUID(userId));
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getUserId().getValue());
        assertEquals(userId, result.get(1).getUserId().getValue());
        verify(jpaRepository).findAllByUserId(userId);
    }
    
    @Test
    void findAllByUserId_ShouldReturnEmptyList_WhenUserHasNoItems() {
        // Arrange
        when(jpaRepository.findAllByUserId(userId)).thenReturn(List.of());
        
        // Act
        List<WishlistItem> result = adapter.findAllByUserId(UserId.fromUUID(userId));
        
        // Assert
        assertTrue(result.isEmpty());
        verify(jpaRepository).findAllByUserId(userId);
    }
    
    @Test
    void findByUserIdAndBookId_ShouldReturnItem_WhenItemExists() {
        // Arrange
        when(jpaRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(testJpaEntity));
        
        // Act
        Optional<WishlistItem> result = adapter.findByUserIdAndBookId(
                UserId.fromUUID(userId),
                BookId.fromUUID(bookId)
        );
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(wishlistItemId, result.get().getId().getValue());
        assertEquals(userId, result.get().getUserId().getValue());
        assertEquals(bookId, result.get().getBookId().getValue());
        verify(jpaRepository).findByUserIdAndBookId(userId, bookId);
    }
    
    @Test
    void findByUserIdAndBookId_ShouldReturnEmpty_WhenItemDoesNotExist() {
        // Arrange
        when(jpaRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());
        
        // Act
        Optional<WishlistItem> result = adapter.findByUserIdAndBookId(
                UserId.fromUUID(userId),
                BookId.fromUUID(bookId)
        );
        
        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository).findByUserIdAndBookId(userId, bookId);
    }
    
    @Test
    void deleteById_ShouldDeleteItem_WhenItemExists() {
        // Arrange
        when(jpaRepository.existsById(wishlistItemId)).thenReturn(true);
        
        // Act
        adapter.deleteById(WishlistItemId.fromUUID(wishlistItemId));
        
        // Assert
        verify(jpaRepository).deleteById(wishlistItemId);
    }
    
    @Test
    void deleteById_ShouldNotThrowException_WhenItemDoesNotExist() {
        // Arrange
        when(jpaRepository.existsById(wishlistItemId)).thenReturn(false);
        
        // Act & Assert
        assertDoesNotThrow(() -> adapter.deleteById(WishlistItemId.fromUUID(wishlistItemId)));
        verify(jpaRepository, never()).deleteById(wishlistItemId);
    }
    
    @Test
    void deleteById_ShouldPropagateException_WhenExceptionIsThrown() {
        // Arrange
        when(jpaRepository.existsById(wishlistItemId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(jpaRepository).deleteById(wishlistItemId);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> adapter.deleteById(WishlistItemId.fromUUID(wishlistItemId)));
    }
    
    @Test
    void existsByUserIdAndBookId_ShouldReturnTrue_WhenItemExists() {
        // Arrange
        when(jpaRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);
        
        // Act
        boolean result = adapter.existsByUserIdAndBookId(
                UserId.fromUUID(userId),
                BookId.fromUUID(bookId)
        );
        
        // Assert
        assertTrue(result);
        verify(jpaRepository).existsByUserIdAndBookId(userId, bookId);
    }
    
    @Test
    void existsByUserIdAndBookId_ShouldReturnFalse_WhenItemDoesNotExist() {
        // Arrange
        when(jpaRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);
        
        // Act
        boolean result = adapter.existsByUserIdAndBookId(
                UserId.fromUUID(userId),
                BookId.fromUUID(bookId)
        );
        
        // Assert
        assertFalse(result);
        verify(jpaRepository).existsByUserIdAndBookId(userId, bookId);
    }
} 