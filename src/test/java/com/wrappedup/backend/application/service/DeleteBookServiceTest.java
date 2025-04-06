package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.out.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private DeleteBookService deleteBookService;

    private BookId bookId;
    private Book book;

    @BeforeEach
    void setUp() {
        bookId = BookId.generate();
        book = Book.createNewBook(
                "Test Book",
                "Test Author",
                "9781234567890",
                "Test Description",
                "https://example.com/cover.jpg",
                300,
                Arrays.asList("Fiction", "Mystery"),
                "English",
                LocalDate.of(2023, 1, 1),
                "Test Publisher",
                null
        );
        ReflectionTestUtils.setField(book, "id", bookId);
    }

    @Test
    @DisplayName("Should delete book when it exists")
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).deleteById(bookId);

        // Act
        deleteBookService.deleteBook(bookId);

        // Assert
        verify(bookRepository).findById(bookId);
        verify(bookRepository).deleteById(bookId);
    }

    @Test
    @DisplayName("Should throw exception when book doesn't exist")
    void deleteBook_WhenBookDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deleteBookService.deleteBook(bookId)
        );

        verify(bookRepository).findById(bookId);
        verify(bookRepository, never()).deleteById(any());
        
        assertTrue(exception.getMessage().contains("Book not found"));
    }

    @Test
    @DisplayName("Should wrap repository exceptions in RuntimeException")
    void deleteBook_WhenRepositoryThrowsException_ShouldWrapInRuntimeException() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        doThrow(new RuntimeException("Database error")).when(bookRepository).deleteById(bookId);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deleteBookService.deleteBook(bookId)
        );

        verify(bookRepository).findById(bookId);
        verify(bookRepository).deleteById(bookId);
        
        assertTrue(exception.getMessage().contains("Failed to delete book"));
        assertTrue(exception.getCause() instanceof RuntimeException);
    }
} 