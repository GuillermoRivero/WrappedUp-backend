package com.wrappedup.backend.infrastructure.adapter.web;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;
import com.wrappedup.backend.domain.port.in.CreateWishlistItemUseCase;
import com.wrappedup.backend.domain.port.in.CreateWishlistItemUseCase.CreateWishlistItemCommand;
import com.wrappedup.backend.domain.port.in.DeleteWishlistItemUseCase;
import com.wrappedup.backend.domain.port.in.GetBookUseCase;
import com.wrappedup.backend.domain.port.in.GetWishlistItemUseCase;
import com.wrappedup.backend.domain.port.in.UpdateWishlistItemUseCase;
import com.wrappedup.backend.domain.port.in.UpdateWishlistItemUseCase.UpdateWishlistItemCommand;
import com.wrappedup.backend.infrastructure.adapter.web.dto.BookDTO;
import com.wrappedup.backend.infrastructure.adapter.web.dto.WishlistItemDTO;
import com.wrappedup.backend.domain.port.out.UserIdPort;
import com.wrappedup.backend.application.service.GetBookService;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {

    private final CreateWishlistItemUseCase createWishlistItemUseCase;
    private final GetWishlistItemUseCase getWishlistItemUseCase;
    private final UpdateWishlistItemUseCase updateWishlistItemUseCase;
    private final DeleteWishlistItemUseCase deleteWishlistItemUseCase;
    private final GetBookUseCase getBookUseCase;
    private final UserIdPort userIdPort;
    private final GetBookService getBookService;

    @GetMapping
    public ResponseEntity<List<WishlistItemDTO>> getUserWishlist(@RequestHeader("Authorization") String token) {
        UUID userId = userIdPort.extractUserId(token.substring(7));
        
        List<WishlistItem> wishlistItems = getWishlistItemUseCase.getWishlistItemsByUserId(UserId.fromUUID(userId));
        
        List<WishlistItemDTO> wishlistItemDTOs = wishlistItems.stream()
                .map(wishlistItem -> {
                    // Fetch book details
                    BookDTO bookDTO = getBookUseCase.getBookById(wishlistItem.getBookId())
                            .map(BookDTO::fromDomain)
                            .orElse(null);
                    
                    return WishlistItemDTO.fromDomainWithBook(wishlistItem, bookDTO);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(wishlistItemDTOs);
    }

    @PostMapping
    public ResponseEntity<WishlistItemDTO> addToWishlist(
            @RequestHeader("Authorization") String token,
            @RequestBody AddToWishlistRequest request) {
        
        UUID userId = userIdPort.extractUserId(token.substring(7));
        
        try {
            BookId bookId;
            
            // If openLibraryKey is provided, fetch the book from OpenLibrary and persist it
            if (request.openLibraryKey != null && !request.openLibraryKey.trim().isEmpty()) {
                // Fetch and persist book from OpenLibrary
                bookId = getBookService.getAndPersistBookByOpenLibraryKey(request.openLibraryKey.trim())
                        .map(Book::getId)
                        .orElseThrow(() -> new IllegalArgumentException("Book not found with OpenLibrary key: " + request.openLibraryKey));
            } else if (request.bookId != null) {
                // Use provided book ID
                bookId = BookId.fromUUID(request.bookId);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            // Create wishlist item
            WishlistItemId wishlistItemId = createWishlistItemUseCase.createWishlistItem(
                    new CreateWishlistItemCommand(
                            UserId.fromUUID(userId),
                            bookId,
                            request.description,
                            request.priority,
                            request.isPublic
                    )
            );
            
            // Get created wishlist item
            WishlistItem wishlistItem = getWishlistItemUseCase.getWishlistItemById(wishlistItemId)
                    .orElseThrow(() -> new IllegalStateException("Created wishlist item not found"));
            
            // Fetch book details
            BookDTO bookDTO = getBookUseCase.getBookById(wishlistItem.getBookId())
                    .map(BookDTO::fromDomain)
                    .orElse(null);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(WishlistItemDTO.fromDomainWithBook(wishlistItem, bookDTO));
            
        } catch (IllegalStateException e) {
            log.warn("Failed to add book to wishlist: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error adding book to wishlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<WishlistItemDTO> updateWishlistItem(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id,
            @RequestBody UpdateWishlistItemRequest request) {
        
        UUID userId = userIdPort.extractUserId(token.substring(7));
        
        try {
            // Verify ownership
            WishlistItem existingItem = getWishlistItemUseCase.getWishlistItemById(WishlistItemId.fromUUID(id))
                    .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));
            
            if (!existingItem.getUserId().getValue().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Update wishlist item
            WishlistItem updatedItem = updateWishlistItemUseCase.updateWishlistItem(
                    new UpdateWishlistItemCommand(
                            WishlistItemId.fromUUID(id),
                            request.description,
                            request.priority,
                            request.isPublic
                    )
            );
            
            // Fetch book details
            BookDTO bookDTO = getBookUseCase.getBookById(updatedItem.getBookId())
                    .map(BookDTO::fromDomain)
                    .orElse(null);
            
            return ResponseEntity.ok(WishlistItemDTO.fromDomainWithBook(updatedItem, bookDTO));
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update wishlist item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error updating wishlist item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromWishlist(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id) {
        
        UUID userId = userIdPort.extractUserId(token.substring(7));
        
        try {
            // Verify ownership
            WishlistItem existingItem = getWishlistItemUseCase.getWishlistItemById(WishlistItemId.fromUUID(id))
                    .orElse(null);
            
            if (existingItem == null) {
                // Item already deleted or not found
                return ResponseEntity.noContent().build();
            }
            
            if (!existingItem.getUserId().getValue().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            // Delete wishlist item
            deleteWishlistItemUseCase.deleteWishlistItem(WishlistItemId.fromUUID(id));
            
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Error removing from wishlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<WishlistItemDTO> getWishlistItemByBook(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID bookId) {
        
        UUID userId = userIdPort.extractUserId(token.substring(7));
        
        try {
            // Get wishlist item for book
            return getWishlistItemUseCase.getWishlistItemByUserIdAndBookId(
                            UserId.fromUUID(userId),
                            BookId.fromUUID(bookId)
                    )
                    .map(wishlistItem -> {
                        // Fetch book details
                        BookDTO bookDTO = getBookUseCase.getBookById(wishlistItem.getBookId())
                                .map(BookDTO::fromDomain)
                                .orElse(null);
                        
                        return ResponseEntity.ok()
                                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                                .header("Pragma", "no-cache")
                                .header("Expires", "0")
                                .body(WishlistItemDTO.fromDomainWithBook(wishlistItem, bookDTO));
                    })
                    .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            log.error("Error getting wishlist item by book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/check/{bookId}")
    public ResponseEntity<Boolean> checkBookInWishlist(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID bookId) {
        
        UUID userId = userIdPort.extractUserId(token.substring(7));
        
        try {
            boolean exists = getWishlistItemUseCase.existsByUserIdAndBookId(
                    UserId.fromUUID(userId),
                    BookId.fromUUID(bookId)
            );
            
            return ResponseEntity.ok(exists);
            
        } catch (Exception e) {
            log.error("Error checking book in wishlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get public wishlist items for a user by username.
     * 
     * @param username the username of the user
     * @return the list of public wishlist items
     */
    @GetMapping("/public/user/{username}")
    public ResponseEntity<List<WishlistItemDTO>> getPublicWishlistByUsername(@PathVariable String username) {
        try {
            List<WishlistItem> publicWishlist = getWishlistItemUseCase.getPublicWishlistItemsByUsername(username);
            
            List<WishlistItemDTO> wishlistItemDTOs = publicWishlist.stream()
                    .map(wishlistItem -> {
                        // Fetch book details
                        BookDTO bookDTO = getBookUseCase.getBookById(wishlistItem.getBookId())
                                .map(BookDTO::fromDomain)
                                .orElse(null);
                        
                        return WishlistItemDTO.fromDomainWithBook(wishlistItem, bookDTO);
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(wishlistItemDTOs);
        } catch (IllegalArgumentException e) {
            log.warn("User not found: {}", username, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving public wishlist for username: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Request DTOs
    @Data
    public static class AddToWishlistRequest {
        private UUID bookId;
        private String openLibraryKey;
        private String description;
        private Integer priority;
        private Boolean isPublic;
    }

    @Data
    public static class UpdateWishlistItemRequest {
        private String description;
        private Integer priority;
        private Boolean isPublic;
    }
} 