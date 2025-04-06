package com.wrappedup.backend.infrastructure.config;

import com.wrappedup.backend.application.service.CreateBookService;
import com.wrappedup.backend.application.service.DeleteBookService;
import com.wrappedup.backend.application.service.GetBookService;
import com.wrappedup.backend.application.service.UpdateBookService;
import com.wrappedup.backend.domain.port.in.CreateBookUseCase;
import com.wrappedup.backend.domain.port.in.DeleteBookUseCase;
import com.wrappedup.backend.domain.port.in.UpdateBookUseCase;
import com.wrappedup.backend.domain.port.out.BookRepository;
import com.wrappedup.backend.domain.port.out.OpenLibraryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UseCaseConfigTest {

    @Mock
    private BookRepository bookRepository;
    
    @Mock
    private OpenLibraryPort openLibraryPort;

    @Test
    @DisplayName("Should create CreateBookUseCase")
    void createBookUseCase_ShouldReturnCreateBookService() {
        // Arrange
        UseCaseConfig useCaseConfig = new UseCaseConfig();
        
        // Act
        CreateBookUseCase useCase = useCaseConfig.createBookUseCase(bookRepository);
        
        // Assert
        assertNotNull(useCase);
        assertTrue(useCase instanceof CreateBookService);
    }
    
    @Test
    @DisplayName("Should create UpdateBookUseCase")
    void updateBookUseCase_ShouldReturnUpdateBookService() {
        // Arrange
        UseCaseConfig useCaseConfig = new UseCaseConfig();
        
        // Act
        UpdateBookUseCase useCase = useCaseConfig.updateBookUseCase(bookRepository);
        
        // Assert
        assertNotNull(useCase);
        assertTrue(useCase instanceof UpdateBookService);
    }
    
    @Test
    @DisplayName("Should create DeleteBookUseCase")
    void deleteBookUseCase_ShouldReturnDeleteBookService() {
        // Arrange
        UseCaseConfig useCaseConfig = new UseCaseConfig();
        
        // Act
        DeleteBookUseCase useCase = useCaseConfig.deleteBookUseCase(bookRepository);
        
        // Assert
        assertNotNull(useCase);
        assertTrue(useCase instanceof DeleteBookService);
    }
    
    @Test
    @DisplayName("Should create GetBookService")
    void getBookService_ShouldReturnGetBookService() {
        // Arrange
        UseCaseConfig useCaseConfig = new UseCaseConfig();
        
        // Act
        GetBookService service = useCaseConfig.getBookService(bookRepository, openLibraryPort);
        
        // Assert
        assertNotNull(service);
        assertTrue(service instanceof GetBookService);
    }
} 