package com.wrappedup.backend.infrastructure.adapter.jpa.adapter;

import com.wrappedup.backend.domain.exception.BookPersistenceException;
import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.out.BookRepository;
import com.wrappedup.backend.infrastructure.adapter.jpa.entity.BookJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.jpa.repository.BookJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA implementation of the BookRepository port.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaBookRepositoryAdapter implements BookRepository {
    
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/";
    
    private final BookJpaRepository bookJpaRepository;
    
    @Override
    @Transactional
    public Book save(Book book) {
        try {
            BookJpaEntity entity = mapToJpaEntity(book);
            BookJpaEntity savedEntity = bookJpaRepository.save(entity);
            return mapToDomainEntity(savedEntity);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while saving book", e);
            throw new BookPersistenceException("Book could not be saved due to a data conflict", e);
        } catch (Exception e) {
            log.error("Error saving book", e);
            throw new BookPersistenceException("Error saving book: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(BookId id) {
        return bookJpaRepository.findById(id.getValue())
                .map(this::mapToDomainEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findByTitleContaining(String titleText) {
        return bookJpaRepository.findByTitleContainingIgnoreCase(titleText)
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAuthorContaining(String authorText) {
        return bookJpaRepository.findByAuthorContainingIgnoreCase(authorText)
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByIsbn(String isbn) {
        return bookJpaRepository.findByIsbn(isbn)
                .map(this::mapToDomainEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findByGenre(String genre) {
        return bookJpaRepository.findByGenre(genre)
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteById(BookId id) {
        bookJpaRepository.deleteById(id.getValue());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        return bookJpaRepository.existsByIsbn(isbn);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookJpaRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return bookJpaRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByOpenLibraryKey(String openLibraryKey) {
        return bookJpaRepository.findByOpenLibraryKey(openLibraryKey)
                .map(this::mapToDomainEntity);
    }
    
    /**
     * Maps a domain Book entity to a JPA entity.
     */
    private BookJpaEntity mapToJpaEntity(Book book) {
        if (book == null) {
            return null;
        }
        
        BookJpaEntity entity = new BookJpaEntity();
        entity.setId(book.getId().getValue());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        
        // Set ISBN
        if (book.getIsbn() != null) {
            List<String> isbns = new ArrayList<>();
            isbns.add(book.getIsbn());
            entity.setIsbns(isbns);
        }
        
        // Set description as first_sentence
        entity.setFirstSentence(book.getDescription());
        
        // Set cover URL and cover ID
        entity.setCoverUrl(book.getCoverImageUrl());
        
        // Try to extract cover ID from URL if present
        if (book.getCoverImageUrl() != null && book.getCoverImageUrl().contains(COVER_URL)) {
            try {
                String coverIdStr = book.getCoverImageUrl()
                        .replace(COVER_URL, "")
                        .replace("-L.jpg", "")
                        .replace("-M.jpg", "")
                        .replace("-S.jpg", "");
                entity.setCoverId(Long.parseLong(coverIdStr));
            } catch (Exception e) {
                log.warn("Could not extract cover ID from URL: {}", book.getCoverImageUrl());
            }
        }
        
        // Set page count
        entity.setNumberOfPagesMedian(book.getPageCount());
        
        // Set genres if not empty
        if (book.getGenres() != null && !book.getGenres().isEmpty()) {
            entity.setGenres(new ArrayList<>(book.getGenres()));
        }
        
        // Set language
        if (book.getLanguage() != null) {
            List<String> languages = new ArrayList<>();
            languages.add(book.getLanguage());
            entity.setLanguages(languages);
        }
        
        // Set publication date
        if (book.getPublicationDate() != null) {
            entity.setFirstPublishYear(book.getPublicationDate().getYear());
        }
        
        // Set publisher
        if (book.getPublisher() != null) {
            List<String> publishers = new ArrayList<>();
            publishers.add(book.getPublisher());
            entity.setPublishers(publishers);
        }
        
        // Set OpenLibrary key
        entity.setOpenLibraryKey(book.getOpenLibraryKey());
        
        // Set platform type
        entity.setPlatform("system");
        
        // Set timestamps
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(book.getCreatedAt());
        }
        
        entity.setUpdatedAt(book.getUpdatedAt() != null ? 
                book.getUpdatedAt() : LocalDateTime.now());
        
        return entity;
    }
    
    /**
     * Maps a JPA entity to a domain Book entity.
     */
    private Book mapToDomainEntity(BookJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // Handle null values with defaults
        String isbn = entity.getIsbns() != null && !entity.getIsbns().isEmpty() ? entity.getIsbns().get(0) : null;
        
        String language = null;
        if (entity.getLanguages() != null && !entity.getLanguages().isEmpty()) {
            language = entity.getLanguages().get(0);
        }
        
        String publisher = null;
        if (entity.getPublishers() != null && !entity.getPublishers().isEmpty()) {
            publisher = entity.getPublishers().get(0);
        }
        
        // Extract publication date
        LocalDate publicationDate = entity.getFirstPublishYear() != null 
                ? LocalDate.of(entity.getFirstPublishYear(), 1, 1) 
                : null;
        
        // Ensure timestamps are never null
        LocalDateTime createdAt = entity.getCreatedAt() != null 
                ? entity.getCreatedAt() 
                : LocalDateTime.now();
                
        LocalDateTime updatedAt = entity.getUpdatedAt() != null 
                ? entity.getUpdatedAt() 
                : LocalDateTime.now();
        
        return Book.reconstitute(
                BookId.of(entity.getId()),
                entity.getTitle(),
                entity.getAuthor(),
                isbn,
                entity.getFirstSentence(), // Using first_sentence as description
                entity.getCoverUrl(),
                entity.getNumberOfPagesMedian(),
                entity.getGenres(),
                language,
                publicationDate,
                publisher,
                entity.getOpenLibraryKey(),
                createdAt,
                updatedAt
        );
    }
} 