package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.exception.BookPersistenceException;
import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.CreateBookUseCase.CreateBookCommand;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CreateBookService createBookService;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    private CreateBookCommand createCommand;
    private final String title = "Test Book";
    private final String author = "Test Author";
    private final String isbn = "9781234567890";
    private final String description = "Test description";
    private final String coverImageUrl = "https://example.com/cover.jpg";
    private final Integer pageCount = 300;
    private final List<String> genres = Arrays.asList("Fiction", "Mystery");
    private final String language = "English";
    private final LocalDate publicationDate = LocalDate.of(2023, 1, 1);
    private final String publisher = "Test Publisher";
    private final BookId bookId = BookId.generate();

    @BeforeEach
    void setUp() {
        createCommand = new CreateBookCommand(
                title, author, isbn, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher
        );
    }

    @Test
    @DisplayName("Should create a new book with valid command")
    void createBook_WithValidCommand_ShouldCreateBook() {
        // Arrange
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            // Simulate setting the ID by the repository
            ReflectionTestUtils.setField(savedBook, "id", bookId);
            return savedBook;
        });

        // Act
        BookId resultId = createBookService.createBook(createCommand);

        // Assert
        verify(bookRepository).existsByIsbn(isbn);
        verify(bookRepository).save(bookCaptor.capture());
        
        Book capturedBook = bookCaptor.getValue();
        assertNotNull(resultId);
        assertEquals(title, capturedBook.getTitle());
        assertEquals(author, capturedBook.getAuthor());
        assertEquals(isbn, capturedBook.getIsbn());
        assertEquals(description, capturedBook.getDescription());
        assertEquals(coverImageUrl, capturedBook.getCoverImageUrl());
        assertEquals(pageCount, capturedBook.getPageCount());
        assertEquals(genres, capturedBook.getGenres());
        assertEquals(language, capturedBook.getLanguage());
        assertEquals(publicationDate, capturedBook.getPublicationDate());
        assertEquals(publisher, capturedBook.getPublisher());
        assertNull(capturedBook.getOpenLibraryKey()); // No OpenLibrary key for manually created books
    }

    @Test
    @DisplayName("Should throw exception when book with ISBN already exists")
    void createBook_WithExistingIsbn_ShouldThrowException() {
        // Arrange
        when(bookRepository.existsByIsbn(isbn)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBookService.createBook(createCommand)
        );

        verify(bookRepository).existsByIsbn(isbn);
        verify(bookRepository, never()).save(any(Book.class));
        
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("Should not check ISBN existence when ISBN is null")
    void createBook_WithNullIsbn_ShouldNotCheckExistence() {
        // Arrange
        CreateBookCommand commandWithNullIsbn = new CreateBookCommand(
                title, author, null, description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher
        );

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedBook, "id", bookId);
            return savedBook;
        });

        // Act
        BookId resultId = createBookService.createBook(commandWithNullIsbn);

        // Assert
        verify(bookRepository, never()).existsByIsbn(any());
        verify(bookRepository).save(any(Book.class));
        assertNotNull(resultId);
    }

    @Test
    @DisplayName("Should not check ISBN existence when ISBN is blank")
    void createBook_WithBlankIsbn_ShouldNotCheckExistence() {
        // Arrange
        CreateBookCommand commandWithBlankIsbn = new CreateBookCommand(
                title, author, "", description, coverImageUrl,
                pageCount, genres, language, publicationDate, publisher
        );

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedBook, "id", bookId);
            return savedBook;
        });

        // Act
        BookId resultId = createBookService.createBook(commandWithBlankIsbn);

        // Assert
        verify(bookRepository, never()).existsByIsbn(any());
        verify(bookRepository).save(any(Book.class));
        assertNotNull(resultId);
    }

    @Test
    @DisplayName("Should propagate BookPersistenceException")
    void createBook_WithBookPersistenceException_ShouldPropagateException() {
        // Arrange
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenThrow(new BookPersistenceException("Error saving book"));

        // Act & Assert
        BookPersistenceException exception = assertThrows(
                BookPersistenceException.class,
                () -> createBookService.createBook(createCommand)
        );

        verify(bookRepository).existsByIsbn(isbn);
        verify(bookRepository).save(any(Book.class));
        
        assertEquals("Error saving book", exception.getMessage());
    }

    @Test
    @DisplayName("Should wrap other exceptions in RuntimeException")
    void createBook_WithOtherException_ShouldWrapInRuntimeException() {
        // Arrange
        when(bookRepository.existsByIsbn(isbn)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenThrow(new IllegalStateException("Database error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createBookService.createBook(createCommand)
        );

        verify(bookRepository).existsByIsbn(isbn);
        verify(bookRepository).save(any(Book.class));
        
        assertTrue(exception.getMessage().contains("Failed to create book"));
        assertTrue(exception.getCause() instanceof IllegalStateException);
    }
} 