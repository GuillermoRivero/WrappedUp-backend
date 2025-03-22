package com.wrappedup.backend.infrastructure.controller;

import com.wrappedup.backend.application.WishlistService;
import com.wrappedup.backend.domain.WishlistItem;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.exception.WishlistOperationException;
import com.wrappedup.backend.infrastructure.dto.WishlistItemDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<?> getUserWishlist(@AuthenticationPrincipal User user) {
        try {
            List<WishlistItem> wishlist = wishlistService.getUserWishlistByUsername(user.getUsername());
            List<WishlistItemDTO> wishlistDTOs = wishlist.stream()
                    .map(WishlistItemDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(wishlistDTOs);
        } catch (Exception e) {
            log.error("Error retrieving user wishlist for user email: {}", user.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving wishlist: " + e.getMessage()));
        }
    }

    @GetMapping("/public/user/{username}")
    public ResponseEntity<?> getPublicWishlistByUsername(@PathVariable String username) {
        try {
            List<WishlistItem> publicWishlist = wishlistService.getPublicWishlistByUsername(username);
            List<WishlistItemDTO> wishlistDTOs = publicWishlist.stream()
                    .map(WishlistItemDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(wishlistDTOs);
        } catch (IllegalArgumentException e) {
            log.warn("User not found: {}", username, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving public wishlist for username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving public wishlist: " + e.getMessage()));
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> addToWishlist(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WishlistRequest request) {
        
        if (request.getBookId() == null && (request.getOpenLibraryKey() == null || request.getOpenLibraryKey().isEmpty())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            WishlistItem wishlistItem;
            
            if (request.getOpenLibraryKey() != null && !request.getOpenLibraryKey().isEmpty()) {
                String openLibraryKey = request.getOpenLibraryKey().trim();

                request.setOpenLibraryKey(openLibraryKey);
                
                wishlistItem = wishlistService.addToWishlistByOpenLibraryKeyAndUsername(user.getUsername(), request);
            } 
            else {
                wishlistItem = wishlistService.addToWishlistByUsername(
                        user.getUsername(),
                        request.getBookId(),
                        request.getDescription(),
                        request.getPriority(),
                        request.getIsPublic()
                );
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(WishlistItemDTO.fromEntity(wishlistItem));
        } catch (WishlistOperationException e) {
            log.warn("Error adding item to wishlist: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error adding book to wishlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal error processing request: " + e.getMessage()));
        }
    }

    @GetMapping("/{wishlistItemId}")
    public ResponseEntity<?> getWishlistItem(
            @AuthenticationPrincipal User user,
            @PathVariable UUID wishlistItemId) {
        
        try {
            return wishlistService.getWishlistItemByUsername(user.getUsername(), wishlistItemId)
                    .map(WishlistItemDTO::fromEntity)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            log.warn("Error getting wishlist item: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error getting wishlist item {}", wishlistItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal error retrieving wishlist item: " + e.getMessage()));
        }
    }

    @PutMapping("/{wishlistItemId}")
    @Transactional
    public ResponseEntity<?> updateWishlistItem(
            @AuthenticationPrincipal User user,
            @PathVariable UUID wishlistItemId,
            @Valid @RequestBody WishlistUpdateRequest request) {
        
        try {
            WishlistItem updatedItem = wishlistService.updateWishlistItemByUsername(
                    user.getUsername(),
                    wishlistItemId,
                    request.getDescription(),
                    request.getPriority(),
                    request.getIsPublic()
            );
            return ResponseEntity.ok(WishlistItemDTO.fromEntity(updatedItem));
        } catch (IllegalArgumentException e) {
            log.warn("Error updating wishlist item: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating wishlist item {}", wishlistItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal error updating wishlist item: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{wishlistItemId}")
    @Transactional
    public ResponseEntity<?> removeFromWishlist(
            @AuthenticationPrincipal User user,
            @PathVariable UUID wishlistItemId) {
        
        try {
            wishlistService.removeFromWishlistByUsername(user.getUsername(), wishlistItemId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Error removing wishlist item: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error removing wishlist item {}", wishlistItemId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal error removing wishlist item: " + e.getMessage()));
        }
    }
    
    @Data
    public static class ErrorResponse {
        private final String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
} 