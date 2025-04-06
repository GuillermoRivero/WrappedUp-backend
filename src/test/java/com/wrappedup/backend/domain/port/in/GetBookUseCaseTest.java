package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetBookUseCaseTest {
    
    @Test
    @DisplayName("Should call getBookById with the provided BookId")
    void getBookById_ShouldCallWithProvidedBookId() {
        // Create a mock implementation of the interface
        GetBookUseCase useCase = Mockito.mock(GetBookUseCase.class);
        
        // Create a test BookId
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Mock a book response
        Book mockBook = Mockito.mock(Book.class);
        when(useCase.getBookById(bookId)).thenReturn(Optional.of(mockBook));
        
        // Call the method
        Optional<Book> result = useCase.getBookById(bookId);
        
        // Verify the method was called with the correct BookId
        verify(useCase).getBookById(bookId);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockBook, result.get());
    }
    
    @Test
    @DisplayName("Should call getBookByIsbn with the provided ISBN")
    void getBookByIsbn_ShouldCallWithProvidedIsbn() {
        // Create a mock implementation of the interface
        GetBookUseCase useCase = Mockito.mock(GetBookUseCase.class);
        
        // Create a test ISBN
        String isbn = "1234567890";
        
        // Mock a book response
        Book mockBook = Mockito.mock(Book.class);
        when(useCase.getBookByIsbn(isbn)).thenReturn(Optional.of(mockBook));
        
        // Call the method
        Optional<Book> result = useCase.getBookByIsbn(isbn);
        
        // Verify the method was called with the correct ISBN
        verify(useCase).getBookByIsbn(isbn);
        
        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(mockBook, result.get());
    }
    
    @Test
    @DisplayName("Should call searchBooksByTitle with the provided title text")
    void searchBooksByTitle_ShouldCallWithProvidedTitleText() {
        // Create a mock implementation of the interface
        GetBookUseCase useCase = Mockito.mock(GetBookUseCase.class);
        
        // Create a test title search
        String titleText = "Harry Potter";
        
        // Mock books response
        Book mockBook1 = Mockito.mock(Book.class);
        Book mockBook2 = Mockito.mock(Book.class);
        List<Book> mockBooks = Arrays.asList(mockBook1, mockBook2);
        when(useCase.searchBooksByTitle(titleText)).thenReturn(mockBooks);
        
        // Call the method
        List<Book> result = useCase.searchBooksByTitle(titleText);
        
        // Verify the method was called with the correct title text
        verify(useCase).searchBooksByTitle(titleText);
        
        // Verify the result
        assertEquals(2, result.size());
        assertEquals(mockBooks, result);
    }
    
    @Test
    @DisplayName("Should call searchBooksByAuthor with the provided author text")
    void searchBooksByAuthor_ShouldCallWithProvidedAuthorText() {
        // Create a mock implementation of the interface
        GetBookUseCase useCase = Mockito.mock(GetBookUseCase.class);
        
        // Create a test author search
        String authorText = "J.K. Rowling";
        
        // Mock books response
        Book mockBook = Mockito.mock(Book.class);
        List<Book> mockBooks = Collections.singletonList(mockBook);
        when(useCase.searchBooksByAuthor(authorText)).thenReturn(mockBooks);
        
        // Call the method
        List<Book> result = useCase.searchBooksByAuthor(authorText);
        
        // Verify the method was called with the correct author text
        verify(useCase).searchBooksByAuthor(authorText);
        
        // Verify the result
        assertEquals(1, result.size());
        assertEquals(mockBooks, result);
    }
    
    @Test
    @DisplayName("Should call getBooksByGenre with the provided genre")
    void getBooksByGenre_ShouldCallWithProvidedGenre() {
        // Create a mock implementation of the interface
        GetBookUseCase useCase = Mockito.mock(GetBookUseCase.class);
        
        // Create a test genre
        String genre = "Fantasy";
        
        // Mock books response
        Book mockBook1 = Mockito.mock(Book.class);
        Book mockBook2 = Mockito.mock(Book.class);
        Book mockBook3 = Mockito.mock(Book.class);
        List<Book> mockBooks = Arrays.asList(mockBook1, mockBook2, mockBook3);
        when(useCase.getBooksByGenre(genre)).thenReturn(mockBooks);
        
        // Call the method
        List<Book> result = useCase.getBooksByGenre(genre);
        
        // Verify the method was called with the correct genre
        verify(useCase).getBooksByGenre(genre);
        
        // Verify the result
        assertEquals(3, result.size());
        assertEquals(mockBooks, result);
    }
    
    @Test
    @DisplayName("Should call getAllBooks with the provided pagination parameters")
    void getAllBooks_ShouldCallWithProvidedPaginationParameters() {
        // Create a mock implementation of the interface
        GetBookUseCase useCase = Mockito.mock(GetBookUseCase.class);
        
        // Create test pagination parameters
        int page = 0;
        int size = 10;
        
        // Mock books response
        Book mockBook = Mockito.mock(Book.class);
        List<Book> mockBooks = Collections.singletonList(mockBook);
        when(useCase.getAllBooks(page, size)).thenReturn(mockBooks);
        
        // Call the method
        List<Book> result = useCase.getAllBooks(page, size);
        
        // Verify the method was called with the correct pagination parameters
        verify(useCase).getAllBooks(page, size);
        
        // Verify the result
        assertEquals(1, result.size());
        assertEquals(mockBooks, result);
    }
    
    @Test
    @DisplayName("Should call countBooks")
    void countBooks_ShouldReturnCount() {
        // Create a mock implementation of the interface
        GetBookUseCase useCase = Mockito.mock(GetBookUseCase.class);
        
        // Mock count response
        long count = 42;
        when(useCase.countBooks()).thenReturn(count);
        
        // Call the method
        long result = useCase.countBooks();
        
        // Verify the method was called
        verify(useCase).countBooks();
        
        // Verify the result
        assertEquals(count, result);
    }
} 