package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.UpdateBookUseCase.UpdateBookCommand;
import com.wrappedup.backend.domain.port.out.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private UpdateBookService updateBookService;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    private BookId bookId;
    private Book existingBook;
    private UpdateBookCommand updateCommand;
    
    private final String originalTitle = "Original Title";
    private final String originalAuthor = "Original Author";
    private final String originalIsbn = "9781234567890";
    private final String originalDescription = "Original description";
    private final String originalCoverUrl = "https://example.com/original-cover.jpg";
    private final Integer originalPageCount = 300;
    private final List<String> originalGenres = Arrays.asList("Fiction", "Mystery");
    private final String originalLanguage = "English";
    private final LocalDate originalPublicationDate = LocalDate.of(2020, 1, 1);
    private final String originalPublisher = "Original Publisher";
    
    private final String newTitle = "Updated Title";
    private final String newAuthor = "Updated Author";
    private final String newIsbn = "9789876543210";
    private final String newDescription = "Updated description";
    private final String newCoverUrl = "https://example.com/updated-cover.jpg";
    private final Integer newPageCount = 350;
    private final List<String> newGenres = Arrays.asList("Science Fiction", "Thriller");
    private final String newLanguage = "Spanish";
    private final LocalDate newPublicationDate = LocalDate.of(2023, 2, 2);
    private final String newPublisher = "Updated Publisher";

    @BeforeEach
    void setUp() {
        bookId = BookId.generate();
        
        // Create existing book
        existingBook = Book.createNewBook(
                originalTitle,
                originalAuthor,
                originalIsbn,
                originalDescription,
                originalCoverUrl,
                originalPageCount,
                originalGenres,
                originalLanguage,
                originalPublicationDate,
                originalPublisher,
                null
        );
        ReflectionTestUtils.setField(existingBook, "id", bookId);
        LocalDateTime createdAt = LocalDateTime.now().minusDays(10);
        LocalDateTime updatedAt = createdAt;
        ReflectionTestUtils.setField(existingBook, "createdAt", createdAt);
        ReflectionTestUtils.setField(existingBook, "updatedAt", updatedAt);
        
        // Create update command with all fields
        updateCommand = new UpdateBookCommand(
                bookId,
                newTitle,
                newAuthor,
                newIsbn,
                newDescription,
                newCoverUrl,
                newPageCount,
                newGenres,
                newLanguage,
                newPublicationDate,
                newPublisher
        );
    }

    @Test
    @DisplayName("Should update book with all new values")
    void updateBook_WithAllFields_ShouldUpdateAllValues() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.findByIsbn(newIsbn)).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Book updatedBook = updateBookService.updateBook(updateCommand);

        // Assert
        verify(bookRepository).findById(bookId);
        verify(bookRepository).findByIsbn(newIsbn);
        verify(bookRepository).save(bookCaptor.capture());

        Book capturedBook = bookCaptor.getValue();
        assertEquals(bookId, capturedBook.getId());
        assertEquals(newTitle, capturedBook.getTitle());
        assertEquals(newAuthor, capturedBook.getAuthor());
        assertEquals(newIsbn, capturedBook.getIsbn());
        assertEquals(newDescription, capturedBook.getDescription());
        assertEquals(newCoverUrl, capturedBook.getCoverImageUrl());
        assertEquals(newPageCount, capturedBook.getPageCount());
        assertEquals(newGenres, capturedBook.getGenres());
        assertEquals(newLanguage, capturedBook.getLanguage());
        assertEquals(newPublicationDate, capturedBook.getPublicationDate());
        assertEquals(newPublisher, capturedBook.getPublisher());
        assertNull(capturedBook.getOpenLibraryKey());
        assertEquals(existingBook.getCreatedAt(), capturedBook.getCreatedAt());
        assertEquals(existingBook.getUpdatedAt(), capturedBook.getUpdatedAt());
    }

    @Test
    @DisplayName("Should keep existing values when update command has null fields")
    void updateBook_WithPartialFields_ShouldKeepExistingValues() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateBookCommand partialCommand = new UpdateBookCommand(
                bookId,
                null,           // Keep original title
                newAuthor,
                null,           // Keep original ISBN
                newDescription,
                null,           // Keep original cover URL
                null,           // Keep original page count
                null,           // Keep original genres
                newLanguage,
                null,           // Keep original publication date
                null            // Keep original publisher
        );

        // Act
        Book updatedBook = updateBookService.updateBook(partialCommand);

        // Assert
        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).findByIsbn(any());
        verify(bookRepository).save(bookCaptor.capture());

        Book capturedBook = bookCaptor.getValue();
        assertEquals(bookId, capturedBook.getId());
        assertEquals(originalTitle, capturedBook.getTitle());
        assertEquals(newAuthor, capturedBook.getAuthor());
        assertEquals(originalIsbn, capturedBook.getIsbn());
        assertEquals(newDescription, capturedBook.getDescription());
        assertEquals(originalCoverUrl, capturedBook.getCoverImageUrl());
        assertEquals(originalPageCount, capturedBook.getPageCount());
        assertEquals(originalGenres, capturedBook.getGenres());
        assertEquals(newLanguage, capturedBook.getLanguage());
        assertEquals(originalPublicationDate, capturedBook.getPublicationDate());
        assertEquals(originalPublisher, capturedBook.getPublisher());
    }

    @Test
    @DisplayName("Should throw exception when book is not found")
    void updateBook_WithNonExistingBook_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateBookService.updateBook(updateCommand)
        );

        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).findByIsbn(any());
        verify(bookRepository, never()).save(any());
        
        assertTrue(exception.getMessage().contains("Book not found"));
    }

    @Test
    @DisplayName("Should throw exception when ISBN is already used by another book")
    void updateBook_WithDuplicateIsbn_ShouldThrowException() {
        // Arrange
        Book otherBook = Book.createNewBook(
                "Other Book", "Other Author", newIsbn,
                "Other description", null, null, null, null, null, null, null
        );
        BookId otherId = BookId.generate();
        ReflectionTestUtils.setField(otherBook, "id", otherId);
        
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.findByIsbn(newIsbn)).thenReturn(Optional.of(otherBook));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> updateBookService.updateBook(updateCommand)
        );

        verify(bookRepository).findById(bookId);
        verify(bookRepository).findByIsbn(newIsbn);
        verify(bookRepository, never()).save(any());
        
        assertTrue(exception.getMessage().contains("ISBN is already in use"));
    }

    @Test
    @DisplayName("Should allow book to keep its own ISBN")
    void updateBook_WithSameIsbn_ShouldNotCheckDuplicates() {
        // Arrange
        UpdateBookCommand sameIsbnCommand = new UpdateBookCommand(
                bookId, newTitle, newAuthor, originalIsbn, newDescription,
                newCoverUrl, newPageCount, newGenres, newLanguage,
                newPublicationDate, newPublisher
        );
        
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Book updatedBook = updateBookService.updateBook(sameIsbnCommand);

        // Assert
        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).findByIsbn(any());
        verify(bookRepository).save(any(Book.class));
        
        assertEquals(originalIsbn, updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Should wrap repository exceptions in RuntimeException")
    void updateBook_WhenRepositoryThrowsException_ShouldWrapInRuntimeException() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.findByIsbn(newIsbn)).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> updateBookService.updateBook(updateCommand)
        );

        verify(bookRepository).findById(bookId);
        verify(bookRepository).findByIsbn(newIsbn);
        verify(bookRepository).save(any(Book.class));
        
        assertTrue(exception.getMessage().contains("Failed to update book"));
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should handle empty genres list")
    void updateBook_WithEmptyGenresList_ShouldKeepExistingGenres() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateBookCommand emptyGenresCommand = new UpdateBookCommand(
                bookId, newTitle, newAuthor, newIsbn, newDescription,
                newCoverUrl, newPageCount, Collections.emptyList(), newLanguage,
                newPublicationDate, newPublisher
        );

        // Act
        Book updatedBook = updateBookService.updateBook(emptyGenresCommand);

        // Assert
        verify(bookRepository).save(bookCaptor.capture());
        Book capturedBook = bookCaptor.getValue();
        
        assertEquals(originalGenres, capturedBook.getGenres());
    }
} 