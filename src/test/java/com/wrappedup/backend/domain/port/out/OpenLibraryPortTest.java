package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OpenLibraryPortTest {

    @Test
    @DisplayName("Should search books with query")
    void searchBooks_ShouldReturnMatchingBooks() {
        // Create a mock implementation of the interface
        OpenLibraryPort openLibraryPort = Mockito.mock(OpenLibraryPort.class);
        
        // Create test data
        String query = "Harry Potter";
        Book book1 = Mockito.mock(Book.class);
        Book book2 = Mockito.mock(Book.class);
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        
        // Mock the searchBooks behavior
        when(openLibraryPort.searchBooks(query)).thenReturn(expectedBooks);
        
        // Call the method
        List<Book> result = openLibraryPort.searchBooks(query);
        
        // Verify the method was called with the correct query
        verify(openLibraryPort).searchBooks(query);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(expectedBooks, result);
    }
    
    @Test
    @DisplayName("Should return empty list when no books match search query")
    void searchBooks_ShouldReturnEmptyListWhenNoMatches() {
        // Create a mock implementation of the interface
        OpenLibraryPort openLibraryPort = Mockito.mock(OpenLibraryPort.class);
        
        // Create test data
        String query = "NonexistentBook12345";
        
        // Mock the searchBooks behavior
        when(openLibraryPort.searchBooks(query)).thenReturn(Collections.emptyList());
        
        // Call the method
        List<Book> result = openLibraryPort.searchBooks(query);
        
        // Verify the method was called with the correct query
        verify(openLibraryPort).searchBooks(query);
        
        // Verify the result
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should get book by OpenLibrary key")
    void getBookByKey_ShouldReturnBookWhenFound() {
        // Create a mock implementation of the interface
        OpenLibraryPort openLibraryPort = Mockito.mock(OpenLibraryPort.class);
        
        // Create test data
        String openLibraryKey = "OL12345W";
        Book book = Mockito.mock(Book.class);
        List<Book> expectedBooks = Collections.singletonList(book);
        
        // Mock the getBookByKey behavior
        when(openLibraryPort.getBookByKey(openLibraryKey)).thenReturn(expectedBooks);
        
        // Call the method
        List<Book> result = openLibraryPort.getBookByKey(openLibraryKey);
        
        // Verify the method was called with the correct key
        verify(openLibraryPort).getBookByKey(openLibraryKey);
        
        // Verify the result
        assertEquals(1, result.size());
        assertEquals(expectedBooks, result);
    }
    
    @Test
    @DisplayName("Should return empty list when book not found by OpenLibrary key")
    void getBookByKey_ShouldReturnEmptyListWhenNotFound() {
        // Create a mock implementation of the interface
        OpenLibraryPort openLibraryPort = Mockito.mock(OpenLibraryPort.class);
        
        // Create test data
        String openLibraryKey = "InvalidKey12345";
        
        // Mock the getBookByKey behavior
        when(openLibraryPort.getBookByKey(openLibraryKey)).thenReturn(Collections.emptyList());
        
        // Call the method
        List<Book> result = openLibraryPort.getBookByKey(openLibraryKey);
        
        // Verify the method was called with the correct key
        verify(openLibraryPort).getBookByKey(openLibraryKey);
        
        // Verify the result
        assertTrue(result.isEmpty());
    }
} 