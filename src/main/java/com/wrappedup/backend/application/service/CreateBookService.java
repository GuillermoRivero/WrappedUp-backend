package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.exception.BookPersistenceException;
import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.CreateBookUseCase;
import com.wrappedup.backend.domain.port.out.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of the CreateBookUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBookService implements CreateBookUseCase {

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookId createBook(CreateBookCommand command) {
        log.debug("Creating new book with title: {}", command.title());
        
        try {
            // Check if a book with the same ISBN already exists (if ISBN is provided)
            if (command.isbn() != null && !command.isbn().isBlank() && 
                bookRepository.existsByIsbn(command.isbn())) {
                log.debug("Book with ISBN {} already exists", command.isbn());
                throw new IllegalArgumentException("A book with this ISBN already exists");
            }
            
            // Create a new domain book entity
            Book book = Book.createNewBook(
                    command.title(),
                    command.author(),
                    command.isbn(),
                    command.description(),
                    command.coverImageUrl(),
                    command.pageCount(),
                    command.genres(),
                    command.language(),
                    command.publicationDate(),
                    command.publisher(),
                    null // No OpenLibrary key for manually created books
            );
            
            // Save the book
            Book savedBook = bookRepository.save(book);
            log.info("Book created successfully with ID: {}", savedBook.getId());
            
            return savedBook.getId();
        } catch (BookPersistenceException | IllegalArgumentException e) {
            // Re-throw domain exceptions and IllegalArgumentException directly
            throw e;
        } catch (Exception e) {
            // Log and wrap all other exceptions
            log.error("Failed to create book: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create book due to an internal error", e);
        }
    }
} 