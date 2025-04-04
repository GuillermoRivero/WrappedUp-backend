package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;

import java.util.List;
import java.util.Optional;

/**
 * Input port for retrieving book information.
 */
public interface GetBookUseCase {
    
    /**
     * Get a book by its ID.
     * 
     * @param id The book ID
     * @return An optional containing the book if found
     */
    Optional<Book> getBookById(BookId id);
    
    /**
     * Get a book by its ISBN.
     * 
     * @param isbn The ISBN
     * @return An optional containing the book if found
     */
    Optional<Book> getBookByIsbn(String isbn);
    
    /**
     * Search for books by title.
     * 
     * @param titleText Text to search in titles
     * @return List of matching books
     */
    List<Book> searchBooksByTitle(String titleText);
    
    /**
     * Search for books by author.
     * 
     * @param authorText Text to search in author names
     * @return List of matching books
     */
    List<Book> searchBooksByAuthor(String authorText);
    
    /**
     * Get books by genre.
     * 
     * @param genre The genre to filter by
     * @return List of books in the specified genre
     */
    List<Book> getBooksByGenre(String genre);
    
    /**
     * Get all books with pagination.
     * 
     * @param page The page number (0-based)
     * @param size The page size
     * @return A list of books for the requested page
     */
    List<Book> getAllBooks(int page, int size);
    
    /**
     * Get the total number of books.
     * 
     * @return The total number of books
     */
    long countBooks();
} 