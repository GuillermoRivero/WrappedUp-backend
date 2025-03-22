package com.wrappedup.backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrappedup.backend.application.AddBookUseCase;
import com.wrappedup.backend.application.BookService;
import com.wrappedup.backend.application.GetBookInfoUseCase;
import com.wrappedup.backend.application.WishlistService;
import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.Role;
import com.wrappedup.backend.infrastructure.service.OpenLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    private static final UUID TEST_USER_ID = UUID.fromString("f2295dea-c8cb-4493-ae1c-dbfebbfc4f3a");
    private static final UUID TEST_BOOK_ID = UUID.fromString("a1234567-e89b-12d3-a456-426614174000");

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private AddBookUseCase addBookUseCase;

    @MockBean
    private GetBookInfoUseCase getBookInfoUseCase;
    
    @MockBean
    private BookService bookService;

    @MockBean
    private OpenLibraryService openLibraryService;
    
    @MockBean
    private WishlistService wishlistService;

    private Book createTestBook() {
        Book book = new Book(
            "Test Book", 
            "Test Author", 
            2023, 
            "Test Platform", 
            "test-cover.jpg", 
            "/works/OL123456W"
        );
        book.setId(TEST_BOOK_ID);
        return book;
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldSearchBooks() throws Exception {
        // Given
        String query = "test";
        Book book = createTestBook();
        List<Book> books = Arrays.asList(book);
        when(bookService.searchInOpenLibrary(query)).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books/search")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_BOOK_ID.toString()))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[0].author").value("Test Author"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetBookById() throws Exception {
        // Given
        Book book = createTestBook();
        when(bookService.findById(TEST_BOOK_ID)).thenReturn(Optional.of(book));

        // When & Then
        mockMvc.perform(get("/api/books/{id}", TEST_BOOK_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID.toString()))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetAllBooks() throws Exception {
        // Given
        Book book1 = createTestBook();
        Book book2 = new Book("Test Book 2", "Test Author 2", 2023, "Test Platform", "test-cover-2.jpg", "/works/OL789012W");
        book2.setId(UUID.randomUUID());
        
        List<Book> books = Arrays.asList(book1, book2);
        when(bookService.findAll()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_BOOK_ID.toString()))
                .andExpect(jsonPath("$[0].title").value("Test Book"))
                .andExpect(jsonPath("$[1].title").value("Test Book 2"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldAddBook() throws Exception {
        // Given
        Book inputBook = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "/works/OL123456W"
        );
        
        Book savedBook = createTestBook();
        when(bookService.save(any(Book.class))).thenReturn(savedBook);

        // When & Then
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_BOOK_ID.toString()))
                .andExpect(jsonPath("$.title").value("Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"))
                .andExpect(jsonPath("$.firstPublishYear").value(2023))
                .andExpect(jsonPath("$.platform").value("Test Platform"))
                .andExpect(jsonPath("$.coverUrl").value("test-cover.jpg"))
                .andExpect(jsonPath("$.openLibraryKey").value("/works/OL123456W"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldHandleInvalidBook() throws Exception {
        // Given
        var invalidBook = new Book(
            "",  // Invalid: empty title
            "",  // Invalid: empty author
            0,   // Invalid: year too low
            "",  // Invalid: empty platform
            "",  // Invalid: empty cover URL
            ""   // Invalid: empty OpenLibrary key
        );

        // When & Then
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest());
    }
} 