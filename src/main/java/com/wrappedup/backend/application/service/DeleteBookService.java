package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.DeleteBookUseCase;
import com.wrappedup.backend.domain.port.out.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation of the DeleteBookUseCase.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteBookService implements DeleteBookUseCase {
    
    private final BookRepository bookRepository;
    
    @Override
    @Transactional
    public void deleteBook(BookId id) {
        log.info("Deleting book with ID: {}", id);
        
        // Check if book exists before deletion
        Optional<Book> bookToDelete = bookRepository.findById(id);
        if (bookToDelete.isEmpty()) {
            log.warn("Attempted to delete non-existent book with ID: {}", id);
            throw new IllegalArgumentException("Book not found with ID: " + id);
        }
        
        try {
            bookRepository.deleteById(id);
            log.info("Successfully deleted book with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting book with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete book: " + e.getMessage(), e);
        }
    }
} 