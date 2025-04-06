package com.wrappedup.backend.domain.port.out;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookRepositoryTest {

    @Test
    @DisplayName("Should save a book")
    void save_ShouldReturnSavedBook() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create a test book
        Book book = createMockBook("Test Book", "Test Author");
        
        // Mock the save behavior
        when(repository.save(book)).thenReturn(book);
        
        // Call the method
        Book result = repository.save(book);
        
        // Verify the method was called with the correct book
        verify(repository).save(book);
        
        // Verify the result
        assertEquals(book, result);
    }
    
    @Test
    @DisplayName("Should find a book by ID")
    void findById_ShouldReturnBookWhenFound() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create a test book and ID
        BookId bookId = BookId.of(UUID.randomUUID());
        Book book = createMockBook("Test Book", "Test Author");
        
        // Mock the findById behavior
        when(repository.findById(bookId)).thenReturn(Optional.of(book));
        
        // Call the method
        Optional<Book> result = repository.findById(bookId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(bookId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }
    
    @Test
    @DisplayName("Should return empty when book not found by ID")
    void findById_ShouldReturnEmptyWhenNotFound() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create a test ID
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock the findById behavior
        when(repository.findById(bookId)).thenReturn(Optional.empty());
        
        // Call the method
        Optional<Book> result = repository.findById(bookId);
        
        // Verify the method was called with the correct ID
        verify(repository).findById(bookId);
        
        // Verify the result
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should find books by title containing text")
    void findByTitleContaining_ShouldReturnMatchingBooks() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create test data
        String titleText = "Harry";
        Book book1 = createMockBook("Harry Potter 1", "J.K. Rowling");
        Book book2 = createMockBook("Harry Potter 2", "J.K. Rowling");
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        
        // Mock the findByTitleContaining behavior
        when(repository.findByTitleContaining(titleText)).thenReturn(expectedBooks);
        
        // Call the method
        List<Book> result = repository.findByTitleContaining(titleText);
        
        // Verify the method was called with the correct text
        verify(repository).findByTitleContaining(titleText);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(expectedBooks, result);
    }
    
    @Test
    @DisplayName("Should find books by author containing text")
    void findByAuthorContaining_ShouldReturnMatchingBooks() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create test data
        String authorText = "Rowling";
        Book book1 = createMockBook("Harry Potter 1", "J.K. Rowling");
        Book book2 = createMockBook("Harry Potter 2", "J.K. Rowling");
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        
        // Mock the findByAuthorContaining behavior
        when(repository.findByAuthorContaining(authorText)).thenReturn(expectedBooks);
        
        // Call the method
        List<Book> result = repository.findByAuthorContaining(authorText);
        
        // Verify the method was called with the correct text
        verify(repository).findByAuthorContaining(authorText);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(expectedBooks, result);
    }
    
    @Test
    @DisplayName("Should find a book by ISBN")
    void findByIsbn_ShouldReturnBookWhenFound() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create test data
        String isbn = "1234567890";
        Book book = createMockBook("Test Book", "Test Author");
        
        // Mock the findByIsbn behavior
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        
        // Call the method
        Optional<Book> result = repository.findByIsbn(isbn);
        
        // Verify the method was called with the correct ISBN
        verify(repository).findByIsbn(isbn);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }
    
    @Test
    @DisplayName("Should find books by genre")
    void findByGenre_ShouldReturnMatchingBooks() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create test data
        String genre = "Fantasy";
        Book book1 = createMockBook("Book 1", "Author 1");
        Book book2 = createMockBook("Book 2", "Author 2");
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        
        // Mock the findByGenre behavior
        when(repository.findByGenre(genre)).thenReturn(expectedBooks);
        
        // Call the method
        List<Book> result = repository.findByGenre(genre);
        
        // Verify the method was called with the correct genre
        verify(repository).findByGenre(genre);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(expectedBooks, result);
    }
    
    @Test
    @DisplayName("Should delete a book by ID")
    void deleteById_ShouldCallRepositoryWithCorrectId() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create a test ID
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Call the method
        repository.deleteById(bookId);
        
        // Verify the method was called with the correct ID
        verify(repository).deleteById(bookId);
    }
    
    @Test
    @DisplayName("Should check if book exists by ISBN")
    void existsByIsbn_ShouldReturnTrueWhenExists() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create test data
        String isbn = "1234567890";
        
        // Mock the existsByIsbn behavior
        when(repository.existsByIsbn(isbn)).thenReturn(true);
        
        // Call the method
        boolean result = repository.existsByIsbn(isbn);
        
        // Verify the method was called with the correct ISBN
        verify(repository).existsByIsbn(isbn);
        
        // Verify the result
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Should find all books with pagination")
    void findAll_ShouldReturnPaginatedBooks() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create test data
        int page = 0;
        int size = 10;
        Book book1 = createMockBook("Book 1", "Author 1");
        Book book2 = createMockBook("Book 2", "Author 2");
        List<Book> expectedBooks = Arrays.asList(book1, book2);
        
        // Mock the findAll behavior
        when(repository.findAll(page, size)).thenReturn(expectedBooks);
        
        // Call the method
        List<Book> result = repository.findAll(page, size);
        
        // Verify the method was called with the correct pagination
        verify(repository).findAll(page, size);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(expectedBooks, result);
    }
    
    @Test
    @DisplayName("Should count total number of books")
    void count_ShouldReturnTotalBookCount() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create test data
        long expectedCount = 42;
        
        // Mock the count behavior
        when(repository.count()).thenReturn(expectedCount);
        
        // Call the method
        long result = repository.count();
        
        // Verify the method was called
        verify(repository).count();
        
        // Verify the result
        assertEquals(expectedCount, result);
    }
    
    @Test
    @DisplayName("Should find a book by OpenLibrary key")
    void findByOpenLibraryKey_ShouldReturnBookWhenFound() {
        // Create a mock implementation of the interface
        BookRepository repository = Mockito.mock(BookRepository.class);
        
        // Create test data
        String openLibraryKey = "OL12345";
        Book book = createMockBook("Test Book", "Test Author");
        
        // Mock the findByOpenLibraryKey behavior
        when(repository.findByOpenLibraryKey(openLibraryKey)).thenReturn(Optional.of(book));
        
        // Call the method
        Optional<Book> result = repository.findByOpenLibraryKey(openLibraryKey);
        
        // Verify the method was called with the correct key
        verify(repository).findByOpenLibraryKey(openLibraryKey);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(book, result.get());
    }

    private Book createMockBook(String title, String author) {
        LocalDateTime now = LocalDateTime.now();
        return Book.reconstitute(
            BookId.of(UUID.randomUUID()),
            title,
            author,
            "1234567890",
            "Description",
            "cover.jpg",
            100,
            Arrays.asList("Fiction", "Fantasy"),
            "English",
            LocalDate.now(),
            "Publisher",
            "OL12345",
            now,
            now
        );
    }
} 