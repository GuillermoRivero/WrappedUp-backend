package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaBookRepositoryTest {

    @Mock
    private SpringDataBookRepository springDataBookRepository;

    @InjectMocks
    private JpaBookRepository jpaBookRepository;

    @Test
    void shouldSaveBook() {
        // Given
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        when(springDataBookRepository.save(any(Book.class))).thenReturn(book);

        // When
        Book savedBook = jpaBookRepository.save(book);

        // Then
        assertEquals(book, savedBook);
        verify(springDataBookRepository, times(1)).save(book);
    }

    @Test
    void shouldFindBookById() {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        when(springDataBookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        Optional<Book> foundBook = jpaBookRepository.findById(bookId);

        // Then
        assertTrue(foundBook.isPresent());
        assertEquals(book, foundBook.get());
        verify(springDataBookRepository, times(1)).findById(bookId);
    }

    @Test
    void shouldFindAllBooks() {
        // Given
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        List<Book> expectedBooks = Arrays.asList(book);
        when(springDataBookRepository.findAll()).thenReturn(expectedBooks);

        // When
        List<Book> foundBooks = jpaBookRepository.findAll();

        // Then
        assertEquals(expectedBooks, foundBooks);
        verify(springDataBookRepository, times(1)).findAll();
    }

    @Test
    void shouldSearchBooks() {
        // Given
        Book book = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", "OL123456W");
        List<Book> expectedBooks = Arrays.asList(book);
        String searchPattern = "%test%";
        when(springDataBookRepository.findByTitleLikeIgnoreCaseOrAuthorLikeIgnoreCase(searchPattern, searchPattern))
            .thenReturn(expectedBooks);

        // When
        List<Book> foundBooks = jpaBookRepository.search("test");

        // Then
        assertEquals(expectedBooks, foundBooks);
        verify(springDataBookRepository, times(1))
            .findByTitleLikeIgnoreCaseOrAuthorLikeIgnoreCase(searchPattern, searchPattern);
    }
} 