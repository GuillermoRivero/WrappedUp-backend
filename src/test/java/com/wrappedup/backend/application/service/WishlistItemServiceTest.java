package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.*;
import com.wrappedup.backend.domain.port.in.CreateWishlistItemUseCase.CreateWishlistItemCommand;
import com.wrappedup.backend.domain.port.in.UpdateWishlistItemUseCase.UpdateWishlistItemCommand;
import com.wrappedup.backend.domain.port.out.UserRepository;
import com.wrappedup.backend.domain.port.out.WishlistItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistItemServiceTest {

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WishlistItemService wishlistItemService;

    @Captor
    private ArgumentCaptor<WishlistItem> wishlistItemCaptor;

    private WishlistItemId wishlistItemId;
    private UserId userId;
    private BookId bookId;
    private WishlistItem existingWishlistItem;
    private CreateWishlistItemCommand createCommand;
    private UpdateWishlistItemCommand updateCommand;
    private User user;
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        wishlistItemId = WishlistItemId.generate();
        userId = UserId.generate();
        bookId = BookId.generate();

        // Create existing wishlist item
        existingWishlistItem = WishlistItem.createNewWishlistItem(
                userId,
                bookId,
                "Original description",
                2,
                true
        );
        
        // Use reflection to set the ID since it's normally set by the repository
        ReflectionTestUtils.setField(existingWishlistItem, "id", wishlistItemId);

        // Create commands
        createCommand = new CreateWishlistItemCommand(
                userId,
                bookId,
                "Test description",
                3,
                true
        );

        updateCommand = new UpdateWishlistItemCommand(
                wishlistItemId,
                "Updated description",
                1,
                false
        );

        // Create user
        user = User.createNewUser(
                username,
                "test@example.com",
                "password123"
        );
        ReflectionTestUtils.setField(user, "id", userId);
    }

    @Test
    @DisplayName("Should create a new wishlist item")
    void createWishlistItem_WithValidCommand_ShouldCreateWishlistItem() {
        // Arrange
        when(wishlistItemRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> {
            WishlistItem saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", wishlistItemId);
            return saved;
        });

        // Act
        WishlistItemId resultId = wishlistItemService.createWishlistItem(createCommand);

        // Assert
        verify(wishlistItemRepository).existsByUserIdAndBookId(userId, bookId);
        verify(wishlistItemRepository).save(wishlistItemCaptor.capture());

        WishlistItem capturedItem = wishlistItemCaptor.getValue();
        assertEquals(wishlistItemId, resultId);
        assertEquals(userId, capturedItem.getUserId());
        assertEquals(bookId, capturedItem.getBookId());
        assertEquals("Test description", capturedItem.getDescription());
        assertEquals(3, capturedItem.getPriority());
        assertTrue(capturedItem.isPublic());
    }

    @Test
    @DisplayName("Should throw exception when wishlist item already exists")
    void createWishlistItem_WhenItemExists_ShouldThrowException() {
        // Arrange
        when(wishlistItemRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> wishlistItemService.createWishlistItem(createCommand)
        );

        verify(wishlistItemRepository).existsByUserIdAndBookId(userId, bookId);
        verify(wishlistItemRepository, never()).save(any());
        
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Should get wishlist item by ID")
    void getWishlistItemById_WithExistingId_ShouldReturnWishlistItem() {
        // Arrange
        when(wishlistItemRepository.findById(wishlistItemId)).thenReturn(Optional.of(existingWishlistItem));

        // Act
        Optional<WishlistItem> result = wishlistItemService.getWishlistItemById(wishlistItemId);

        // Assert
        verify(wishlistItemRepository).findById(wishlistItemId);
        assertTrue(result.isPresent());
        assertEquals(existingWishlistItem, result.get());
    }

    @Test
    @DisplayName("Should return empty optional when wishlist item not found by ID")
    void getWishlistItemById_WithNonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(wishlistItemRepository.findById(wishlistItemId)).thenReturn(Optional.empty());

        // Act
        Optional<WishlistItem> result = wishlistItemService.getWishlistItemById(wishlistItemId);

        // Assert
        verify(wishlistItemRepository).findById(wishlistItemId);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should get all wishlist items by user ID")
    void getWishlistItemsByUserId_ShouldReturnUserItems() {
        // Arrange
        List<WishlistItem> items = Arrays.asList(
                existingWishlistItem,
                WishlistItem.createNewWishlistItem(userId, BookId.generate(), "Another book", 1, false)
        );
        when(wishlistItemRepository.findAllByUserId(userId)).thenReturn(items);

        // Act
        List<WishlistItem> result = wishlistItemService.getWishlistItemsByUserId(userId);

        // Assert
        verify(wishlistItemRepository).findAllByUserId(userId);
        assertEquals(2, result.size());
        assertEquals(items, result);
    }

    @Test
    @DisplayName("Should get public wishlist items by username")
    void getPublicWishlistItemsByUsername_ShouldReturnPublicItems() {
        // Arrange
        WishlistItem publicItem = existingWishlistItem; // This is public
        WishlistItem privateItem = WishlistItem.createNewWishlistItem(userId, BookId.generate(), "Private item", 3, false);
        List<WishlistItem> allItems = Arrays.asList(publicItem, privateItem);
        
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.of(user));
        when(wishlistItemRepository.findAllByUserId(userId)).thenReturn(allItems);

        // Act
        List<WishlistItem> result = wishlistItemService.getPublicWishlistItemsByUsername(username);

        // Assert
        verify(userRepository).findByUsername(any(Username.class));
        verify(wishlistItemRepository).findAllByUserId(userId);
        assertEquals(1, result.size());
        assertTrue(result.contains(publicItem));
        assertFalse(result.contains(privateItem));
    }

    @Test
    @DisplayName("Should throw exception when username not found")
    void getPublicWishlistItemsByUsername_WithNonExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.findByUsername(any(Username.class))).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wishlistItemService.getPublicWishlistItemsByUsername(username)
        );

        verify(userRepository).findByUsername(any(Username.class));
        verify(wishlistItemRepository, never()).findAllByUserId(any());
        
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should update wishlist item with new values")
    void updateWishlistItem_WithValidCommand_ShouldUpdateItem() {
        // Arrange
        when(wishlistItemRepository.findById(wishlistItemId)).thenReturn(Optional.of(existingWishlistItem));
        when(wishlistItemRepository.save(any(WishlistItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Verify initial state
        assertEquals("Original description", existingWishlistItem.getDescription());
        assertEquals(2, existingWishlistItem.getPriority());
        assertTrue(existingWishlistItem.isPublic());

        // Act
        WishlistItem updatedItem = wishlistItemService.updateWishlistItem(updateCommand);

        // Assert
        verify(wishlistItemRepository).findById(wishlistItemId);
        verify(wishlistItemRepository).save(wishlistItemCaptor.capture());

        WishlistItem capturedItem = wishlistItemCaptor.getValue();
        assertNotNull(updatedItem);
        assertEquals("Updated description", updatedItem.getDescription());
        assertEquals(1, updatedItem.getPriority());
        assertFalse(updatedItem.isPublic());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent wishlist item")
    void updateWishlistItem_WithNonExistingItem_ShouldThrowException() {
        // Arrange
        when(wishlistItemRepository.findById(wishlistItemId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wishlistItemService.updateWishlistItem(updateCommand)
        );

        verify(wishlistItemRepository).findById(wishlistItemId);
        verify(wishlistItemRepository, never()).save(any());
        
        assertTrue(exception.getMessage().contains("Wishlist item not found"));
    }

    @Test
    @DisplayName("Should delete wishlist item when it exists")
    void deleteWishlistItem_WhenItemExists_ShouldDeleteItem() {
        // Arrange
        when(wishlistItemRepository.findById(wishlistItemId)).thenReturn(Optional.of(existingWishlistItem));
        doNothing().when(wishlistItemRepository).deleteById(wishlistItemId);

        // Act
        wishlistItemService.deleteWishlistItem(wishlistItemId);

        // Assert
        verify(wishlistItemRepository).findById(wishlistItemId);
        verify(wishlistItemRepository).deleteById(wishlistItemId);
    }

    @Test
    @DisplayName("Should not delete wishlist item when it doesn't exist")
    void deleteWishlistItem_WhenItemDoesNotExist_ShouldDoNothing() {
        // Arrange
        when(wishlistItemRepository.findById(wishlistItemId)).thenReturn(Optional.empty());

        // Act
        wishlistItemService.deleteWishlistItem(wishlistItemId);

        // Assert
        verify(wishlistItemRepository).findById(wishlistItemId);
        verify(wishlistItemRepository, never()).deleteById(any(WishlistItemId.class));
    }

    @Test
    @DisplayName("Should check if wishlist item exists by user ID and book ID")
    void existsByUserIdAndBookId_ShouldReturnCorrectResult() {
        // Arrange
        when(wishlistItemRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);

        // Act
        boolean result = wishlistItemService.existsByUserIdAndBookId(userId, bookId);

        // Assert
        verify(wishlistItemRepository).existsByUserIdAndBookId(userId, bookId);
        assertTrue(result);
    }

    @Test
    @DisplayName("Should get wishlist item by user ID and book ID")
    void getWishlistItemByUserIdAndBookId_ShouldReturnItem() {
        // Arrange
        when(wishlistItemRepository.findByUserIdAndBookId(userId, bookId))
            .thenReturn(Optional.of(existingWishlistItem));

        // Act
        Optional<WishlistItem> result = wishlistItemService.getWishlistItemByUserIdAndBookId(userId, bookId);

        // Assert
        verify(wishlistItemRepository).findByUserIdAndBookId(userId, bookId);
        assertTrue(result.isPresent());
        assertEquals(existingWishlistItem, result.get());
    }
} 