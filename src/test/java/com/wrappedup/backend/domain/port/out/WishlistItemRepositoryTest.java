package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WishlistItemRepositoryTest {

    @Test
    @DisplayName("Should save a wishlist item")
    void save_ShouldReturnSavedItem() {
        // Create a mock implementation of the interface
        WishlistItemRepository repository = Mockito.mock(WishlistItemRepository.class);
        
        // Create a test wishlist item
        WishlistItem wishlistItem = createMockWishlistItem();
        
        // Mock the save behavior
        when(repository.save(wishlistItem)).thenReturn(wishlistItem);
        
        // Call the method
        WishlistItem result = repository.save(wishlistItem);
        
        // Verify the method was called with the correct item
        verify(repository).save(wishlistItem);
        
        // Verify the result
        assertEquals(wishlistItem, result);
    }
    
    @Test
    @DisplayName("Should find a wishlist item by ID")
    void findById_ShouldReturnItemWhenFound() {
        // Create a mock implementation of the interface
        WishlistItemRepository repository = Mockito.mock(WishlistItemRepository.class);
        
        // Create a test wishlist item and ID
        WishlistItemId itemId = WishlistItemId.fromUUID(UUID.randomUUID());
        WishlistItem wishlistItem = createMockWishlistItem();
        
        // Mock the findById behavior
        when(repository.findById(itemId)).thenReturn(Optional.of(wishlistItem));
        
        // Call the method
        Optional<WishlistItem> result = repository.findById(itemId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(itemId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(wishlistItem, result.get());
    }
    
    @Test
    @DisplayName("Should return empty when wishlist item not found by ID")
    void findById_ShouldReturnEmptyWhenNotFound() {
        // Create a mock implementation of the interface
        WishlistItemRepository repository = Mockito.mock(WishlistItemRepository.class);
        
        // Create a test ID
        WishlistItemId itemId = WishlistItemId.fromUUID(UUID.randomUUID());
        
        // Mock the findById behavior
        when(repository.findById(itemId)).thenReturn(Optional.empty());
        
        // Call the method
        Optional<WishlistItem> result = repository.findById(itemId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(itemId);
        
        // Verify the result
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should find all wishlist items for a user")
    void findAllByUserId_ShouldReturnUserItems() {
        // Create a mock implementation of the interface
        WishlistItemRepository repository = Mockito.mock(WishlistItemRepository.class);
        
        // Create test data
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        WishlistItem item1 = createMockWishlistItem();
        WishlistItem item2 = createMockWishlistItem();
        List<WishlistItem> expectedItems = Arrays.asList(item1, item2);
        
        // Mock the findAllByUserId behavior
        when(repository.findAllByUserId(userId)).thenReturn(expectedItems);
        
        // Call the method
        List<WishlistItem> result = repository.findAllByUserId(userId);
        
        // Verify the method was called with the correct user ID
        verify(repository).findAllByUserId(userId);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(expectedItems, result);
    }
    
    @Test
    @DisplayName("Should find a wishlist item by user ID and book ID")
    void findByUserIdAndBookId_ShouldReturnItemWhenFound() {
        // Create a mock implementation of the interface
        WishlistItemRepository repository = Mockito.mock(WishlistItemRepository.class);
        
        // Create test data
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        WishlistItem wishlistItem = createMockWishlistItem();
        
        // Mock the findByUserIdAndBookId behavior
        when(repository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(wishlistItem));
        
        // Call the method
        Optional<WishlistItem> result = repository.findByUserIdAndBookId(userId, bookId);
        
        // Verify the method was called with the correct IDs
        verify(repository).findByUserIdAndBookId(userId, bookId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(wishlistItem, result.get());
    }
    
    @Test
    @DisplayName("Should delete a wishlist item by ID")
    void deleteById_ShouldCallRepositoryWithCorrectId() {
        // Create a mock implementation of the interface
        WishlistItemRepository repository = Mockito.mock(WishlistItemRepository.class);
        
        // Create a test ID
        WishlistItemId itemId = WishlistItemId.fromUUID(UUID.randomUUID());
        
        // Call the method
        repository.deleteById(itemId);
        
        // Verify the method was called with the correct ID
        verify(repository).deleteById(itemId);
    }
    
    @Test
    @DisplayName("Should check if wishlist item exists by user ID and book ID")
    void existsByUserIdAndBookId_ShouldReturnTrueWhenExists() {
        // Create a mock implementation of the interface
        WishlistItemRepository repository = Mockito.mock(WishlistItemRepository.class);
        
        // Create test data
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock the existsByUserIdAndBookId behavior
        when(repository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);
        
        // Call the method
        boolean result = repository.existsByUserIdAndBookId(userId, bookId);
        
        // Verify the method was called with the correct IDs
        verify(repository).existsByUserIdAndBookId(userId, bookId);
        
        // Verify the result
        assertTrue(result);
    }
    
    private WishlistItem createMockWishlistItem() {
        LocalDateTime now = LocalDateTime.now();
        return WishlistItem.reconstitute(
                WishlistItemId.fromUUID(UUID.randomUUID()),
                UserId.fromUUID(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()),
                "This is my wishlist note",
                2,
                true,
                now,
                now
        );
    }
} 