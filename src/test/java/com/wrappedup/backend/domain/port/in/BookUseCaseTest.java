package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.in.BookUseCase.CreateBookCommand;
import com.wrappedup.backend.domain.port.in.BookUseCase.UpdateBookCommand;
import com.wrappedup.backend.domain.port.in.BookUseCase.SearchBooksCommand;
import com.wrappedup.backend.domain.port.in.BookUseCase.PaginatedBooksResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookUseCaseTest {

    @Test
    @DisplayName("Should throw exception when title is null in CreateBookCommand")
    void createBookCommand_WithNullTitle_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                null, "Author", "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher"
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when title is blank in CreateBookCommand")
    void createBookCommand_WithBlankTitle_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                "", "Author", "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher"
            )
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                "   ", "Author", "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher"
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when author is null in CreateBookCommand")
    void createBookCommand_WithNullAuthor_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                "Title", null, "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher"
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when author is blank in CreateBookCommand")
    void createBookCommand_WithBlankAuthor_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                "Title", "", "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher"
            )
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                "Title", "   ", "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher"
            )
        );
    }

    @Test
    @DisplayName("Should create CreateBookCommand with valid parameters")
    void createBookCommand_WithValidParameters_ShouldCreateInstance() {
        CreateBookCommand command = new CreateBookCommand(
            "Title", "Author", "1234567890", "Description", 
            "image.jpg", 100, Arrays.asList("Fiction", "Fantasy"), 
            "English", LocalDate.now(), "Publisher"
        );
        
        assertNotNull(command);
        assertEquals("Title", command.title());
        assertEquals("Author", command.author());
        assertEquals("1234567890", command.isbn());
        assertEquals("Description", command.description());
        assertEquals("image.jpg", command.coverImageUrl());
        assertEquals(100, command.pageCount());
        assertEquals(2, command.genres().size());
        assertEquals("English", command.language());
        assertNotNull(command.publicationDate());
        assertEquals("Publisher", command.publisher());
    }

    @Test
    @DisplayName("Should throw exception when bookId is null in UpdateBookCommand")
    void updateBookCommand_WithNullBookId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new UpdateBookCommand(
                null, "Title", "Author", "1234567890", 
                "Description", "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher"
            )
        );
    }

    @Test
    @DisplayName("Should create UpdateBookCommand with valid parameters")
    void updateBookCommand_WithValidParameters_ShouldCreateInstance() {
        BookId bookId = BookId.of(UUID.randomUUID());
        UpdateBookCommand command = new UpdateBookCommand(
            bookId, "Updated Title", "Updated Author", "1234567890", 
            "Updated Description", "new-image.jpg", 200, 
            Arrays.asList("Non-Fiction", "Science"), 
            "Spanish", LocalDate.now(), "New Publisher"
        );
        
        assertNotNull(command);
        assertEquals(bookId, command.bookId());
        assertEquals("Updated Title", command.title());
        assertEquals("Updated Author", command.author());
    }

    @Test
    @DisplayName("Should throw exception when page is negative in SearchBooksCommand")
    void searchBooksCommand_WithNegativePage_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new SearchBooksCommand("Harry Potter", "title", "Fantasy", -1, 10)
        );
    }

    @Test
    @DisplayName("Should throw exception when size is not positive in SearchBooksCommand")
    void searchBooksCommand_WithNonPositiveSize_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new SearchBooksCommand("Harry Potter", "title", "Fantasy", 0, 0)
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new SearchBooksCommand("Harry Potter", "title", "Fantasy", 0, -5)
        );
    }

    @Test
    @DisplayName("Should create SearchBooksCommand with valid parameters")
    void searchBooksCommand_WithValidParameters_ShouldCreateInstance() {
        SearchBooksCommand command = new SearchBooksCommand("Harry Potter", "title", "Fantasy", 0, 10);
        
        assertNotNull(command);
        assertEquals("Harry Potter", command.query());
        assertEquals("title", command.searchBy());
        assertEquals("Fantasy", command.genre());
        assertEquals(0, command.page());
        assertEquals(10, command.size());
    }

    @Test
    @DisplayName("Should create PaginatedBooksResult with valid parameters")
    void paginatedBooksResult_WithValidParameters_ShouldCreateInstance() {
        Book book1 = createMockBook("Book 1");
        Book book2 = createMockBook("Book 2");
        List<Book> books = Arrays.asList(book1, book2);
        
        PaginatedBooksResult result = new PaginatedBooksResult(books, 0, 10, 2, 1);
        
        assertNotNull(result);
        assertEquals(2, result.books().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
    }

    private Book createMockBook(String title) {
        LocalDateTime now = LocalDateTime.now();
        return Book.reconstitute(
            BookId.generate(),
            title,
            "Author",
            "1234567890",
            "Description",
            "image.jpg",
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