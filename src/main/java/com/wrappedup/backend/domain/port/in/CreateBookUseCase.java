package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;

import java.time.LocalDate;
import java.util.List;

/**
 * Input port for creating a new book.
 */
public interface CreateBookUseCase {
    
    /**
     * Command for creating a new book.
     */
    record CreateBookCommand(
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
        public CreateBookCommand {
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Title cannot be null or blank");
            }
            if (author == null || author.isBlank()) {
                throw new IllegalArgumentException("Author cannot be null or blank");
            }
            // openLibraryKey is optional and can be null
        }
        
        // Constructor without openLibraryKey (for backward compatibility)
        public CreateBookCommand(
                String title, 
                String author, 
                String isbn, 
                String description, 
                String coverImageUrl, 
                Integer pageCount, 
                List<String> genres, 
                String language, 
                LocalDate publicationDate, 
                String publisher) {
            this(title, author, isbn, description, coverImageUrl, pageCount, genres, language, publicationDate, publisher, null);
        }
    }
    
    /**
     * Create a new book based on the provided command.
     * 
     * @param command The create book command
     * @return The ID of the created book
     */
    BookId createBook(CreateBookCommand command);
} 