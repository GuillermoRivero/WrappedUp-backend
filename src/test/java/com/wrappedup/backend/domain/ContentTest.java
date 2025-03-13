package com.wrappedup.backend.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContentTest {

    @Test
    void shouldCreateBookWithAllFields() {
        // Given
        String title = "The Lord of the Rings";
        String author = "J.R.R. Tolkien";
        int releaseYear = 1954;
        String platform = "Amazon Kindle";
        String coverUrl = "https://example.com/lotr-cover.jpg";
        String openLibraryKey = "OL123456W";

        // When
        Book book = new Book(title, author, releaseYear, platform, coverUrl, openLibraryKey);

        // Then
        assertNull(book.getId()); // ID should be null until persisted
        assertEquals(title, book.getTitle());
        assertEquals(author, book.getAuthor());
        assertEquals(releaseYear, book.getFirstPublishYear());
        assertEquals(platform, book.getPlatform());
        assertEquals(coverUrl, book.getCoverUrl());
        assertEquals(openLibraryKey, book.getOpenLibraryKey());
    }

    @Test
    void shouldCreateContentWithoutCoverUrl() {
        // Given
        String title = "Test Book";
        String author = "Test Author";
        int releaseYear = 2023;
        String platform = "Test Platform";
        String openLibraryKey = "OL789012W";

        // When
        Book book = new Book(title, author, releaseYear, platform, null, openLibraryKey);

        // Then
        assertNull(book.getCoverUrl());
        assertEquals(title, book.getTitle());
        assertEquals(openLibraryKey, book.getOpenLibraryKey());
    }
} 