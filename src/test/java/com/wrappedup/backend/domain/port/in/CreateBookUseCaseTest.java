package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.port.in.CreateBookUseCase.CreateBookCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CreateBookUseCaseTest {

    @Test
    @DisplayName("Should throw exception when title is null in CreateBookCommand")
    void createBookCommand_WithNullTitle_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                null, "Author", "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher", "OL12345"
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
                "English", LocalDate.now(), "Publisher", "OL12345"
            )
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                "   ", "Author", "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher", "OL12345"
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
                "English", LocalDate.now(), "Publisher", "OL12345"
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
                "English", LocalDate.now(), "Publisher", "OL12345"
            )
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateBookCommand(
                "Title", "   ", "1234567890", "Description", 
                "image.jpg", 100, Collections.emptyList(), 
                "English", LocalDate.now(), "Publisher", "OL12345"
            )
        );
    }

    @Test
    @DisplayName("Should create CreateBookCommand with valid parameters and OpenLibraryKey")
    void createBookCommand_WithValidParametersAndOpenLibraryKey_ShouldCreateInstance() {
        CreateBookCommand command = new CreateBookCommand(
            "Title", "Author", "1234567890", "Description", 
            "image.jpg", 100, Arrays.asList("Fiction", "Fantasy"), 
            "English", LocalDate.now(), "Publisher", "OL12345"
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
        assertEquals("OL12345", command.openLibraryKey());
    }

    @Test
    @DisplayName("Should create CreateBookCommand with valid parameters and without OpenLibraryKey")
    void createBookCommand_WithValidParametersWithoutOpenLibraryKey_ShouldCreateInstance() {
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
        assertNull(command.openLibraryKey());
    }
} 