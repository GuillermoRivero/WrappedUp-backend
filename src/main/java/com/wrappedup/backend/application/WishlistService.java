package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.WishlistItem;
import com.wrappedup.backend.domain.port.BookRepository;
import com.wrappedup.backend.domain.port.UserRepository;
import com.wrappedup.backend.domain.port.WishlistRepository;
import com.wrappedup.backend.infrastructure.controller.WishlistRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    @PersistenceContext
    private EntityManager entityManager;

    public WishlistService(WishlistRepository wishlistRepository, 
                           UserRepository userRepository, 
                           BookRepository bookRepository,
                           BookService bookService) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
    }

    public WishlistItem addToWishlist(UUID userId, UUID bookId, String description, Integer priority, Boolean isPublic) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (wishlistRepository.existsByUserAndBook(user, book)) {
            throw new IllegalStateException("Book already in wishlist");
        }

        WishlistItem wishlistItem = new WishlistItem(user, book, description, priority != null ? priority : 3, isPublic != null && isPublic);
        WishlistItem savedItem = wishlistRepository.save(wishlistItem);
        
        if (entityManager != null) {
            entityManager.flush();
            entityManager.refresh(savedItem);
        }
        
        return savedItem;
    }

    public WishlistItem addToWishlistByOpenLibraryKey(UUID userId, WishlistRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String normalizedKey = normalizeOpenLibraryKey(request.getOpenLibraryKey());
        
        Book book;
        try {
            book = bookService.findAndPersistBookByKey(normalizedKey);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching book from OpenLibrary: " + e.getMessage(), e);
        }

        Optional<WishlistItem> existingItem = wishlistRepository.findByUserIdAndBookId(userId, book.getId());
        if (existingItem.isPresent()) {
            return existingItem.get();
        }

        WishlistItem wishlistItem = new WishlistItem(
                user, 
                book, 
                request.getDescription(), 
                request.getPriority() != null ? request.getPriority() : 3, 
                request.getIsPublic() != null && request.getIsPublic()
        );
        
        return wishlistRepository.save(wishlistItem);
    }

    public List<WishlistItem> getUserWishlist(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return wishlistRepository.findAllByUser(user);
    }

    public List<WishlistItem> getPublicWishlistByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return wishlistRepository.findAllByUser(user).stream()
                .filter(WishlistItem::isPublic)
                .collect(Collectors.toList());
    }

    public Optional<WishlistItem> getWishlistItem(UUID userId, UUID wishlistItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        WishlistItem wishlistItem = wishlistRepository.findById(wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));
        
        if (!wishlistItem.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Wishlist item does not belong to user");
        }
        
        return Optional.of(wishlistItem);
    }

    public WishlistItem updateWishlistItem(UUID userId, UUID wishlistItemId, String description, Integer priority, Boolean isPublic) {
        WishlistItem wishlistItem = getWishlistItem(userId, wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));
        
        if (description != null) {
            wishlistItem.setDescription(description);
        }
        
        if (priority != null) {
            wishlistItem.setPriority(priority);
        }
        
        if (isPublic != null) {
            wishlistItem.setPublic(isPublic);
        }
        
        return wishlistRepository.save(wishlistItem);
    }

    public void removeFromWishlist(UUID userId, UUID wishlistItemId) {
        WishlistItem wishlistItem = getWishlistItem(userId, wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));
        
        wishlistRepository.delete(wishlistItem);
    }

    private String normalizeOpenLibraryKey(String key) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        
        if (!key.startsWith("/works/")) {
            if (key.startsWith("OL") && key.contains("W")) {
                return "/works/" + key;
            }
            
            return key;
        }
        
        return key;
    }


    @Transactional(readOnly = true)
    public List<WishlistItem> getPublicWishlistByUsername(String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
                        
            List<WishlistItem> items = wishlistRepository.findAllByUser(user).stream()
                    .filter(WishlistItem::isPublic)
                    .collect(Collectors.toList());
                                
            return items;
        } catch (Exception e) {
            log.error("Error getting public wishlist by username: {}", username, e);
            throw e;
        }
    }
    

    @Transactional(readOnly = true)
    public Optional<WishlistItem> getWishlistItemByUsername(String username, UUID wishlistItemId) {
        try {

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));
                        
            WishlistItem wishlistItem = wishlistRepository.findById(wishlistItemId)
                    .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));
            
            if (!wishlistItem.getUser().getEmail().equals(username)) {
                log.warn("Wishlist item {} does not belong to user {}", wishlistItemId, username);
                throw new IllegalArgumentException("Wishlist item does not belong to user");
            }
            
            return Optional.of(wishlistItem);
        } catch (Exception e) {
            log.error("Error getting wishlist item by username: {}, id: {}", username, wishlistItemId, e);
            throw e;
        }
    }
    

    public WishlistItem addToWishlistByUsername(String username, UUID bookId, String description, Integer priority, Boolean isPublic) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));
        
        return addToWishlist(user.getId(), bookId, description, priority, isPublic);
    }

    public WishlistItem addToWishlistByOpenLibraryKeyAndUsername(String username, WishlistRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));
        
        return addToWishlistByOpenLibraryKey(user.getId(), request);
    }
    
    public WishlistItem updateWishlistItemByUsername(String username, UUID wishlistItemId, String description, Integer priority, Boolean isPublic) {
        getWishlistItemByUsername(username, wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));
        
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));
        
        return updateWishlistItem(user.getId(), wishlistItemId, description, priority, isPublic);
    }
    
    public void removeFromWishlistByUsername(String username, UUID wishlistItemId) {
        getWishlistItemByUsername(username, wishlistItemId)
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found"));
        
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));
        
        removeFromWishlist(user.getId(), wishlistItemId);
    }
    
    @Transactional(readOnly = true)
    public List<WishlistItem> getUserWishlistByUsername(String username) {
        try {

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + username));
            
            List<WishlistItem> items = wishlistRepository.findAllByUser(user);
            
            return items;
        } catch (Exception e) {
            log.error("Error getting wishlist by username: {}", username, e);
            throw e;
        }
    }
} 