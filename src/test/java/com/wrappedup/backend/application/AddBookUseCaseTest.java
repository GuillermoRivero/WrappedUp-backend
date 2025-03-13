package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddBookUseCaseTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private AddBookUseCase addBookUseCase;

    @Test
    void shouldAddBook() {
        // Given
        Book book = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        Book savedBook = addBookUseCase.execute(book);

        // Then
        assertEquals(book, savedBook);
        verify(bookRepository, times(1)).save(book);
    }
} 