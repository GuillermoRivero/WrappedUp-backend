package com.wrappedup.backend.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrappedup.backend.domain.Role;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.port.UserProfileService;
import com.wrappedup.backend.dto.UpdateUserProfileDto;
import com.wrappedup.backend.dto.UserProfileDto;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserProfileControllerTest {

    private static final UUID TEST_USER_ID = UUID.fromString("f2295dea-c8cb-4493-ae1c-dbfebbfc4f3a");
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private UserProfileService userProfileService;

    private User testUser;
    private UserProfileDto testProfileDto;
    private UpdateUserProfileDto updateProfileDto;

    @BeforeEach
    public void setup() {
        // Create test user
        testUser = new User(TEST_USERNAME, TEST_EMAIL, "password");
        testUser.setId(TEST_USER_ID);
        testUser.setRole(Role.USER);
        testUser.setEnabled(true);

        // Create test profile DTO
        testProfileDto = new UserProfileDto();
        testProfileDto.setUsername(TEST_USERNAME);
        testProfileDto.setEmail(TEST_EMAIL);
        testProfileDto.setFullName("Test User");
        testProfileDto.setBio("This is a test bio");
        testProfileDto.setUserImageUrl("https://example.com/image.jpg");
        testProfileDto.setFavoriteGenres(List.of("Fantasy", "Science Fiction"));
        testProfileDto.setReadingGoal(50);
        testProfileDto.setPreferredLanguage("English");
        testProfileDto.setPublicProfile(true);
        Map<String, String> socialLinks = new HashMap<>();
        socialLinks.put("twitter", "https://twitter.com/testuser");
        testProfileDto.setSocialLinks(socialLinks);
        testProfileDto.setLocation("Test City");
        testProfileDto.setCreatedAt(LocalDateTime.now());
        testProfileDto.setUpdatedAt(LocalDateTime.now());

        // Create update profile DTO
        updateProfileDto = new UpdateUserProfileDto();
        updateProfileDto.setFullName("Updated Name");
        updateProfileDto.setBio("Updated bio");
        updateProfileDto.setUserImageUrl("https://example.com/updated.jpg");
        updateProfileDto.setFavoriteGenres(List.of("Mystery", "Thriller"));
        updateProfileDto.setReadingGoal(75);
        updateProfileDto.setPreferredLanguage("Spanish");
        updateProfileDto.setPublicProfile(true);
        Map<String, String> updatedLinks = new HashMap<>();
        updatedLinks.put("instagram", "https://instagram.com/testuser");
        updateProfileDto.setSocialLinks(updatedLinks);
        updateProfileDto.setLocation("Updated City");

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
    void shouldGetUserProfile() throws Exception {
        // Given
        when(userProfileService.getProfile(TEST_USER_ID)).thenReturn(Optional.of(testProfileDto));

        // When & Then
        mockMvc.perform(get("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.bio").value("This is a test bio"))
                .andExpect(jsonPath("$.favoriteGenres[0]").value("Fantasy"))
                .andExpect(jsonPath("$.favoriteGenres[1]").value("Science Fiction"))
                .andExpect(jsonPath("$.readingGoal").value(50))
                .andExpect(jsonPath("$.preferredLanguage").value("English"))
                .andExpect(jsonPath("$.isPublicProfile").value(true))
                .andExpect(jsonPath("$.socialLinks.twitter").value("https://twitter.com/testuser"))
                .andExpect(jsonPath("$.location").value("Test City"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturn404WhenUserProfileNotFound() throws Exception {
        // Given
        when(userProfileService.getProfile(TEST_USER_ID)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdateUserProfile() throws Exception {
        // Given
        when(userProfileService.updateProfile(eq(TEST_USER_ID), any(UpdateUserProfileDto.class)))
                .thenReturn(testProfileDto);

        // When & Then
        mockMvc.perform(put("/api/profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProfileDto))
                .principal(new UsernamePasswordAuthenticationToken(testUser, null, testUser.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));
    }

    @Test
    void shouldGetPublicProfile() throws Exception {
        // Given
        when(userProfileService.getPublicProfile(TEST_USERNAME)).thenReturn(Optional.of(testProfileDto));

        // When & Then
        mockMvc.perform(get("/api/profiles/public/{username}", TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.isPublicProfile").value(true));
    }

    @Test
    void shouldReturn404WhenPublicProfileNotFound() throws Exception {
        // Given
        when(userProfileService.getPublicProfile(TEST_USERNAME)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/profiles/public/{username}", TEST_USERNAME)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
} 