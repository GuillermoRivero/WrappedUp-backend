package com.wrappedup.backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrappedup.backend.application.AddBookUseCase;
import com.wrappedup.backend.application.GetBookInfoUseCase;
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
    private OpenLibraryService openLibraryService;

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
        Book book = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        List<Book> books = Arrays.asList(book);
        when(openLibraryService.searchBooks(query)).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books/search")
                .param("query", query)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(books)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetBookById() throws Exception {
        // Given
        UUID bookId = UUID.randomUUID();
        Book book = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        when(getBookInfoUseCase.execute(bookId)).thenReturn(Optional.of(book));

        // When & Then
        mockMvc.perform(get("/api/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(book)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetAllBooks() throws Exception {
        // Given
        List<Book> books = Arrays.asList(
            new Book("Test Book 1", "Test Author", 2023, "Test Platform", "test-cover-1.jpg", "OL123456W"),
            new Book("Test Book 2", "Test Author", 2023, "Test Platform", "test-cover-2.jpg", "OL789012W")
        );
        when(getBookInfoUseCase.findAll()).thenReturn(books);

        // When & Then
        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(books)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldAddBook() throws Exception {
        // Given
        Book book = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        when(addBookUseCase.execute(any(Book.class))).thenReturn(book);

        // When & Then
        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(book)));
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