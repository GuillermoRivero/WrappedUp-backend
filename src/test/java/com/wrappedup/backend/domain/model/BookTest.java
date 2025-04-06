package com.wrappedup.backend.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private final BookId bookId = BookId.generate();
    private final String title = "Test Book Title";
    private final String author = "Test Author";
    private final String isbn = "9781234567890";
    private final String description = "Test book description";
    private final String coverImageUrl = "https://example.com/cover.jpg";
    private final Integer pageCount = 300;
    private final List<String> genres = Arrays.asList("Fiction", "Thriller");
    private final String language = "English";
    private final LocalDate publicationDate = LocalDate.of(2023, 1, 1);
    private final String publisher = "Test Publisher";
    private final String openLibraryKey = "OL12345M";
    private final LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    private final LocalDateTime updatedAt = LocalDateTime.now();

    @Test
    @DisplayName("Should create a new book with all fields")
    void createNewBook_ShouldCreateBookWithAllFields() {
        // Act
        Book book = Book.createNewBook(
                title, author, isbn, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher, openLibraryKey
        );
        
        // Assert
        assertNotNull(book);
        assertNotNull(book.getId());
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(isbn, book.getIsbn());
        assertEquals(description, book.getDescription());
        assertEquals(coverImageUrl, book.getCoverImageUrl());
        assertEquals(pageCount, book.getPageCount());
        assertEquals(genres, book.getGenres());
        assertEquals(language, book.getLanguage());
        assertEquals(publicationDate, book.getPublicationDate());
        assertEquals(publisher, book.getPublisher());
        assertEquals(openLibraryKey, book.getOpenLibraryKey());
        assertNotNull(book.getCreatedAt());
        assertNotNull(book.getUpdatedAt());
        // CreatedAt and updatedAt should be the same for a new book
        assertEquals(book.getCreatedAt(), book.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should reconstitute an existing book with all fields")
    void reconstitute_ShouldCreateBookWithAllFields() {
        // Act
        Book book = Book.reconstitute(
                bookId, title, author, isbn, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher, openLibraryKey,
                createdAt, updatedAt
        );
        
        // Assert
        assertNotNull(book);
        assertEquals(bookId, book.getId());
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(isbn, book.getIsbn());
        assertEquals(description, book.getDescription());
        assertEquals(coverImageUrl, book.getCoverImageUrl());
        assertEquals(pageCount, book.getPageCount());
        assertEquals(genres, book.getGenres());
        assertEquals(language, book.getLanguage());
        assertEquals(publicationDate, book.getPublicationDate());
        assertEquals(publisher, book.getPublisher());
        assertEquals(openLibraryKey, book.getOpenLibraryKey());
        assertEquals(createdAt, book.getCreatedAt());
        assertEquals(updatedAt, book.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should update book with new information")
    void updateDetails_ShouldUpdateBookFields() {
        // Arrange
        Book book = Book.reconstitute(
                bookId, title, author, isbn, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher, openLibraryKey,
                createdAt, updatedAt
        );
        
        String newTitle = "Updated Title";
        String newDescription = "Updated description";
        Integer newPageCount = 350;
        LocalDateTime beforeUpdate = LocalDateTime.now();
        
        // Act
        book.updateDetails(
                newTitle, author, isbn, newDescription, coverImageUrl,
                newPageCount, genres, language, publicationDate, publisher, openLibraryKey
        );
        
        // Assert
        assertEquals(bookId, book.getId());
        assertEquals(newTitle, book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(isbn, book.getIsbn());
        assertEquals(newDescription, book.getDescription());
        assertEquals(coverImageUrl, book.getCoverImageUrl());
        assertEquals(newPageCount, book.getPageCount());
        assertEquals(genres, book.getGenres());
        assertEquals(language, book.getLanguage());
        assertEquals(publicationDate, book.getPublicationDate());
        assertEquals(publisher, book.getPublisher());
        assertEquals(openLibraryKey, book.getOpenLibraryKey());
        assertEquals(createdAt, book.getCreatedAt());
        assertTrue(book.getUpdatedAt().isAfter(beforeUpdate) || book.getUpdatedAt().equals(beforeUpdate));
    }
    
    @Test
    @DisplayName("Equal books should be equal")
    void equals_WithSameId_ShouldBeEqual() {
        // Arrange
        Book book1 = Book.reconstitute(
                bookId, title, author, isbn, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher, openLibraryKey,
                createdAt, updatedAt
        );
        
        Book book2 = Book.reconstitute(
                bookId, title, author, isbn, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher, openLibraryKey,
                createdAt, updatedAt
        );
        
        // Assert
        assertEquals(book1, book2);
        assertEquals(book1.hashCode(), book2.hashCode());
    }
    
    @Test
    @DisplayName("Books with different IDs should not be equal")
    void equals_WithDifferentIds_ShouldNotBeEqual() {
        // Arrange
        Book book1 = Book.reconstitute(
                bookId, title, author, isbn, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher, openLibraryKey,
                createdAt, updatedAt
        );
        
        Book book2 = Book.reconstitute(
                BookId.generate(), title, author, isbn, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher, openLibraryKey,
                createdAt, updatedAt
        );
        
        // Assert
        assertNotEquals(book1, book2);
        assertNotEquals(book1.hashCode(), book2.hashCode());
    }
    
    @Test
    @DisplayName("Should handle null field values properly")
    void reconstitute_WithNullValues_ShouldCreateBookCorrectly() {
        // Act
        Book book = Book.reconstitute(
                bookId, title, author, isbn, null, null,
                null, null, null, null, null, null,
                createdAt, updatedAt
        );
        
        // Assert
        assertNotNull(book);
        assertEquals(bookId, book.getId());
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(isbn, book.getIsbn());
        assertNull(book.getDescription());
        assertNull(book.getCoverImageUrl());
        assertNull(book.getPageCount());
        assertNotNull(book.getGenres());
        assertTrue(book.getGenres().isEmpty());
        assertNull(book.getLanguage());
        assertNull(book.getPublicationDate());
        assertNull(book.getPublisher());
        assertNull(book.getOpenLibraryKey());
        assertEquals(createdAt, book.getCreatedAt());
        assertEquals(updatedAt, book.getUpdatedAt());
    }
} 