package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.WishlistItem;
import com.wrappedup.backend.domain.exception.WishlistOperationException;
import com.wrappedup.backend.domain.port.BookRepository;
import com.wrappedup.backend.domain.port.UserRepository;
import com.wrappedup.backend.domain.port.WishlistRepository;
import com.wrappedup.backend.infrastructure.controller.WishlistRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WishlistServiceTest {

    private static final UUID TEST_USER_ID = UUID.fromString("f2295dea-c8cb-4493-ae1c-dbfebbfc4f3a");
    private static final UUID TEST_BOOK_ID = UUID.fromString("a1234567-e89b-12d3-a456-426614174000");
    private static final UUID TEST_WISHLIST_ITEM_ID = UUID.fromString("b9876543-e21a-45c6-789d-012345678901");
    private static final String TEST_OPEN_LIBRARY_KEY = "/works/OL1234567W";

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookService bookService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WishlistService wishlistService;

    private User testUser;
    private Book testBook;
    private WishlistItem testWishlistItem;

    @BeforeEach
    void setUp() {
        // Setup test objects
        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId(TEST_USER_ID);

        testBook = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", TEST_OPEN_LIBRARY_KEY);
        testBook.setId(TEST_BOOK_ID);

        testWishlistItem = new WishlistItem(testUser, testBook, "Test description", 3, true);
        testWishlistItem.setId(TEST_WISHLIST_ITEM_ID);
        testWishlistItem.setCreatedAt(LocalDateTime.now());
        
        // Inyectar el mock de EntityManager en el servicio
        ReflectionTestUtils.setField(wishlistService, "entityManager", entityManager);
        
        // Configurar el repositorio para devolver el libro por ID o OpenLibrary key
        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(testBook));
        when(bookRepository.findByOpenLibraryKey(TEST_OPEN_LIBRARY_KEY)).thenReturn(Optional.of(testBook));
        
        // Configurar BookService para devolver el libro cuando se busca por clave
        when(bookService.findAndPersistBookByKey(anyString())).thenReturn(testBook);
    }

    @Test
    void shouldAddToWishlist() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(testBook));
        when(wishlistRepository.existsByUserAndBook(testUser, testBook)).thenReturn(false);
        when(wishlistRepository.save(any(WishlistItem.class))).thenReturn(testWishlistItem);

        // When
        WishlistItem result = wishlistService.addToWishlist(
                TEST_USER_ID, TEST_BOOK_ID, "Test description", 3, true);

        // Then
        assertNotNull(result);
        assertEquals(TEST_WISHLIST_ITEM_ID, result.getId());
        assertEquals("Test description", result.getDescription());
        assertEquals(3, result.getPriority());
        assertTrue(result.isPublic());
        verify(wishlistRepository).save(any(WishlistItem.class));
    }

    @Test
    void shouldThrowExceptionWhenAddingExistingBookToWishlist() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(TEST_BOOK_ID)).thenReturn(Optional.of(testBook));
        when(wishlistRepository.existsByUserAndBook(testUser, testBook)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> wishlistService.addToWishlist(TEST_USER_ID, TEST_BOOK_ID, null, null, null)
        );
        assertEquals("Book already in wishlist", exception.getMessage());
        verify(wishlistRepository, never()).save(any(WishlistItem.class));
    }

    @Test
    void shouldAddToWishlistByOpenLibraryKey() {
        // Given
        WishlistRequest request = new WishlistRequest();
        request.setOpenLibraryKey(TEST_OPEN_LIBRARY_KEY);
        request.setDescription("Test description");
        request.setPriority(4);
        request.setIsPublic(true);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(bookService.findAndPersistBookByKey(anyString())).thenReturn(testBook);
        when(wishlistRepository.findByUserIdAndBookId(TEST_USER_ID, TEST_BOOK_ID)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(WishlistItem.class))).thenReturn(testWishlistItem);

        // When
        WishlistItem result = wishlistService.addToWishlistByOpenLibraryKey(TEST_USER_ID, request);

        // Then
        assertNotNull(result);
        assertEquals(TEST_WISHLIST_ITEM_ID, result.getId());
        assertEquals("Test description", result.getDescription());
        assertTrue(result.isPublic());
        verify(bookService).findAndPersistBookByKey(contains(TEST_OPEN_LIBRARY_KEY));
        verify(wishlistRepository).save(any(WishlistItem.class));
    }

    @Test
    void shouldReturnExistingItemWhenAddingDuplicateByOpenLibraryKey() {
        // Given
        WishlistRequest request = new WishlistRequest();
        request.setOpenLibraryKey(TEST_OPEN_LIBRARY_KEY);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(bookService.findAndPersistBookByKey(anyString())).thenReturn(testBook);
        when(wishlistRepository.findByUserIdAndBookId(TEST_USER_ID, TEST_BOOK_ID))
                .thenReturn(Optional.of(testWishlistItem));

        // When
        WishlistItem result = wishlistService.addToWishlistByOpenLibraryKey(TEST_USER_ID, request);

        // Then
        assertNotNull(result);
        assertEquals(TEST_WISHLIST_ITEM_ID, result.getId());
        verify(wishlistRepository, never()).save(any(WishlistItem.class));
    }

    @Test
    void shouldGetUserWishlist() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(wishlistRepository.findAllByUser(testUser))
                .thenReturn(List.of(testWishlistItem));

        // When
        List<WishlistItem> result = wishlistService.getUserWishlist(TEST_USER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_WISHLIST_ITEM_ID, result.get(0).getId());
        verify(wishlistRepository).findAllByUser(testUser);
    }

    @Test
    void shouldGetPublicWishlistByUserId() {
        // Given
        WishlistItem publicItem = new WishlistItem(testUser, testBook);
        publicItem.setPublic(true);
        
        WishlistItem privateItem = new WishlistItem(testUser, testBook);
        privateItem.setPublic(false);
        
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(wishlistRepository.findAllByUser(testUser))
                .thenReturn(Arrays.asList(publicItem, privateItem));

        // When
        List<WishlistItem> result = wishlistService.getPublicWishlistByUserId(TEST_USER_ID);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isPublic());
        verify(wishlistRepository).findAllByUser(testUser);
    }

    @Test
    void shouldGetWishlistItem() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(wishlistRepository.findById(TEST_WISHLIST_ITEM_ID))
                .thenReturn(Optional.of(testWishlistItem));

        // When
        Optional<WishlistItem> result = wishlistService.getWishlistItem(TEST_USER_ID, TEST_WISHLIST_ITEM_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(TEST_WISHLIST_ITEM_ID, result.get().getId());
    }

    @Test
    void shouldNotGetWishlistItemFromAnotherUser() {
        // Given
        User otherUser = new User("otheruser", "other@example.com", "password");
        UUID otherUserId = UUID.randomUUID();
        otherUser.setId(otherUserId);
        
        // Crear un item que pertenece a otro usuario
        WishlistItem otherUsersItem = new WishlistItem(otherUser, testBook, "Other user's item", 3, true);
        otherUsersItem.setId(TEST_WISHLIST_ITEM_ID);
        
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(wishlistRepository.findById(TEST_WISHLIST_ITEM_ID))
                .thenReturn(Optional.of(otherUsersItem));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wishlistService.getWishlistItem(TEST_USER_ID, TEST_WISHLIST_ITEM_ID)
        );
        assertEquals("Wishlist item does not belong to user", exception.getMessage());
    }

    @Test
    void shouldUpdateWishlistItem() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(wishlistRepository.findById(TEST_WISHLIST_ITEM_ID))
                .thenReturn(Optional.of(testWishlistItem));
        when(wishlistRepository.save(testWishlistItem)).thenReturn(testWishlistItem);

        // When
        WishlistItem result = wishlistService.updateWishlistItem(
                TEST_USER_ID, TEST_WISHLIST_ITEM_ID, "Updated description", 5, true);

        // Then
        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        assertEquals(5, result.getPriority());
        assertTrue(result.isPublic());
        verify(wishlistRepository).save(testWishlistItem);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentItem() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(wishlistRepository.findById(TEST_WISHLIST_ITEM_ID))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wishlistService.updateWishlistItem(TEST_USER_ID, TEST_WISHLIST_ITEM_ID, null, null, null)
        );
        assertEquals("Wishlist item not found", exception.getMessage());
        verify(wishlistRepository, never()).save(any(WishlistItem.class));
    }

    @Test
    void shouldRemoveFromWishlist() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(wishlistRepository.findById(TEST_WISHLIST_ITEM_ID))
                .thenReturn(Optional.of(testWishlistItem));

        // When
        wishlistService.removeFromWishlist(TEST_USER_ID, TEST_WISHLIST_ITEM_ID);

        // Then
        verify(wishlistRepository).delete(testWishlistItem);
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentItem() {
        // Given
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(wishlistRepository.findById(TEST_WISHLIST_ITEM_ID))
                .thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> wishlistService.removeFromWishlist(TEST_USER_ID, TEST_WISHLIST_ITEM_ID)
        );
        assertEquals("Wishlist item not found", exception.getMessage());
        verify(wishlistRepository, never()).delete(any(WishlistItem.class));
    }

    @Test
    void shouldNormalizeOpenLibraryKey() {
        // Given
        WishlistRequest request = new WishlistRequest();
        
        // Test different formats
        request.setOpenLibraryKey("OL1234567W");  // Plain ID
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(bookService.findAndPersistBookByKey("/works/OL1234567W")).thenReturn(testBook);
        when(wishlistRepository.findByUserIdAndBookId(any(), any())).thenReturn(Optional.empty());
        when(wishlistRepository.save(any())).thenReturn(testWishlistItem);

        // When
        wishlistService.addToWishlistByOpenLibraryKey(TEST_USER_ID, request);

        // Then
        verify(bookService).findAndPersistBookByKey("/works/OL1234567W");
    }
} 