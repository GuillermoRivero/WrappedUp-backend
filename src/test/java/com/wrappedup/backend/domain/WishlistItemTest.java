package com.wrappedup.backend.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WishlistItemTest {

    @Test
    void shouldCreateWishlistItemWithAllFields() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        String description = "Test description";
        int priority = 3;
        boolean isPublic = true;
        
        // When
        WishlistItem wishlistItem = new WishlistItem(user, book, description, priority, isPublic);
        
        // Then
        assertNotNull(wishlistItem);
        assertEquals(user, wishlistItem.getUser());
        assertEquals(book, wishlistItem.getBook());
        assertEquals(description, wishlistItem.getDescription());
        assertEquals(priority, wishlistItem.getPriority());
        assertTrue(wishlistItem.isPublic());
    }
    
    @Test
    void shouldCreateWishlistItemWithMinimalFields() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        
        // When
        WishlistItem wishlistItem = new WishlistItem(user, book);
        
        // Then
        assertNotNull(wishlistItem);
        assertEquals(user, wishlistItem.getUser());
        assertEquals(book, wishlistItem.getBook());
        assertNull(wishlistItem.getDescription());
        assertEquals(3, wishlistItem.getPriority()); // El valor por defecto es 3
        assertFalse(wishlistItem.isPublic()); // Default should be false
    }
    
    @Test
    void shouldSetAndGetIsPublic() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        WishlistItem wishlistItem = new WishlistItem(user, book);
        
        // Initial state should be false (default)
        assertFalse(wishlistItem.isPublic());
        
        // When setting to true
        wishlistItem.setPublic(true);
        
        // Then should be true
        assertTrue(wishlistItem.isPublic());
        
        // When setting to false
        wishlistItem.setPublic(false);
        
        // Then should be false
        assertFalse(wishlistItem.isPublic());
    }
    
    @Test
    void shouldHandleNullIsPublicInConstructor() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        
        // When using constructor with null for isPublic parameter
        WishlistItem wishlistItem = new WishlistItem(user, book, "Description", 3, null);
        
        // Then should default to false
        assertFalse(wishlistItem.isPublic());
    }
    
    @Test
    void shouldUseExplicitGetterAndSetter() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "/works/OL1234567W");
        WishlistItem wishlistItem = new WishlistItem(user, book);
        
        // When using explicit setters/getters
        wishlistItem.setPublic(true);
        Boolean isPublicValue = wishlistItem.isPublic();
        
        // Then
        assertTrue(isPublicValue);
    }
} 