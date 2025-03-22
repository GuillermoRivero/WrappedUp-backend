package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {
    
    private final SpringDataBookRepository springDataBookRepository;

    @Override
    public Book save(Book book) {
        try {
            return springDataBookRepository.save(book);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            if (book.getId() != null) {
                Optional<Book> existingBook = springDataBookRepository.findById(book.getId());
                if (existingBook.isPresent()) {
                    return existingBook.get();
                }
            }
            
            if (book.getOpenLibraryKey() != null && !book.getOpenLibraryKey().isEmpty()) {
                Optional<Book> existingBook = springDataBookRepository.findByOpenLibraryKey(book.getOpenLibraryKey());
                if (existingBook.isPresent()) {
                    return existingBook.get();
                }
            }
            
            throw e;
        } catch (ObjectOptimisticLockingFailureException e) {
            if (book.getId() != null) {
                Optional<Book> existingBook = springDataBookRepository.findById(book.getId());
                if (existingBook.isPresent()) {
                    return existingBook.get();
                }
            }
            
            throw e;
        }
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return springDataBookRepository.findById(id);
    }

    @Override
    public Optional<Book> findByOpenLibraryKey(String openLibraryKey) {
        return springDataBookRepository.findByOpenLibraryKey(openLibraryKey);
    }

    @Override
    public List<Book> findAll() {
        return springDataBookRepository.findAll();
    }

    @Override
    public List<Book> search(String query) {
        String searchPattern = "%" + query.toLowerCase() + "%";
        return springDataBookRepository.findByTitleLikeIgnoreCaseOrAuthorLikeIgnoreCase(searchPattern, searchPattern);
    }

    @Override
    public List<Book> searchByTitleOrAuthor(String query) {
        return springDataBookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }
} 