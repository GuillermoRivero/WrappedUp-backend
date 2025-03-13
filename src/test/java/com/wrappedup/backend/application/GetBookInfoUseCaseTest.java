package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBookInfoUseCaseTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private GetBookInfoUseCase getBookInfoUseCase;

    @Test
    void shouldGetBookById() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book expectedBook = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(expectedBook));

        // When
        Optional<Book> result = getBookInfoUseCase.execute(bookId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedBook, result.get());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void shouldGetAllBooks() {
        // Given
        List<Book> expectedBooks = Arrays.asList(
            new Book("Test Book 1", "Test Author", 2023, "Test Platform", "test-cover-1.jpg", "OL123456W"),
            new Book("Test Book 2", "Test Author", 2023, "Test Platform", "test-cover-2.jpg", "OL789012W")
        );
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        // When
        List<Book> result = getBookInfoUseCase.findAll();

        // Then
        assertEquals(expectedBooks, result);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldSearchBooks() {
        // Given
        String query = "Test Author";
        List<Book> expectedBooks = Arrays.asList(
            new Book("Test Book 1", "Test Author", 2023, "Test Platform", "test-cover-1.jpg", "OL123456W"),
            new Book("Test Book 2", "Test Author", 2023, "Test Platform", "test-cover-2.jpg", "OL789012W")
        );
        when(bookRepository.searchByTitleOrAuthor(query)).thenReturn(expectedBooks);

        // When
        List<Book> result = getBookInfoUseCase.searchBooks(query);

        // Then
        assertEquals(expectedBooks, result);
        verify(bookRepository, times(1)).searchByTitleOrAuthor(query);
    }
} 