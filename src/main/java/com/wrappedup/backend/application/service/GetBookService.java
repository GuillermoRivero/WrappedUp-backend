package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.GetBookUseCase;
import com.wrappedup.backend.domain.port.out.BookRepository;
import com.wrappedup.backend.domain.port.out.OpenLibraryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the GetBookUseCase for retrieving book information.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GetBookService implements GetBookUseCase {
    
    private final BookRepository bookRepository;
    private final OpenLibraryPort openLibraryPort;
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(BookId id) {
        log.debug("Retrieving book by ID: {}", id);
        return bookRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getBookByIsbn(String isbn) {
        log.debug("Retrieving book by ISBN: {}", isbn);
        if (isbn == null || isbn.isBlank()) {
            log.warn("Attempted to find book with null or blank ISBN");
            return Optional.empty();
        }
        return bookRepository.findByIsbn(isbn);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> searchBooksByTitle(String titleText) {
        log.debug("Searching books by title containing: {}", titleText);
        if (titleText == null || titleText.isBlank()) {
            log.warn("Attempted to search books with null or blank title");
            return Collections.emptyList();
        }
        return bookRepository.findByTitleContaining(titleText);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> searchBooksByAuthor(String authorText) {
        log.debug("Searching books by author containing: {}", authorText);
        if (authorText == null || authorText.isBlank()) {
            log.warn("Attempted to search books with null or blank author");
            return Collections.emptyList();
        }
        return bookRepository.findByAuthorContaining(authorText);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooksByGenre(String genre) {
        log.debug("Retrieving books by genre: {}", genre);
        if (genre == null || genre.isBlank()) {
            log.warn("Attempted to find books with null or blank genre");
            return Collections.emptyList();
        }
        return bookRepository.findByGenre(genre);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks(int page, int size) {
        log.debug("Retrieving all books with pagination - page: {}, size: {}", page, size);
        if (page < 0 || size <= 0) {
            log.warn("Invalid pagination parameters: page={}, size={}", page, size);
            return Collections.emptyList();
        }
        return bookRepository.findAll(page, size);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countBooks() {
        log.debug("Counting total number of books");
        return bookRepository.count();
    }
    
    /**
     * Searches for books in the OpenLibrary using the provided query.
     * This method will query the OpenLibrary API and return the results.
     * 
     * @param query The search query
     * @return List of Book objects matching the query
     */
    @Transactional(readOnly = true)
    public List<Book> searchBooksInOpenLibrary(String query) {
        log.info("Searching books in OpenLibrary with query: {}", query);
        if (query == null || query.isBlank()) {
            log.warn("Attempted to search OpenLibrary with null or blank query");
            return Collections.emptyList();
        }
        
        try {
            List<Book> results = openLibraryPort.searchBooks(query);
            log.info("Found {} books in OpenLibrary for query: {}", results.size(), query);
            return results;
        } catch (Exception e) {
            log.error("Error searching OpenLibrary: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Retrieves a book from OpenLibrary by its key and persists it if not already in the database.
     * 
     * @param openLibraryKey The OpenLibrary key
     * @return Optional containing the Book if found, empty otherwise
     */
    @Transactional
    public Optional<Book> getAndPersistBookByOpenLibraryKey(String openLibraryKey) {
        log.info("Retrieving and persisting book from OpenLibrary with key: {}", openLibraryKey);
        if (openLibraryKey == null || openLibraryKey.isBlank()) {
            log.warn("Attempted to retrieve book from OpenLibrary with null or blank key");
            return Optional.empty();
        }
        
        try {
            // First, check if the book already exists in the database by its Open Library key
            Optional<Book> existingBookByKey = bookRepository.findByOpenLibraryKey(openLibraryKey);
            if (existingBookByKey.isPresent()) {
                log.info("Book with Open Library key {} already exists in database", openLibraryKey);
                return existingBookByKey;
            }
            
            // If not found, fetch from Open Library API
            List<Book> results = openLibraryPort.getBookByKey(openLibraryKey);
            if (results.isEmpty()) {
                log.warn("No book found in OpenLibrary for key: {}", openLibraryKey);
                return Optional.empty();
            }
            
            Book openLibraryBook = results.get(0);
            
            // Also check if book with same ISBN already exists
            if (openLibraryBook.getIsbn() != null && !openLibraryBook.getIsbn().isBlank()) {
                Optional<Book> existingBookByIsbn = bookRepository.findByIsbn(openLibraryBook.getIsbn());
                if (existingBookByIsbn.isPresent()) {
                    log.info("Book with ISBN {} already exists in database", openLibraryBook.getIsbn());
                    return existingBookByIsbn;
                }
            }
            
            // Persist the new book
            Book savedBook = bookRepository.save(openLibraryBook);
            log.info("Successfully persisted book from OpenLibrary with ID: {}", savedBook.getId());
            return Optional.of(savedBook);
        } catch (Exception e) {
            log.error("Error retrieving book from OpenLibrary: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
} 