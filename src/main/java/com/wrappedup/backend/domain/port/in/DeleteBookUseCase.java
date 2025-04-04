package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;

/**
 * Input port for deleting a book.
 */
public interface DeleteBookUseCase {
    
    /**
     * Delete a book by its ID.
     * 
     * @param id The book ID
     */
    void deleteBook(BookId id);
} 