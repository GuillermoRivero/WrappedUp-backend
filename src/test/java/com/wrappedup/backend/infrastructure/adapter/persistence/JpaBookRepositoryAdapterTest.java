package com.wrappedup.backend.infrastructure.adapter.persistence;

import com.wrappedup.backend.domain.exception.BookPersistenceException;
import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.BookJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.repository.BookJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaBookRepositoryAdapterTest {

    @Mock
    private BookJpaRepository bookJpaRepository;

    @InjectMocks
    private JpaBookRepositoryAdapter adapter;

    @Test
    void save_ShouldReturnSavedBook_WhenBookIsSaved() {
        // Arrange
        UUID id = UUID.randomUUID();
        Book book = createTestBook(id);
        BookJpaEntity entity = createTestBookEntity(id);
        
        when(bookJpaRepository.save(any(BookJpaEntity.class))).thenReturn(entity);
        
        // Act
        Book result = adapter.save(book);
        
        // Assert
        assertNotNull(result);
        assertEquals(id.toString(), result.getId().getValue().toString());
        assertEquals(book.getTitle(), result.getTitle());
        assertEquals(book.getAuthor(), result.getAuthor());
        verify(bookJpaRepository).save(any(BookJpaEntity.class));
    }
    
    @Test
    void save_ShouldThrowBookPersistenceException_WhenDataIntegrityViolationOccurs() {
        // Arrange
        UUID id = UUID.randomUUID();
        Book book = createTestBook(id);
        
        when(bookJpaRepository.save(any(BookJpaEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Data integrity violation"));
        
        // Act & Assert
        assertThrows(BookPersistenceException.class, () -> adapter.save(book));
        verify(bookJpaRepository).save(any(BookJpaEntity.class));
    }

    @Test
    void save_ShouldThrowBookPersistenceException_WhenGeneralExceptionOccurs() {
        // Arrange
        UUID id = UUID.randomUUID();
        Book book = createTestBook(id);
        
        when(bookJpaRepository.save(any(BookJpaEntity.class)))
                .thenThrow(new RuntimeException("General error"));
        
        // Act & Assert
        assertThrows(BookPersistenceException.class, () -> adapter.save(book));
        verify(bookJpaRepository).save(any(BookJpaEntity.class));
    }

    @Test
    void findById_ShouldReturnBook_WhenBookExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        BookJpaEntity entity = createTestBookEntity(id);
        
        when(bookJpaRepository.findById(id)).thenReturn(Optional.of(entity));
        
        // Act
        Optional<Book> result = adapter.findById(BookId.of(id.toString()));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(id.toString(), result.get().getId().getValue().toString());
        assertEquals(entity.getTitle(), result.get().getTitle());
        verify(bookJpaRepository).findById(id);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenBookDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        
        when(bookJpaRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act
        Optional<Book> result = adapter.findById(BookId.of(id.toString()));
        
        // Assert
        assertFalse(result.isPresent());
        verify(bookJpaRepository).findById(id);
    }

    @Test
    void findByTitleContaining_ShouldReturnBooks_WhenBooksWithTitleExist() {
        // Arrange
        String titleText = "test";
        List<BookJpaEntity> entities = Arrays.asList(
                createTestBookEntity(UUID.randomUUID()),
                createTestBookEntity(UUID.randomUUID())
        );
        
        when(bookJpaRepository.findByTitleContainingIgnoreCase(titleText)).thenReturn(entities);
        
        // Act
        List<Book> result = adapter.findByTitleContaining(titleText);
        
        // Assert
        assertEquals(2, result.size());
        verify(bookJpaRepository).findByTitleContainingIgnoreCase(titleText);
    }

    @Test
    void findByTitleContaining_ShouldReturnEmptyList_WhenNoBooksWithTitleExist() {
        // Arrange
        String titleText = "nonexistent";
        
        when(bookJpaRepository.findByTitleContainingIgnoreCase(titleText)).thenReturn(Collections.emptyList());
        
        // Act
        List<Book> result = adapter.findByTitleContaining(titleText);
        
        // Assert
        assertTrue(result.isEmpty());
        verify(bookJpaRepository).findByTitleContainingIgnoreCase(titleText);
    }

    @Test
    void findByAuthorContaining_ShouldReturnBooks_WhenBooksWithAuthorExist() {
        // Arrange
        String authorText = "author";
        List<BookJpaEntity> entities = Arrays.asList(
                createTestBookEntity(UUID.randomUUID()),
                createTestBookEntity(UUID.randomUUID())
        );
        
        when(bookJpaRepository.findByAuthorContainingIgnoreCase(authorText)).thenReturn(entities);
        
        // Act
        List<Book> result = adapter.findByAuthorContaining(authorText);
        
        // Assert
        assertEquals(2, result.size());
        verify(bookJpaRepository).findByAuthorContainingIgnoreCase(authorText);
    }

    @Test
    void findByIsbn_ShouldReturnBook_WhenBookWithIsbnExists() {
        // Arrange
        String isbn = "1234567890";
        UUID id = UUID.randomUUID();
        BookJpaEntity entity = createTestBookEntity(id);
        
        when(bookJpaRepository.findByIsbn(isbn)).thenReturn(Optional.of(entity));
        
        // Act
        Optional<Book> result = adapter.findByIsbn(isbn);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(id.toString(), result.get().getId().getValue().toString());
        verify(bookJpaRepository).findByIsbn(isbn);
    }

    @Test
    void findByGenre_ShouldReturnBooks_WhenBooksWithGenreExist() {
        // Arrange
        String genre = "fantasy";
        List<BookJpaEntity> entities = Arrays.asList(
                createTestBookEntity(UUID.randomUUID()),
                createTestBookEntity(UUID.randomUUID())
        );
        
        when(bookJpaRepository.findByGenre(genre)).thenReturn(entities);
        
        // Act
        List<Book> result = adapter.findByGenre(genre);
        
        // Assert
        assertEquals(2, result.size());
        verify(bookJpaRepository).findByGenre(genre);
    }

    @Test
    void deleteById_ShouldDeleteBook() {
        // Arrange
        UUID id = UUID.randomUUID();
        
        // Act
        adapter.deleteById(BookId.of(id.toString()));
        
        // Assert
        verify(bookJpaRepository).deleteById(id);
    }

    @Test
    void existsByIsbn_ShouldReturnTrue_WhenBookWithIsbnExists() {
        // Arrange
        String isbn = "1234567890";
        
        when(bookJpaRepository.existsByIsbn(isbn)).thenReturn(true);
        
        // Act
        boolean result = adapter.existsByIsbn(isbn);
        
        // Assert
        assertTrue(result);
        verify(bookJpaRepository).existsByIsbn(isbn);
    }

    @Test
    void existsByIsbn_ShouldReturnFalse_WhenBookWithIsbnDoesNotExist() {
        // Arrange
        String isbn = "1234567890";
        
        when(bookJpaRepository.existsByIsbn(isbn)).thenReturn(false);
        
        // Act
        boolean result = adapter.existsByIsbn(isbn);
        
        // Assert
        assertFalse(result);
        verify(bookJpaRepository).existsByIsbn(isbn);
    }

    @Test
    void findAll_ShouldReturnPagedBooks() {
        // Arrange
        int page = 0;
        int size = 10;
        List<BookJpaEntity> entities = Arrays.asList(
                createTestBookEntity(UUID.randomUUID()),
                createTestBookEntity(UUID.randomUUID())
        );
        Page<BookJpaEntity> pageResult = new PageImpl<>(entities);
        
        when(bookJpaRepository.findAll(any(Pageable.class))).thenReturn(pageResult);
        
        // Act
        List<Book> result = adapter.findAll(page, size);
        
        // Assert
        assertEquals(2, result.size());
        verify(bookJpaRepository).findAll(eq(PageRequest.of(page, size)));
    }

    @Test
    void count_ShouldReturnNumberOfBooks() {
        // Arrange
        long expectedCount = 5;
        
        when(bookJpaRepository.count()).thenReturn(expectedCount);
        
        // Act
        long result = adapter.count();
        
        // Assert
        assertEquals(expectedCount, result);
        verify(bookJpaRepository).count();
    }

    @Test
    void findByOpenLibraryKey_ShouldReturnBook_WhenBookExists() {
        // Arrange
        String openLibraryKey = "/works/OL123W";
        UUID id = UUID.randomUUID();
        BookJpaEntity entity = createTestBookEntity(id);
        entity.setOpenLibraryKey(openLibraryKey);
        
        when(bookJpaRepository.findByOpenLibraryKey(openLibraryKey)).thenReturn(Optional.of(entity));
        
        // Act
        Optional<Book> result = adapter.findByOpenLibraryKey(openLibraryKey);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(id.toString(), result.get().getId().getValue().toString());
        assertEquals(openLibraryKey, result.get().getOpenLibraryKey());
        verify(bookJpaRepository).findByOpenLibraryKey(openLibraryKey);
    }

    private Book createTestBook(UUID id) {
        return Book.reconstitute(
                BookId.of(id.toString()),
                "Test Book",
                "Test Author",
                "1234567890",
                "Test description",
                "https://covers.openlibrary.org/b/id/12345-M.jpg",
                200,
                Collections.singletonList("fantasy"),
                "en",
                LocalDate.of(2020, 1, 1),
                "Test Publisher",
                "/works/OL123W",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private BookJpaEntity createTestBookEntity(UUID id) {
        BookJpaEntity entity = new BookJpaEntity();
        entity.setId(id);
        entity.setTitle("Test Book");
        entity.setAuthor("Test Author");
        entity.setIsbns(Collections.singletonList("1234567890"));
        entity.setFirstSentence("Test description");
        entity.setCoverUrl("https://covers.openlibrary.org/b/id/12345-M.jpg");
        entity.setCoverId(12345L);
        entity.setNumberOfPagesMedian(200);
        entity.setGenres(Collections.singletonList("fantasy"));
        entity.setLanguages(Collections.singletonList("en"));
        entity.setFirstPublishYear(2020);
        entity.setPublishers(Collections.singletonList("Test Publisher"));
        entity.setOpenLibraryKey("/works/OL123W");
        entity.setPlatform("system");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
} 