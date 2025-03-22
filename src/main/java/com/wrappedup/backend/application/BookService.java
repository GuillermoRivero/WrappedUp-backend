package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import com.wrappedup.backend.infrastructure.service.BookPersistenceService;
import com.wrappedup.backend.infrastructure.service.OpenLibraryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final OpenLibraryService openLibraryService;
    private final BookPersistenceService bookPersistenceService;

    @PersistenceContext
    private EntityManager entityManager;
    
    public BookService(BookRepository bookRepository, 
                      OpenLibraryService openLibraryService,
                      BookPersistenceService bookPersistenceService) {
        this.bookRepository = bookRepository;
        this.openLibraryService = openLibraryService;
        this.bookPersistenceService = bookPersistenceService;
    }

    public Optional<Book> findById(UUID id) {
        return bookRepository.findById(id);
    }
    
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
    
    public Book save(Book book) {
        if (book.getId() != null) {
            return bookPersistenceService.saveBookWithConcurrencyHandling(book);
        }
        
        return bookRepository.save(book);
    }
    
    public List<Book> searchInOpenLibrary(String query) {
        List<Book> books = openLibraryService.searchBooks(query);
        
        for (Book book : books) {
            findAndPersistBookIfNotExists(book);
        }
        
        return books;
    }
    
    public Book findAndPersistBookByKey(String openLibraryKey) {
        String normalizedKey = normalizeOpenLibraryKey(openLibraryKey);
        
        Optional<Book> existingBook = bookRepository.findByOpenLibraryKey(normalizedKey);
        if (existingBook.isPresent()) {
            return existingBook.get();
        }
        
        List<Book> books = openLibraryService.getBookByKey(normalizedKey);
        
        if (books != null && !books.isEmpty()) {
            Book book = books.get(0);
            return save(book);
        }
        
        throw new IllegalArgumentException("Book not found with OpenLibrary key: " + normalizedKey);
    }
    
    private Book findAndPersistBookIfNotExists(Book book) {
        if (book.getOpenLibraryKey() == null) {
            return book; 
        }
        
        Optional<Book> existingBook = bookRepository.findByOpenLibraryKey(book.getOpenLibraryKey());
        if (existingBook.isPresent()) {
            return existingBook.get();
        }
        
        return save(book);
    }
    
    private String normalizeOpenLibraryKey(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        
        if (!key.startsWith("/works/")) {
            if (key.startsWith("OL") && key.contains("W")) {
                return "/works/" + key;
            }
            
            return key;
        }
        
        return key;
    }
} 