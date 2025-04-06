package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.CreateWishlistItemUseCase.CreateWishlistItemCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateWishlistItemUseCaseTest {

    @Test
    @DisplayName("Should throw exception when userId is null in CreateWishlistItemCommand")
    void createWishlistItemCommand_WithNullUserId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateWishlistItemCommand(
                null, 
                BookId.of(UUID.randomUUID()), 
                "Want to read this book", 
                3, 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when bookId is null in CreateWishlistItemCommand")
    void createWishlistItemCommand_WithNullBookId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateWishlistItemCommand(
                UserId.of(UUID.randomUUID()),
                null, 
                "Want to read this book", 
                3, 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when priority is less than 1 in CreateWishlistItemCommand")
    void createWishlistItemCommand_WithPriorityLessThanOne_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateWishlistItemCommand(
                UserId.of(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                "Want to read this book", 
                0, 
                true
            )
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateWishlistItemCommand(
                UserId.of(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                "Want to read this book", 
                -1, 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when priority is greater than 5 in CreateWishlistItemCommand")
    void createWishlistItemCommand_WithPriorityGreaterThanFive_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateWishlistItemCommand(
                UserId.of(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                "Want to read this book", 
                6, 
                true
            )
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateWishlistItemCommand(
                UserId.of(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                "Want to read this book", 
                10, 
                true
            )
        );
    }

    @Test
    @DisplayName("Should set default priority and visibility when null in CreateWishlistItemCommand")
    void createWishlistItemCommand_WithNullPriorityAndVisibility_ShouldSetDefaults() {
        UserId userId = UserId.of(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        
        CreateWishlistItemCommand command = new CreateWishlistItemCommand(
            userId, bookId, "Want to read this book", null, null
        );
        
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertEquals(bookId, command.bookId());
        assertEquals("Want to read this book", command.description());
        assertEquals(3, command.priority());  // Default priority
        assertFalse(command.isPublic());      // Default visibility
    }

    @Test
    @DisplayName("Should create CreateWishlistItemCommand with valid parameters")
    void createWishlistItemCommand_WithValidParameters_ShouldCreateInstance() {
        UserId userId = UserId.of(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        String description = "I want to read this book soon";
        int priority = 5;
        boolean isPublic = true;
        
        CreateWishlistItemCommand command = new CreateWishlistItemCommand(
            userId, bookId, description, priority, isPublic
        );
        
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertEquals(bookId, command.bookId());
        assertEquals(description, command.description());
        assertEquals(priority, command.priority());
        assertEquals(isPublic, command.isPublic());
    }

    @Test
    @DisplayName("Should create CreateWishlistItemCommand with valid parameters and null description")
    void createWishlistItemCommand_WithNullDescription_ShouldCreateInstance() {
        UserId userId = UserId.of(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        int priority = 2;
        boolean isPublic = false;
        
        CreateWishlistItemCommand command = new CreateWishlistItemCommand(
            userId, bookId, null, priority, isPublic
        );
        
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertEquals(bookId, command.bookId());
        assertNull(command.description());
        assertEquals(priority, command.priority());
        assertEquals(isPublic, command.isPublic());
    }
} 