package com.wrappedup.backend.domain.port.in;

import com.wrappedup.backend.domain.model.BookId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.verify;

class DeleteBookUseCaseTest {

    @Test
    @DisplayName("Should call deleteBook with the provided BookId")
    void deleteBook_ShouldCallWithProvidedBookId() {
        // Create a mock implementation of the interface
        DeleteBookUseCase useCase = Mockito.mock(DeleteBookUseCase.class);
        
        // Create a test BookId
        BookId bookId = BookId.of(UUID.randomUUID());
        
        // Call the method
        useCase.deleteBook(bookId);
        
        // Verify the method was called with the correct BookId
        verify(useCase).deleteBook(bookId);
    }
} 