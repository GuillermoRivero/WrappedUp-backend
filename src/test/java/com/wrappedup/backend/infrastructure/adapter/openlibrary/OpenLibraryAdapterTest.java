package com.wrappedup.backend.infrastructure.adapter.openlibrary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrappedup.backend.domain.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenLibraryAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenLibraryAdapter openLibraryAdapter;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void searchBooks_ShouldReturnEmptyList_WhenApiReturnsNull() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(null);

        // Act
        List<Book> result = openLibraryAdapter.searchBooks("test query");

        // Assert
        assertTrue(result.isEmpty());
        verify(restTemplate).getForObject(anyString(), eq(JsonNode.class));
    }

    @Test
    void searchBooks_ShouldReturnEmptyList_WhenApiThrowsException() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenThrow(new RestClientException("API Error"));

        // Act
        List<Book> result = openLibraryAdapter.searchBooks("test query");

        // Assert
        assertTrue(result.isEmpty());
        verify(restTemplate).getForObject(anyString(), eq(JsonNode.class));
    }

    @Test
    void searchBooks_ShouldReturnEmptyList_WhenResponseDoesNotHaveDocs() {
        // Arrange
        ObjectNode response = objectMapper.createObjectNode();
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(response);

        // Act
        List<Book> result = openLibraryAdapter.searchBooks("test query");

        // Assert
        assertTrue(result.isEmpty());
        verify(restTemplate).getForObject(anyString(), eq(JsonNode.class));
    }

    @Test
    void searchBooks_ShouldReturnBooks_WhenResponseHasDocs() {
        // Arrange
        ObjectNode doc = objectMapper.createObjectNode();
        doc.put("key", "/works/OL123W");
        doc.put("title", "Test Book");
        
        ArrayNode authors = objectMapper.createArrayNode();
        authors.add("Test Author");
        doc.set("author_name", authors);
        
        ArrayNode isbns = objectMapper.createArrayNode();
        isbns.add("1234567890");
        doc.set("isbn", isbns);
        
        ArrayNode subjects = objectMapper.createArrayNode();
        subjects.add("Fiction");
        subjects.add("Adventure");
        doc.set("subject", subjects);
        
        doc.put("first_publish_year", 2020);
        doc.put("cover_i", 12345);

        ArrayNode docs = objectMapper.createArrayNode();
        docs.add(doc);

        ObjectNode response = objectMapper.createObjectNode();
        response.set("docs", docs);

        when(restTemplate.getForObject(anyString(), eq(JsonNode.class))).thenReturn(response);

        // Act
        List<Book> result = openLibraryAdapter.searchBooks("test query");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        Book book = result.get(0);
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("1234567890", book.getIsbn());
        assertTrue(book.getGenres().contains("Fiction"));
        assertTrue(book.getGenres().contains("Adventure"));
        assertNotNull(book.getCoverImageUrl());
        
        verify(restTemplate).getForObject(anyString(), eq(JsonNode.class));
    }

    @Test
    void getBookByKey_ShouldReturnBook_WhenResponseIsValid() {
        // Arrange
        String openLibraryKey = "/works/OL123W";
        
        // Create work data
        ObjectNode workData = objectMapper.createObjectNode();
        workData.put("title", "Test Book");
        
        // Create author data
        ObjectNode authorRef = objectMapper.createObjectNode();
        ObjectNode author = objectMapper.createObjectNode();
        author.put("key", "/authors/OL123A");
        authorRef.set("author", author);
        
        ArrayNode authors = objectMapper.createArrayNode();
        authors.add(authorRef);
        workData.set("authors", authors);
        
        // Create author details
        ObjectNode authorData = objectMapper.createObjectNode();
        authorData.put("name", "Test Author");
        
        // Create covers
        ArrayNode covers = objectMapper.createArrayNode();
        covers.add(12345);
        workData.set("covers", covers);
        
        // Create description
        workData.put("description", "This is a test book description");
        
        // Create subjects
        ArrayNode subjects = objectMapper.createArrayNode();
        subjects.add("Fiction");
        subjects.add("Adventure");
        workData.set("subjects", subjects);
        
        // Set publication date
        workData.put("first_publish_date", "2020");
        
        when(restTemplate.getForObject(contains(openLibraryKey), eq(JsonNode.class))).thenReturn(workData);
        when(restTemplate.getForObject(contains("/authors/"), eq(JsonNode.class))).thenReturn(authorData);

        // Act
        List<Book> result = openLibraryAdapter.getBookByKey(openLibraryKey);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        Book book = result.get(0);
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("This is a test book description", book.getDescription());
        assertTrue(book.getGenres().contains("Fiction"));
        assertTrue(book.getGenres().contains("Adventure"));
        assertTrue(book.getCoverImageUrl().contains("12345"));
        assertEquals(openLibraryKey, book.getOpenLibraryKey());
    }

    @Test
    void getBookByKey_ShouldReturnEmptyList_WhenApiThrowsException() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(JsonNode.class)))
                .thenThrow(new RestClientException("API Error"));

        // Act
        List<Book> result = openLibraryAdapter.getBookByKey("/works/OL123W");

        // Assert
        assertTrue(result.isEmpty());
        verify(restTemplate, atLeastOnce()).getForObject(anyString(), eq(JsonNode.class));
    }

    @Test
    void getBookByKey_ShouldHandleNullFields() {
        // Arrange
        String openLibraryKey = "/works/OL123W";
        
        // Create minimal work data
        ObjectNode workData = objectMapper.createObjectNode();
        workData.put("title", "Test Book");
        
        when(restTemplate.getForObject(contains(openLibraryKey), eq(JsonNode.class))).thenReturn(workData);

        // Act
        List<Book> result = openLibraryAdapter.getBookByKey(openLibraryKey);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        Book book = result.get(0);
        assertEquals("Test Book", book.getTitle());
        assertEquals("Unknown Author", book.getAuthor());
        assertNull(book.getDescription());
        assertTrue(book.getGenres().isEmpty());
        assertNull(book.getCoverImageUrl());
        assertEquals(openLibraryKey, book.getOpenLibraryKey());
    }
} 