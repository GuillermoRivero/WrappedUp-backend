package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.CreateReviewUseCase.CreateReviewCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateReviewUseCaseTest {

    @Test
    @DisplayName("Should throw exception when userId is null in CreateReviewCommand")
    void createReviewCommand_WithNullUserId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                null, 
                BookId.of(UUID.randomUUID()), 
                4, 
                "Great book!", 
                LocalDate.now().minusDays(30), 
                LocalDate.now(), 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when bookId is null in CreateReviewCommand")
    void createReviewCommand_WithNullBookId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                UserId.of(UUID.randomUUID()),
                null, 
                4, 
                "Great book!", 
                LocalDate.now().minusDays(30), 
                LocalDate.now(), 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when rating is less than 1 in CreateReviewCommand")
    void createReviewCommand_WithRatingLessThanOne_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                UserId.of(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                0, 
                "Bad rating", 
                LocalDate.now().minusDays(30), 
                LocalDate.now(), 
                true
            )
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                UserId.of(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                -1, 
                "Negative rating", 
                LocalDate.now().minusDays(30), 
                LocalDate.now(), 
                true
            )
        );
    }

    @Test
    @DisplayName("Should throw exception when rating is greater than 5 in CreateReviewCommand")
    void createReviewCommand_WithRatingGreaterThanFive_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                UserId.of(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                6, 
                "Too high rating", 
                LocalDate.now().minusDays(30), 
                LocalDate.now(), 
                true
            )
        );
        
        assertThrows(IllegalArgumentException.class, () -> 
            new CreateReviewCommand(
                UserId.of(UUID.randomUUID()),
                BookId.of(UUID.randomUUID()), 
                10, 
                "Way too high rating", 
                LocalDate.now().minusDays(30), 
                LocalDate.now(), 
                true
            )
        );
    }

    @Test
    @DisplayName("Should create CreateReviewCommand with valid parameters")
    void createReviewCommand_WithValidParameters_ShouldCreateInstance() {
        UserId userId = UserId.of(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        String content = "This was a great book! Highly recommended.";
        boolean isPublic = true;
        int rating = 5;
        
        CreateReviewCommand command = new CreateReviewCommand(
            userId, bookId, rating, content, startDate, endDate, isPublic
        );
        
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertEquals(bookId, command.bookId());
        assertEquals(rating, command.rating());
        assertEquals(content, command.content());
        assertEquals(startDate, command.startDate());
        assertEquals(endDate, command.endDate());
        assertEquals(isPublic, command.isPublic());
    }

    @Test
    @DisplayName("Should create CreateReviewCommand with minimum valid parameters")
    void createReviewCommand_WithMinimumValidParameters_ShouldCreateInstance() {
        UserId userId = UserId.of(UUID.randomUUID());
        BookId bookId = BookId.of(UUID.randomUUID());
        int rating = 1;
        
        CreateReviewCommand command = new CreateReviewCommand(
            userId, bookId, rating, null, null, null, false
        );
        
        assertNotNull(command);
        assertEquals(userId, command.userId());
        assertEquals(bookId, command.bookId());
        assertEquals(rating, command.rating());
        assertNull(command.content());
        assertNull(command.startDate());
        assertNull(command.endDate());
        assertFalse(command.isPublic());
    }
} 