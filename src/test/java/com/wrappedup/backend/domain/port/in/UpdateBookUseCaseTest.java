package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.UpdateBookUseCase.UpdateBookCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateBookUseCaseTest {
    
    @Test
    @DisplayName("Should throw exception when id is null in UpdateBookCommand")
    void updateBookCommand_WithNullId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateBookCommand(
                null, 
                "Updated Title", 
                "Updated Author", 
                "1234567890", 
                "Updated Description", 
                "cover.jpg", 
                200, 
                Arrays.asList("Fiction", "Fantasy"), 
                "English", 
                LocalDate.now(), 
                "Publisher", 
                "OL12345"
            )
        );
    }
    
    @Test
    @DisplayName("Should create UpdateBookCommand with valid parameters with openLibraryKey")
    void updateBookCommand_WithValidParametersWithOpenLibraryKey_ShouldCreateInstance() {
        BookId bookId = BookId.of(UUID.randomUUID());
        String title = "Updated Title";
        String author = "Updated Author";
        String isbn = "1234567890";
        String description = "Updated Description";
        String coverImageUrl = "cover.jpg";
        Integer pageCount = 200;
        List<String> genres = Arrays.asList("Fiction", "Fantasy");
        String language = "English";
        LocalDate publicationDate = LocalDate.now();
        String publisher = "Publisher";
        String openLibraryKey = "OL12345";
        
        UpdateBookCommand command = new UpdateBookCommand(
            bookId, title, author, isbn, description, coverImageUrl, 
            pageCount, genres, language, publicationDate, publisher, openLibraryKey
        );
        
        assertNotNull(command);
        assertEquals(bookId, command.id());
        assertEquals(title, command.title());
        assertEquals(author, command.author());
        assertEquals(isbn, command.isbn());
        assertEquals(description, command.description());
        assertEquals(coverImageUrl, command.coverImageUrl());
        assertEquals(pageCount, command.pageCount());
        assertEquals(genres, command.genres());
        assertEquals(language, command.language());
        assertEquals(publicationDate, command.publicationDate());
        assertEquals(publisher, command.publisher());
        assertEquals(openLibraryKey, command.openLibraryKey());
    }
    
    @Test
    @DisplayName("Should create UpdateBookCommand with valid parameters without openLibraryKey")
    void updateBookCommand_WithValidParametersWithoutOpenLibraryKey_ShouldCreateInstance() {
        BookId bookId = BookId.of(UUID.randomUUID());
        String title = "Updated Title";
        String author = "Updated Author";
        String isbn = "1234567890";
        String description = "Updated Description";
        String coverImageUrl = "cover.jpg";
        Integer pageCount = 200;
        List<String> genres = Arrays.asList("Fiction", "Fantasy");
        String language = "English";
        LocalDate publicationDate = LocalDate.now();
        String publisher = "Publisher";
        
        UpdateBookCommand command = new UpdateBookCommand(
            bookId, title, author, isbn, description, coverImageUrl, 
            pageCount, genres, language, publicationDate, publisher
        );
        
        assertNotNull(command);
        assertEquals(bookId, command.id());
        assertEquals(title, command.title());
        assertEquals(author, command.author());
        assertEquals(isbn, command.isbn());
        assertEquals(description, command.description());
        assertEquals(coverImageUrl, command.coverImageUrl());
        assertEquals(pageCount, command.pageCount());
        assertEquals(genres, command.genres());
        assertEquals(language, command.language());
        assertEquals(publicationDate, command.publicationDate());
        assertEquals(publisher, command.publisher());
        assertNull(command.openLibraryKey());
    }
    
    @Test
    @DisplayName("Should create UpdateBookCommand with minimum required parameters")
    void updateBookCommand_WithMinimumRequiredParameters_ShouldCreateInstance() {
        BookId bookId = BookId.of(UUID.randomUUID());
        
        UpdateBookCommand command = new UpdateBookCommand(
            bookId, null, null, null, null, null, 
            null, null, null, null, null
        );
        
        assertNotNull(command);
        assertEquals(bookId, command.id());
        assertNull(command.title());
        assertNull(command.author());
        assertNull(command.isbn());
        assertNull(command.description());
        assertNull(command.coverImageUrl());
        assertNull(command.pageCount());
        assertNull(command.genres());
        assertNull(command.language());
        assertNull(command.publicationDate());
        assertNull(command.publisher());
        assertNull(command.openLibraryKey());
    }
    
    @Test
    @DisplayName("Should call updateBook with the provided command")
    void updateBook_ShouldCallWithProvidedCommand() {
        // Create a mock implementation of the interface
        UpdateBookUseCase useCase = Mockito.mock(UpdateBookUseCase.class);
        
        // Create a test command
        BookId bookId = BookId.of(UUID.randomUUID());
        UpdateBookCommand command = new UpdateBookCommand(
            bookId, "Updated Title", "Updated Author", "1234567890", 
            "Updated Description", "cover.jpg", 200, 
            Arrays.asList("Fiction", "Fantasy"), "English", 
            LocalDate.now(), "Publisher", "OL12345"
        );
        
        // Mock a book response
        Book mockBook = Mockito.mock(Book.class);
        when(useCase.updateBook(command)).thenReturn(mockBook);
        
        // Call the method
        Book result = useCase.updateBook(command);
        
        // Verify the method was called with the correct command
        verify(useCase).updateBook(command);
        
        // Verify the result
        assertEquals(mockBook, result);
    }
} 