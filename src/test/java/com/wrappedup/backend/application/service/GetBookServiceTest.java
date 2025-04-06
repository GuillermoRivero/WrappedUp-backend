package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.out.BookRepository;
import com.wrappedup.backend.domain.port.out.OpenLibraryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OpenLibraryPort openLibraryPort;

    @InjectMocks
    private GetBookService getBookService;

    private Book testBook;
    private BookId bookId;
    private final String ISBN = "9781234567890";
    private final String TITLE = "Test Book";
    private final String AUTHOR = "Test Author";

    @BeforeEach
    void setUp() {
        bookId = BookId.generate();
        LocalDateTime now = LocalDateTime.now();
        testBook = Book.reconstitute(
                bookId,
                TITLE,
                AUTHOR,
                ISBN,
                "Test description",
                "http://test-cover.jpg",
                200,
                Arrays.asList("Fiction", "Mystery"),
                "English",
                LocalDate.of(2022, 1, 1),
                "Test Publisher",
                "OL12345",
                now,
                now
        );
    }

    @Test
    @DisplayName("Should return book when found by ID")
    void getBookById_WhenBookExists_ShouldReturnBook() {
        // Arrange
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // Act
        Optional<Book> result = getBookService.getBookById(bookId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBook, result.get());
    }

    @Test
    @DisplayName("Should return empty when book not found by ID")
    void getBookById_WhenBookDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(bookRepository.findById(any(BookId.class))).thenReturn(Optional.empty());

        // Act
        Optional<Book> result = getBookService.getBookById(bookId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return book when found by ISBN")
    void getBookByIsbn_WhenBookExists_ShouldReturnBook() {
        // Arrange
        when(bookRepository.findByIsbn(ISBN)).thenReturn(Optional.of(testBook));

        // Act
        Optional<Book> result = getBookService.getBookByIsbn(ISBN);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBook, result.get());
    }

    @Test
    @DisplayName("Should return empty for null or blank ISBN")
    void getBookByIsbn_WithNullOrBlankIsbn_ShouldReturnEmpty() {
        // Act & Assert
        assertTrue(getBookService.getBookByIsbn(null).isEmpty());
        assertTrue(getBookService.getBookByIsbn("").isEmpty());
        assertTrue(getBookService.getBookByIsbn("  ").isEmpty());
        
        // Verify repository was not called
        verify(bookRepository, never()).findByIsbn(anyString());
    }

    @Test
    @DisplayName("Should return books when searching by title")
    void searchBooksByTitle_WhenBooksFound_ShouldReturnBooks() {
        // Arrange
        List<Book> books = Collections.singletonList(testBook);
        when(bookRepository.findByTitleContaining("Test")).thenReturn(books);

        // Act
        List<Book> result = getBookService.searchBooksByTitle("Test");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list for null or blank title")
    void searchBooksByTitle_WithNullOrBlankTitle_ShouldReturnEmptyList() {
        // Act & Assert
        assertTrue(getBookService.searchBooksByTitle(null).isEmpty());
        assertTrue(getBookService.searchBooksByTitle("").isEmpty());
        assertTrue(getBookService.searchBooksByTitle("  ").isEmpty());
        
        // Verify repository was not called
        verify(bookRepository, never()).findByTitleContaining(anyString());
    }

    @Test
    @DisplayName("Should return books when searching by author")
    void searchBooksByAuthor_WhenBooksFound_ShouldReturnBooks() {
        // Arrange
        List<Book> books = Collections.singletonList(testBook);
        when(bookRepository.findByAuthorContaining("Author")).thenReturn(books);

        // Act
        List<Book> result = getBookService.searchBooksByAuthor("Author");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list for null or blank author")
    void searchBooksByAuthor_WithNullOrBlankAuthor_ShouldReturnEmptyList() {
        // Act & Assert
        assertTrue(getBookService.searchBooksByAuthor(null).isEmpty());
        assertTrue(getBookService.searchBooksByAuthor("").isEmpty());
        assertTrue(getBookService.searchBooksByAuthor("  ").isEmpty());
        
        // Verify repository was not called
        verify(bookRepository, never()).findByAuthorContaining(anyString());
    }

    @Test
    @DisplayName("Should return books when searching by genre")
    void getBooksByGenre_WhenBooksFound_ShouldReturnBooks() {
        // Arrange
        List<Book> books = Collections.singletonList(testBook);
        when(bookRepository.findByGenre("Fiction")).thenReturn(books);

        // Act
        List<Book> result = getBookService.getBooksByGenre("Fiction");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list for null or blank genre")
    void getBooksByGenre_WithNullOrBlankGenre_ShouldReturnEmptyList() {
        // Act & Assert
        assertTrue(getBookService.getBooksByGenre(null).isEmpty());
        assertTrue(getBookService.getBooksByGenre("").isEmpty());
        assertTrue(getBookService.getBooksByGenre("  ").isEmpty());
        
        // Verify repository was not called
        verify(bookRepository, never()).findByGenre(anyString());
    }

    @Test
    @DisplayName("Should return books with pagination")
    void getAllBooks_WithValidPagination_ShouldReturnBooks() {
        // Arrange
        List<Book> books = Collections.singletonList(testBook);
        when(bookRepository.findAll(0, 10)).thenReturn(books);

        // Act
        List<Book> result = getBookService.getAllBooks(0, 10);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list for invalid pagination")
    void getAllBooks_WithInvalidPagination_ShouldReturnEmptyList() {
        // Act & Assert
        assertTrue(getBookService.getAllBooks(-1, 10).isEmpty());
        assertTrue(getBookService.getAllBooks(0, 0).isEmpty());
        assertTrue(getBookService.getAllBooks(0, -5).isEmpty());
        
        // Verify repository was not called
        verify(bookRepository, never()).findAll(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Should return count of books")
    void countBooks_ShouldReturnCount() {
        // Arrange
        when(bookRepository.count()).thenReturn(10L);

        // Act
        long result = getBookService.countBooks();

        // Assert
        assertEquals(10L, result);
    }

    @Test
    @DisplayName("Should return books from OpenLibrary when searching")
    void searchBooksInOpenLibrary_WithValidQuery_ShouldReturnBooks() {
        // Arrange
        List<Book> books = Collections.singletonList(testBook);
        when(openLibraryPort.searchBooks("Harry Potter")).thenReturn(books);

        // Act
        List<Book> result = getBookService.searchBooksInOpenLibrary("Harry Potter");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testBook, result.get(0));
    }

    @Test
    @DisplayName("Should return empty list for null or blank OpenLibrary query")
    void searchBooksInOpenLibrary_WithNullOrBlankQuery_ShouldReturnEmptyList() {
        // Act & Assert
        assertTrue(getBookService.searchBooksInOpenLibrary(null).isEmpty());
        assertTrue(getBookService.searchBooksInOpenLibrary("").isEmpty());
        assertTrue(getBookService.searchBooksInOpenLibrary("  ").isEmpty());
        
        // Verify port was not called
        verify(openLibraryPort, never()).searchBooks(anyString());
    }

    @Test
    @DisplayName("Should handle OpenLibrary search exceptions gracefully")
    void searchBooksInOpenLibrary_WhenExceptionOccurs_ShouldReturnEmptyList() {
        // Arrange
        when(openLibraryPort.searchBooks(anyString())).thenThrow(new RuntimeException("API error"));

        // Act
        List<Book> result = getBookService.searchBooksInOpenLibrary("Harry Potter");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should persist and return book from OpenLibrary")
    void getAndPersistBookByOpenLibraryKey_WhenBookNotInDatabase_ShouldPersistAndReturnBook() {
        // Arrange
        String openLibraryKey = "OL12345";
        when(bookRepository.findByOpenLibraryKey(openLibraryKey)).thenReturn(Optional.empty());
        when(openLibraryPort.getBookByKey(openLibraryKey)).thenReturn(Collections.singletonList(testBook));
        when(bookRepository.findByIsbn(testBook.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        Optional<Book> result = getBookService.getAndPersistBookByOpenLibraryKey(openLibraryKey);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBook, result.get());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Should return existing book when OpenLibrary key already exists in database")
    void getAndPersistBookByOpenLibraryKey_WhenBookExistsByKey_ShouldReturnExistingBook() {
        // Arrange
        String openLibraryKey = "OL12345";
        when(bookRepository.findByOpenLibraryKey(openLibraryKey)).thenReturn(Optional.of(testBook));

        // Act
        Optional<Book> result = getBookService.getAndPersistBookByOpenLibraryKey(openLibraryKey);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBook, result.get());
        verify(bookRepository, never()).save(any(Book.class));
        verify(openLibraryPort, never()).getBookByKey(anyString());
    }

    @Test
    @DisplayName("Should return existing book when ISBN already exists in database")
    void getAndPersistBookByOpenLibraryKey_WhenBookExistsByIsbn_ShouldReturnExistingBook() {
        // Arrange
        String openLibraryKey = "OL12345";
        when(bookRepository.findByOpenLibraryKey(openLibraryKey)).thenReturn(Optional.empty());
        when(openLibraryPort.getBookByKey(openLibraryKey)).thenReturn(Collections.singletonList(testBook));
        when(bookRepository.findByIsbn(testBook.getIsbn())).thenReturn(Optional.of(testBook));

        // Act
        Optional<Book> result = getBookService.getAndPersistBookByOpenLibraryKey(openLibraryKey);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testBook, result.get());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Should return empty for null or blank OpenLibrary key")
    void getAndPersistBookByOpenLibraryKey_WithNullOrBlankKey_ShouldReturnEmpty() {
        // Act & Assert
        assertTrue(getBookService.getAndPersistBookByOpenLibraryKey(null).isEmpty());
        assertTrue(getBookService.getAndPersistBookByOpenLibraryKey("").isEmpty());
        assertTrue(getBookService.getAndPersistBookByOpenLibraryKey("  ").isEmpty());
        
        // Verify repository and port were not called
        verify(bookRepository, never()).findByOpenLibraryKey(anyString());
        verify(openLibraryPort, never()).getBookByKey(anyString());
    }

    @Test
    @DisplayName("Should handle exceptions gracefully when retrieving from OpenLibrary")
    void getAndPersistBookByOpenLibraryKey_WhenExceptionOccurs_ShouldReturnEmpty() {
        // Arrange
        String openLibraryKey = "OL12345";
        when(bookRepository.findByOpenLibraryKey(openLibraryKey)).thenReturn(Optional.empty());
        when(openLibraryPort.getBookByKey(anyString())).thenThrow(new RuntimeException("API error"));

        // Act
        Optional<Book> result = getBookService.getAndPersistBookByOpenLibraryKey(openLibraryKey);

        // Assert
        assertTrue(result.isEmpty());
        verify(bookRepository, never()).save(any(Book.class));
    }
} 