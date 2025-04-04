package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.Book;

import java.util.List;

/**
 * Output port for OpenLibrary operations.
 */
public interface OpenLibraryPort {
    
    /**
     * Search for books in OpenLibrary.
     * 
     * @param query The search query
     * @return List of matching books
     */
    List<Book> searchBooks(String query);
    
    /**
     * Get a book by its OpenLibrary key.
     * 
     * @param openLibraryKey The OpenLibrary key
     * @return List containing the found book, or empty if not found
     */
    List<Book> getBookByKey(String openLibraryKey);
} 