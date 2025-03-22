package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.WishlistItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaWishlistRepositoryTest {

    private static final UUID TEST_WISHLIST_ITEM_ID = UUID.fromString("b9876543-e21a-45c6-789d-012345678901");
    private static final UUID TEST_USER_ID = UUID.fromString("f2295dea-c8cb-4493-ae1c-dbfebbfc4f3a");
    private static final UUID TEST_BOOK_ID = UUID.fromString("a1234567-e89b-12d3-a456-426614174000");

    @Mock
    private SpringDataWishlistRepository springDataRepository;

    @InjectMocks
    private JpaWishlistRepository jpaWishlistRepository;

    @Test
    void shouldSaveWishlistItem() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        user.setId(TEST_USER_ID);
        
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        book.setId(TEST_BOOK_ID);
        
        WishlistItem wishlistItem = new WishlistItem(user, book, "Test description", 3, true);
        wishlistItem.setId(TEST_WISHLIST_ITEM_ID);
        
        when(springDataRepository.save(any(WishlistItem.class))).thenReturn(wishlistItem);

        // When
        WishlistItem savedItem = jpaWishlistRepository.save(wishlistItem);

        // Then
        assertNotNull(savedItem);
        assertEquals(TEST_WISHLIST_ITEM_ID, savedItem.getId());
        assertEquals("Test description", savedItem.getDescription());
        verify(springDataRepository).save(wishlistItem);
    }

    @Test
    void shouldFindWishlistItemById() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        user.setId(TEST_USER_ID);
        
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        book.setId(TEST_BOOK_ID);
        
        WishlistItem wishlistItem = new WishlistItem(user, book, "Test description", 3, true);
        wishlistItem.setId(TEST_WISHLIST_ITEM_ID);
        
        when(springDataRepository.findById(TEST_WISHLIST_ITEM_ID)).thenReturn(Optional.of(wishlistItem));

        // When
        Optional<WishlistItem> result = jpaWishlistRepository.findById(TEST_WISHLIST_ITEM_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(TEST_WISHLIST_ITEM_ID, result.get().getId());
        verify(springDataRepository).findById(TEST_WISHLIST_ITEM_ID);
    }

    @Test
    void shouldFindAllWishlistItemsByUser() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        user.setId(TEST_USER_ID);
        
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        book.setId(TEST_BOOK_ID);
        
        WishlistItem wishlistItem = new WishlistItem(user, book, "Test description", 3, true);
        wishlistItem.setId(TEST_WISHLIST_ITEM_ID);
        
        List<WishlistItem> wishlistItems = List.of(wishlistItem);
        
        when(springDataRepository.findAllByUser(user)).thenReturn(wishlistItems);

        // When
        List<WishlistItem> result = jpaWishlistRepository.findAllByUser(user);

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(TEST_WISHLIST_ITEM_ID, result.get(0).getId());
        verify(springDataRepository).findAllByUser(user);
    }

    @Test
    void shouldFindWishlistItemByUserAndBook() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        user.setId(TEST_USER_ID);
        
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        book.setId(TEST_BOOK_ID);
        
        WishlistItem wishlistItem = new WishlistItem(user, book, "Test description", 3, true);
        wishlistItem.setId(TEST_WISHLIST_ITEM_ID);
        
        when(springDataRepository.findByUserAndBook(user, book)).thenReturn(Optional.of(wishlistItem));

        // When
        Optional<WishlistItem> result = jpaWishlistRepository.findByUserAndBook(user, book);

        // Then
        assertTrue(result.isPresent());
        assertEquals(TEST_WISHLIST_ITEM_ID, result.get().getId());
        verify(springDataRepository).findByUserAndBook(user, book);
    }

    @Test
    void shouldFindWishlistItemByUserIdAndBookId() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        user.setId(TEST_USER_ID);
        
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        book.setId(TEST_BOOK_ID);
        
        WishlistItem wishlistItem = new WishlistItem(user, book, "Test description", 3, true);
        wishlistItem.setId(TEST_WISHLIST_ITEM_ID);
        
        List<WishlistItem> allItems = Arrays.asList(wishlistItem);
        when(springDataRepository.findAll()).thenReturn(allItems);

        // When
        Optional<WishlistItem> result = jpaWishlistRepository.findByUserIdAndBookId(TEST_USER_ID, TEST_BOOK_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(TEST_WISHLIST_ITEM_ID, result.get().getId());
        verify(springDataRepository).findAll();
    }

    @Test
    void shouldDeleteWishlistItem() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        user.setId(TEST_USER_ID);
        
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        book.setId(TEST_BOOK_ID);
        
        WishlistItem wishlistItem = new WishlistItem(user, book, "Test description", 3, true);
        wishlistItem.setId(TEST_WISHLIST_ITEM_ID);
        
        // When
        jpaWishlistRepository.delete(wishlistItem);

        // Then
        verify(springDataRepository).delete(wishlistItem);
    }

    @Test
    void shouldCheckExistenceByUserAndBook() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        user.setId(TEST_USER_ID);
        
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        book.setId(TEST_BOOK_ID);
        
        when(springDataRepository.existsByUserAndBook(user, book)).thenReturn(true);

        // When
        boolean exists = jpaWishlistRepository.existsByUserAndBook(user, book);

        // Then
        assertTrue(exists);
        verify(springDataRepository).existsByUserAndBook(user, book);
    }
} 