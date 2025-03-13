package com.wrappedup.backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrappedup.backend.application.AddReviewUseCase;
import com.wrappedup.backend.application.GetUserReviewsUseCase;
import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.Review;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.Role;
import com.wrappedup.backend.domain.port.BookRepository;
import com.wrappedup.backend.infrastructure.service.OpenLibraryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerTest {

    private static final UUID TEST_USER_ID = UUID.fromString("f2295dea-c8cb-4493-ae1c-dbfebbfc4f3a");

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private AddReviewUseCase addReviewUseCase;

    @MockBean
    private GetUserReviewsUseCase getUserReviewsUseCase;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private OpenLibraryService openLibraryService;

    private User testUser;

    @BeforeEach
    public void setup() {
        // Create a test user
        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId(TEST_USER_ID);
        testUser.setRole(Role.USER);
        testUser.setEnabled(true);
        
        // Set up the security context with the test user
        Authentication auth = new UsernamePasswordAuthenticationToken(
            testUser, null, testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetReviewById() throws Exception {
        // Given
        UUID reviewId = UUID.randomUUID();
        Book book = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        Review review = new Review(
            TEST_USER_ID,
            book,
            "Great book!",
            5,
            LocalDate.now().minusDays(10),
            LocalDate.now()
        );
        when(getUserReviewsUseCase.findById(reviewId)).thenReturn(Optional.of(review));

        // When & Then
        mockMvc.perform(get("/api/reviews/{id}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(review)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetUserReviews() throws Exception {
        // Given
        Book book = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        List<Review> reviews = Arrays.asList(
            new Review(TEST_USER_ID, book, "Great book!", 5, LocalDate.now(), LocalDate.now())
        );
        when(getUserReviewsUseCase.execute(TEST_USER_ID)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/user/{userId}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviews)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldAddReview() throws Exception {
        // Given
        Book book = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        Review review = new Review(TEST_USER_ID, book, "Great book!", 5, LocalDate.now(), LocalDate.now());
        when(bookRepository.findByOpenLibraryKey(any())).thenReturn(Optional.of(book));
        when(addReviewUseCase.execute(any(Review.class))).thenReturn(review);

        // Create a JSON request with the correct property names
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("open_library_key", "OL123456W");
        requestMap.put("text", "Great book!");
        requestMap.put("rating", 5);
        requestMap.put("start_date", LocalDate.now().minusDays(7).toString());
        requestMap.put("end_date", LocalDate.now().toString());

        // When & Then
        mockMvc.perform(post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMap))
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(review)));
    }

    @Test
    @WithMockUser(username = "testuser")
    void shouldGetMyReviews() throws Exception {
        // Given
        Book book = new Book(
            "Test Book",
            "Test Author",
            2023,
            "Test Platform",
            "test-cover.jpg",
            "OL123456W"
        );
        List<Review> reviews = Arrays.asList(
            new Review(TEST_USER_ID, book, "Great book!", 5, LocalDate.now(), LocalDate.now())
        );
        when(getUserReviewsUseCase.execute(TEST_USER_ID)).thenReturn(reviews);

        // When & Then
        mockMvc.perform(get("/api/reviews/me")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reviews)));
    }
} 