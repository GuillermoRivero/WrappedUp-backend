package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for book repository operations.
 * This is an interface in the domain that is implemented by the infrastructure layer.
 */
public interface BookRepository {
    /**
     * Save a book entity.
     * @param book The book to save
     * @return The saved book
     */
    Book save(Book book);
    
    /**
     * Find a book by its ID.
     * @param id The book ID
     * @return An optional containing the book if found
     */
    Optional<Book> findById(BookId id);
    
    /**
     * Find books by title containing the given text.
     * @param titleText Text to search in titles
     * @return List of books matching the title search
     */
    List<Book> findByTitleContaining(String titleText);
    
    /**
     * Find books by author containing the given text.
     * @param authorText Text to search in author names
     * @return List of books matching the author search
     */
    List<Book> findByAuthorContaining(String authorText);
    
    /**
     * Find a book by its ISBN.
     * @param isbn The ISBN to search for
     * @return An optional containing the book if found
     */
    Optional<Book> findByIsbn(String isbn);
    
    /**
     * Find books by genre.
     * @param genre The genre to search for
     * @return List of books in the specified genre
     */
    List<Book> findByGenre(String genre);
    
    /**
     * Delete a book by its ID.
     * @param id The book ID
     */
    void deleteById(BookId id);
    
    /**
     * Check if a book with the given ISBN exists.
     * @param isbn The ISBN to check
     * @return True if a book with the ISBN exists, false otherwise
     */
    boolean existsByIsbn(String isbn);
    
    /**
     * Find all books with pagination.
     * @param page The page number (0-based)
     * @param size The page size
     * @return A list of books for the requested page
     */
    List<Book> findAll(int page, int size);
    
    /**
     * Count the total number of books.
     * @return The total number of books
     */
    long count();
    
    /**
     * Find a book by its OpenLibrary key.
     * @param openLibraryKey The OpenLibrary key to search for
     * @return An optional containing the book if found
     */
    Optional<Book> findByOpenLibraryKey(String openLibraryKey);
} 