package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Input port for book operations.
 */
public interface BookUseCase {
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
            String publisher) {
        
        public CreateBookCommand {
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Title cannot be null or blank");
            }
            if (author == null || author.isBlank()) {
                throw new IllegalArgumentException("Author cannot be null or blank");
            }
        }
    }
    
    /**
     * Command for updating a book.
     */
    record UpdateBookCommand(
            BookId bookId,
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
        
        public UpdateBookCommand {
            if (bookId == null) {
                throw new IllegalArgumentException("Book ID cannot be null");
            }
        }
    }
    
    /**
     * Command for searching books.
     */
    record SearchBooksCommand(
            String query,
            String searchBy,
            String genre,
            int page,
            int size) {
        
        public SearchBooksCommand {
            if (page < 0) {
                throw new IllegalArgumentException("Page cannot be negative");
            }
            if (size <= 0) {
                throw new IllegalArgumentException("Size must be positive");
            }
        }
    }
    
    /**
     * Result for paginated book queries.
     */
    record PaginatedBooksResult(
            List<Book> books,
            int page,
            int size,
            long totalElements,
            int totalPages) {
    }
    
    /**
     * Create a new book.
     * @param command The create book command
     * @return The created book
     * @throws IllegalArgumentException if a book with the same ISBN already exists
     */
    Book createBook(CreateBookCommand command) throws IllegalArgumentException;
    
    /**
     * Update an existing book.
     * @param command The update book command
     * @return The updated book
     * @throws IllegalArgumentException if the book does not exist
     */
    Book updateBook(UpdateBookCommand command) throws IllegalArgumentException;
    
    /**
     * Get a book by its ID.
     * @param id The book ID
     * @return The book if found
     */
    Optional<Book> getBookById(BookId id);
    
    /**
     * Search books based on various criteria.
     * @param command The search books command
     * @return Paginated result of books matching the search criteria
     */
    PaginatedBooksResult searchBooks(SearchBooksCommand command);
    
    /**
     * Get books by genre.
     * @param genre The genre to filter by
     * @param page The page number (0-based)
     * @param size The page size
     * @return Paginated result of books in the specified genre
     */
    PaginatedBooksResult getBooksByGenre(String genre, int page, int size);
    
    /**
     * Get all books with pagination.
     * @param page The page number (0-based)
     * @param size The page size
     * @return Paginated result of all books
     */
    PaginatedBooksResult getAllBooks(int page, int size);
    
    /**
     * Delete a book by its ID.
     * @param id The book ID
     * @throws IllegalArgumentException if the book does not exist
     */
    void deleteBook(BookId id) throws IllegalArgumentException;
} 