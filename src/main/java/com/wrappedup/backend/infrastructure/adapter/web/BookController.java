package com.wrappedup.backend.infrastructure.adapter.web;

import com.wrappedup.backend.application.service.GetBookService;
import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.CreateBookUseCase;
import com.wrappedup.backend.domain.port.in.DeleteBookUseCase;
import com.wrappedup.backend.domain.port.in.UpdateBookUseCase;
import com.wrappedup.backend.infrastructure.adapter.web.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for Book operations using hexagonal architecture.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    
    private final CreateBookUseCase createBookUseCase;
    private final GetBookService getBookService;
    private final UpdateBookUseCase updateBookUseCase;
    private final DeleteBookUseCase deleteBookUseCase;
    
    /**
     * Get book by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable UUID id) {
        log.debug("REST request to get Book by id: {}", id);
        BookId bookId = BookId.of(id);
        Optional<Book> book = getBookService.getBookById(bookId);
        return book.map(b -> ResponseEntity.ok(BookDTO.fromDomain(b)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get book by ISBN
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable String isbn) {
        log.debug("REST request to get Book by ISBN: {}", isbn);
        Optional<Book> book = getBookService.getBookByIsbn(isbn);
        return book.map(b -> ResponseEntity.ok(BookDTO.fromDomain(b)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all books with pagination
     */
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.debug("REST request to get all Books, page: {}, size: {}", page, size);
        List<Book> books = getBookService.getAllBooks(page, size);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }
    
    /**
     * Count all books
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBooks() {
        log.debug("REST request to count all Books");
        long count = getBookService.countBooks();
        return ResponseEntity.ok(count);
    }
    
    /**
     * Search books by title
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<BookDTO>> searchBooksByTitle(@RequestParam String query) {
        log.debug("REST request to search Books by title: {}", query);
        List<Book> books = getBookService.searchBooksByTitle(query);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }
    
    /**
     * Search books by author
     */
    @GetMapping("/search/author")
    public ResponseEntity<List<BookDTO>> searchBooksByAuthor(@RequestParam String query) {
        log.debug("REST request to search Books by author: {}", query);
        List<Book> books = getBookService.searchBooksByAuthor(query);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }
    
    /**
     * Search books by genre
     */
    @GetMapping("/search/genre")
    public ResponseEntity<List<BookDTO>> getBooksByGenre(@RequestParam String genre) {
        log.debug("REST request to get Books by genre: {}", genre);
        List<Book> books = getBookService.getBooksByGenre(genre);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }
    
    /**
     * Search books in OpenLibrary
     */
    @GetMapping("/openlibrary/search")
    public ResponseEntity<List<BookDTO>> searchBooksInOpenLibrary(@RequestParam String query) {
        log.debug("REST request to search OpenLibrary: {}", query);
        List<Book> books = getBookService.searchBooksInOpenLibrary(query);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }
    
    /**
     * Get book from OpenLibrary by key and persist it
     */
    @GetMapping("/openlibrary/key/{key}")
    public ResponseEntity<BookDTO> getBookFromOpenLibraryByKey(@PathVariable String key) {
        log.debug("REST request to get and persist book from OpenLibrary by key: {}", key);
        Optional<Book> book = getBookService.getAndPersistBookByOpenLibraryKey(key);
        return book.map(b -> ResponseEntity.ok(BookDTO.fromDomain(b)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new book
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody CreateBookRequest request) {
        log.debug("REST request to create Book: {}", request);
        
        CreateBookUseCase.CreateBookCommand command = new CreateBookUseCase.CreateBookCommand(
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                request.getDescription(),
                request.getCoverImageUrl(),
                request.getPageCount(),
                request.getGenres(),
                request.getLanguage(),
                request.getPublicationDate(),
                request.getPublisher()
        );
        
        BookId bookId = createBookUseCase.createBook(command);
        
        Optional<Book> createdBook = getBookService.getBookById(bookId);
        
        return createdBook
                .map(book -> ResponseEntity.status(HttpStatus.CREATED).body(BookDTO.fromDomain(book)))
                .orElse(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    /**
     * Update an existing book
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BookDTO> updateBook(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBookRequest request) {
        log.debug("REST request to update Book: {}", request);
        
        BookId bookId = BookId.of(id);
        
        UpdateBookUseCase.UpdateBookCommand command = new UpdateBookUseCase.UpdateBookCommand(
                bookId,
                request.getTitle(),
                request.getAuthor(),
                request.getIsbn(),
                request.getDescription(),
                request.getCoverImageUrl(),
                request.getPageCount(),
                request.getGenres(),
                request.getLanguage(),
                request.getPublicationDate(),
                request.getPublisher()
        );
        
        try {
            Book updatedBook = updateBookUseCase.updateBook(command);
            return ResponseEntity.ok(BookDTO.fromDomain(updatedBook));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating book: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete a book
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteBook(@PathVariable UUID id) {
        log.debug("REST request to delete Book: {}", id);
        
        BookId bookId = BookId.of(id);
        
        try {
            deleteBookUseCase.deleteBook(bookId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting book: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search books (compatible with legacy endpoint)
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String query) {
        log.debug("REST request to search Books with query: {}", query);
        // For backward compatibility, search in both title and author
        List<Book> booksByTitle = getBookService.searchBooksByTitle(query);
        List<Book> booksByAuthor = getBookService.searchBooksByAuthor(query);
        
        // Combine results (removing duplicates by ID)
        Map<BookId, Book> bookMap = new HashMap<>();
        
        // Add books from title search
        for (Book book : booksByTitle) {
            bookMap.put(book.getId(), book);
        }
        
        // Add books from author search (if not already added)
        for (Book book : booksByAuthor) {
            if (!bookMap.containsKey(book.getId())) {
                bookMap.put(book.getId(), book);
            }
        }
        
        // Always search OpenLibrary to augment local results
        List<Book> openLibraryResults = getBookService.searchBooksInOpenLibrary(query);
        
        // Add OpenLibrary results (if not already added by ID)
        for (Book book : openLibraryResults) {
            if (!bookMap.containsKey(book.getId())) {
                bookMap.put(book.getId(), book);
            }
        }
        
        // Convert map values to list
        List<Book> combinedResults = new ArrayList<>(bookMap.values());
        
        List<BookDTO> bookDTOs = combinedResults.stream()
                .map(BookDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }
    
    /**
     * Request object for creating a new book
     */
    public static class CreateBookRequest {
        private String title;
        private String author;
        private String isbn;
        private String description;
        private String coverImageUrl;
        private Integer pageCount;
        private List<String> genres;
        private String language;
        private LocalDate publicationDate;
        private String publisher;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCoverImageUrl() { return coverImageUrl; }
        public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
        public Integer getPageCount() { return pageCount; }
        public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
        public List<String> getGenres() { return genres; }
        public void setGenres(List<String> genres) { this.genres = genres; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public LocalDate getPublicationDate() { return publicationDate; }
        public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
        public String getPublisher() { return publisher; }
        public void setPublisher(String publisher) { this.publisher = publisher; }
    }
    
    /**
     * Request object for updating an existing book
     */
    public static class UpdateBookRequest {
        private String title;
        private String author;
        private String isbn;
        private String description;
        private String coverImageUrl;
        private Integer pageCount;
        private List<String> genres;
        private String language;
        private LocalDate publicationDate;
        private String publisher;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getCoverImageUrl() { return coverImageUrl; }
        public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
        public Integer getPageCount() { return pageCount; }
        public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }
        public List<String> getGenres() { return genres; }
        public void setGenres(List<String> genres) { this.genres = genres; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
        public LocalDate getPublicationDate() { return publicationDate; }
        public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
        public String getPublisher() { return publisher; }
        public void setPublisher(String publisher) { this.publisher = publisher; }
    }
} 