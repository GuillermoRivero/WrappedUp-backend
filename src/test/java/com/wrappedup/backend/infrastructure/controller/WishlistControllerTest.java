package com.wrappedup.backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrappedup.backend.application.WishlistService;
import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.Role;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.WishlistItem;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WishlistControllerTest {

    private static final UUID TEST_USER_ID = UUID.fromString("f2295dea-c8cb-4493-ae1c-dbfebbfc4f3a");
    private static final UUID TEST_BOOK_ID = UUID.fromString("a1234567-e89b-12d3-a456-426614174000");
    private static final UUID TEST_WISHLIST_ITEM_ID = UUID.fromString("b9876543-e21a-45c6-789d-012345678901");
    private static final String TEST_OPEN_LIBRARY_KEY = "/works/OL1234567W";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    private User testUser;
    private Book testBook;
    private WishlistItem testWishlistItem;

    @BeforeEach
    public void setup() {
        // Create test objects
        testUser = new User(TEST_USERNAME, TEST_EMAIL, "password");
        testUser.setId(TEST_USER_ID);
        testUser.setRole(Role.USER);
        testUser.setEnabled(true);
        
        testBook = new Book("Test Book", "Test Author", 2023, "Test Platform", "test-cover.jpg", TEST_OPEN_LIBRARY_KEY);
        testBook.setId(TEST_BOOK_ID);
        
        testWishlistItem = new WishlistItem(testUser, testBook, "Test description", 3, true);
        testWishlistItem.setId(TEST_WISHLIST_ITEM_ID);
        testWishlistItem.setCreatedAt(LocalDateTime.now());
        
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
    @WithMockUser(username = "test@example.com")
    void shouldGetUserWishlist() throws Exception {
        // Given
        List<WishlistItem> wishlist = List.of(testWishlistItem);
        when(wishlistService.getUserWishlistByUsername(TEST_EMAIL)).thenReturn(wishlist);

        // When & Then
        mockMvc.perform(get("/api/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_WISHLIST_ITEM_ID.toString()))
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$[0].book.id").value(TEST_BOOK_ID.toString()))
                .andExpect(jsonPath("$[0].description").value("Test description"))
                .andExpect(jsonPath("$[0].priority").value(3))
                .andExpect(jsonPath("$[0].isPublic").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldGetPublicWishlistByUserId() throws Exception {
        // Given
        List<WishlistItem> publicWishlist = List.of(testWishlistItem);
        when(wishlistService.getPublicWishlistByUsername(TEST_USERNAME)).thenReturn(publicWishlist);

        // When & Then
        mockMvc.perform(get("/api/wishlist/public/user/{username}", TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(TEST_WISHLIST_ITEM_ID.toString()))
                .andExpect(jsonPath("$[0].userId").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$[0].isPublic").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldAddToWishlistByBookId() throws Exception {
        // Given
        WishlistRequest request = new WishlistRequest();
        request.setBookId(TEST_BOOK_ID);
        request.setDescription("Test description");
        request.setPriority(3);
        request.setIsPublic(true);
        
        when(wishlistService.addToWishlistByUsername(
                eq(TEST_EMAIL), 
                eq(TEST_BOOK_ID), 
                anyString(), 
                anyInt(), 
                anyBoolean()))
            .thenReturn(testWishlistItem);

        // When & Then
        mockMvc.perform(post("/api/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_WISHLIST_ITEM_ID.toString()))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldAddToWishlistByOpenLibraryKey() throws Exception {
        // Given
        WishlistRequest request = new WishlistRequest();
        request.setOpenLibraryKey(TEST_OPEN_LIBRARY_KEY);
        request.setDescription("Test description");
        request.setPriority(3);
        request.setIsPublic(true);
        
        when(wishlistService.addToWishlistByOpenLibraryKeyAndUsername(
                eq(TEST_EMAIL), 
                any(WishlistRequest.class)))
            .thenReturn(testWishlistItem);

        // When & Then
        mockMvc.perform(post("/api/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_WISHLIST_ITEM_ID.toString()))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldGetWishlistItem() throws Exception {
        // Given
        when(wishlistService.getWishlistItemByUsername(TEST_EMAIL, TEST_WISHLIST_ITEM_ID))
                .thenReturn(Optional.of(testWishlistItem));

        // When & Then
        mockMvc.perform(get("/api/wishlist/{wishlistItemId}", TEST_WISHLIST_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_WISHLIST_ITEM_ID.toString()))
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID.toString()))
                .andExpect(jsonPath("$.isPublic").value(true));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturn404WhenWishlistItemNotFound() throws Exception {
        // Given
        when(wishlistService.getWishlistItemByUsername(TEST_EMAIL, TEST_WISHLIST_ITEM_ID))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/wishlist/{wishlistItemId}", TEST_WISHLIST_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdateWishlistItem() throws Exception {
        // Given
        WishlistUpdateRequest request = new WishlistUpdateRequest();
        request.setDescription("Updated description");
        request.setPriority(4);
        request.setIsPublic(false);
        
        WishlistItem updatedItem = new WishlistItem(testUser, testBook, "Updated description", 4, false);
        updatedItem.setId(TEST_WISHLIST_ITEM_ID);
        
        when(wishlistService.updateWishlistItemByUsername(
                eq(TEST_EMAIL), 
                eq(TEST_WISHLIST_ITEM_ID), 
                anyString(), 
                anyInt(), 
                anyBoolean()))
            .thenReturn(updatedItem);

        // When & Then
        mockMvc.perform(put("/api/wishlist/{wishlistItemId}", TEST_WISHLIST_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_WISHLIST_ITEM_ID.toString()))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.priority").value(4))
                .andExpect(jsonPath("$.isPublic").value(false));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldRemoveFromWishlist() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/wishlist/{wishlistItemId}", TEST_WISHLIST_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isNoContent());
    }
} 