package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;

import java.time.LocalDate;
import java.util.List;

/**
 * Input port for updating book information.
 */
public interface UpdateBookUseCase {
    
    /**
     * Command for updating a book.
     */
    record UpdateBookCommand(
            BookId id,
            String title,
            String author,
            String isbn,
            String description,
            String coverImageUrl,
            Integer pageCount,
            List<String> genres,
            String language,
            LocalDate publicationDate,
            String publisher,
            String openLibraryKey
    ) {
        public UpdateBookCommand {
            if (id == null) {
                throw new IllegalArgumentException("Book ID cannot be null");
            }
        }
        
        // Constructor without openLibraryKey (for backward compatibility)
        public UpdateBookCommand(
                BookId id,
                String title,
                String author,
                String isbn,
                String description,
                String coverImageUrl,
                Integer pageCount,
                List<String> genres,
                String language,
                LocalDate publicationDate,
                String publisher
        ) {
            this(id, title, author, isbn, description, coverImageUrl, pageCount, genres, language, publicationDate, publisher, null);
        }
    }
    
    /**
     * Update a book based on the provided command.
     * 
     * @param command The update book command
     * @return The updated book
     */
    Book updateBook(UpdateBookCommand command);
} 