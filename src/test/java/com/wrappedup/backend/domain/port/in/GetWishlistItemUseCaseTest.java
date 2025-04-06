package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetWishlistItemUseCaseTest {

    @Test
    @DisplayName("Should call getWishlistItemById with the provided WishlistItemId")
    void getWishlistItemById_ShouldCallWithProvidedWishlistItemId() {
        // Create a mock implementation of the interface
        GetWishlistItemUseCase useCase = Mockito.mock(GetWishlistItemUseCase.class);
        
        // Create a test WishlistItemId
        WishlistItemId wishlistItemId = WishlistItemId.fromUUID(UUID.randomUUID());
        
        // Mock a wishlist item response
        WishlistItem mockWishlistItem = Mockito.mock(WishlistItem.class);
        when(useCase.getWishlistItemById(wishlistItemId)).thenReturn(Optional.of(mockWishlistItem));
        
        // Call the method
        Optional<WishlistItem> result = useCase.getWishlistItemById(wishlistItemId);
        
        // Verify the method was called with the correct WishlistItemId
        verify(useCase).getWishlistItemById(wishlistItemId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockWishlistItem, result.get());
    }
    
    @Test
    @DisplayName("Should call getWishlistItemsByUserId with the provided UserId")
    void getWishlistItemsByUserId_ShouldCallWithProvidedUserId() {
        // Create a mock implementation of the interface
        GetWishlistItemUseCase useCase = Mockito.mock(GetWishlistItemUseCase.class);
        
        // Create a test UserId
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        
        // Mock wishlist items response
        WishlistItem mockWishlistItem1 = Mockito.mock(WishlistItem.class);
        WishlistItem mockWishlistItem2 = Mockito.mock(WishlistItem.class);
        List<WishlistItem> mockWishlistItems = Arrays.asList(mockWishlistItem1, mockWishlistItem2);
        when(useCase.getWishlistItemsByUserId(userId)).thenReturn(mockWishlistItems);
        
        // Call the method
        List<WishlistItem> result = useCase.getWishlistItemsByUserId(userId);
        
        // Verify the method was called with the correct UserId
        verify(useCase).getWishlistItemsByUserId(userId);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(mockWishlistItems, result);
    }
    
    @Test
    @DisplayName("Should call getPublicWishlistItemsByUsername with the provided username")
    void getPublicWishlistItemsByUsername_ShouldCallWithProvidedUsername() {
        // Create a mock implementation of the interface
        GetWishlistItemUseCase useCase = Mockito.mock(GetWishlistItemUseCase.class);
        
        // Create a test username
        String username = "testuser";
        
        // Mock wishlist items response
        WishlistItem mockWishlistItem1 = Mockito.mock(WishlistItem.class);
        WishlistItem mockWishlistItem2 = Mockito.mock(WishlistItem.class);
        WishlistItem mockWishlistItem3 = Mockito.mock(WishlistItem.class);
        List<WishlistItem> mockWishlistItems = Arrays.asList(mockWishlistItem1, mockWishlistItem2, mockWishlistItem3);
        when(useCase.getPublicWishlistItemsByUsername(username)).thenReturn(mockWishlistItems);
        
        // Call the method
        List<WishlistItem> result = useCase.getPublicWishlistItemsByUsername(username);
        
        // Verify the method was called with the correct username
        verify(useCase).getPublicWishlistItemsByUsername(username);
        
        // Verify the result
        assertEquals(3, result.size());
        assertEquals(mockWishlistItems, result);
    }
    
    @Test
    @DisplayName("Should call getWishlistItemByUserIdAndBookId with the provided UserId and BookId")
    void getWishlistItemByUserIdAndBookId_ShouldCallWithProvidedUserIdAndBookId() {
        // Create a mock implementation of the interface
        GetWishlistItemUseCase useCase = Mockito.mock(GetWishlistItemUseCase.class);
        
        // Create test IDs
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock a wishlist item response
        WishlistItem mockWishlistItem = Mockito.mock(WishlistItem.class);
        when(useCase.getWishlistItemByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(mockWishlistItem));
        
        // Call the method
        Optional<WishlistItem> result = useCase.getWishlistItemByUserIdAndBookId(userId, bookId);
        
        // Verify the method was called with the correct UserId and BookId
        verify(useCase).getWishlistItemByUserIdAndBookId(userId, bookId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockWishlistItem, result.get());
    }
    
    @Test
    @DisplayName("Should call existsByUserIdAndBookId with the provided UserId and BookId")
    void existsByUserIdAndBookId_ShouldCallWithProvidedUserIdAndBookId() {
        // Create a mock implementation of the interface
        GetWishlistItemUseCase useCase = Mockito.mock(GetWishlistItemUseCase.class);
        
        // Create test IDs
        UserId userId = UserId.fromUUID(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock the existence check response
        when(useCase.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);
        
        // Call the method
        boolean result = useCase.existsByUserIdAndBookId(userId, bookId);
        
        // Verify the method was called with the correct UserId and BookId
        verify(useCase).existsByUserIdAndBookId(userId, bookId);
        
        // Verify the result
        assertTrue(result);
    }
} 