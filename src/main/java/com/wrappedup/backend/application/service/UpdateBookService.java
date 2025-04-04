package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.UpdateBookUseCase;
import com.wrappedup.backend.domain.port.out.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation of the UpdateBookUseCase.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateBookService implements UpdateBookUseCase {
    
    private final BookRepository bookRepository;
    
    @Override
    @Transactional
    public Book updateBook(UpdateBookCommand command) {
        log.info("Updating book with ID: {}", command.id());
        
        // Find the existing book
        Book existingBook = bookRepository.findById(command.id())
                .orElseThrow(() -> {
                    log.error("Book with ID {} not found during update", command.id());
                    return new IllegalArgumentException("Book not found with ID: " + command.id());
                });
        
        // Check for ISBN uniqueness if it's being changed
        if (command.isbn() != null && !command.isbn().equals(existingBook.getIsbn())) {
            Optional<Book> bookWithSameIsbn = bookRepository.findByIsbn(command.isbn());
            if (bookWithSameIsbn.isPresent() && !bookWithSameIsbn.get().getId().equals(command.id())) {
                log.error("Cannot update book - ISBN {} is already in use by another book", command.isbn());
                throw new IllegalArgumentException("ISBN is already in use by another book: " + command.isbn());
            }
        }
        
        // Create updated book instance
        Book updatedBook = Book.reconstitute(
            existingBook.getId(),
            command.title() != null ? command.title() : existingBook.getTitle(),
            command.author() != null ? command.author() : existingBook.getAuthor(),
            command.isbn() != null ? command.isbn() : existingBook.getIsbn(),
            command.description() != null ? command.description() : existingBook.getDescription(),
            command.coverImageUrl() != null ? command.coverImageUrl() : existingBook.getCoverImageUrl(),
            command.pageCount() != null ? command.pageCount() : existingBook.getPageCount(),
            command.genres() != null && !command.genres().isEmpty() ? command.genres() : existingBook.getGenres(),
            command.language() != null ? command.language() : existingBook.getLanguage(),
            command.publicationDate() != null ? command.publicationDate() : existingBook.getPublicationDate(),
            command.publisher() != null ? command.publisher() : existingBook.getPublisher(),
            existingBook.getOpenLibraryKey(),
            existingBook.getCreatedAt(),
            existingBook.getUpdatedAt()
        );
        
        // Save and return the updated book
        try {
            Book savedBook = bookRepository.save(updatedBook);
            log.info("Successfully updated book with ID: {}", savedBook.getId());
            return savedBook;
        } catch (Exception e) {
            log.error("Error updating book with ID {}: {}", command.id(), e.getMessage(), e);
            throw new RuntimeException("Failed to update book: " + e.getMessage(), e);
        }
    }
} 