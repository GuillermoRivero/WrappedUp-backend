package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.User;
import com.wrappedup.backend.domain.model.Username;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;
import com.wrappedup.backend.domain.port.in.CreateWishlistItemUseCase;
import com.wrappedup.backend.domain.port.in.DeleteWishlistItemUseCase;
import com.wrappedup.backend.domain.port.in.GetWishlistItemUseCase;
import com.wrappedup.backend.domain.port.in.UpdateWishlistItemUseCase;
import com.wrappedup.backend.domain.port.out.WishlistItemRepository;
import com.wrappedup.backend.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WishlistItemService implements 
        CreateWishlistItemUseCase, 
        GetWishlistItemUseCase, 
        UpdateWishlistItemUseCase, 
        DeleteWishlistItemUseCase {

    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;

    @Override
    public WishlistItemId createWishlistItem(CreateWishlistItemCommand command) {
        // Check if wishlist item already exists for this user and book
        if (wishlistItemRepository.existsByUserIdAndBookId(command.userId(), command.bookId())) {
            log.warn("Wishlist item already exists for user {} and book {}", command.userId(), command.bookId());
            throw new IllegalStateException(
                    "Wishlist item already exists for this book"
            );
        }

        // Create new wishlist item
        WishlistItem wishlistItem = WishlistItem.createNewWishlistItem(
                command.userId(),
                command.bookId(),
                command.description(),
                command.priority(),
                command.isPublic()
        );

        // Save wishlist item
        WishlistItem savedWishlistItem = wishlistItemRepository.save(wishlistItem);
        log.info("Created wishlist item with ID: {}", savedWishlistItem.getId());

        return savedWishlistItem.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistItem> getWishlistItemById(WishlistItemId id) {
        return wishlistItemRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItem> getWishlistItemsByUserId(UserId userId) {
        return wishlistItemRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistItem> getPublicWishlistItemsByUsername(String username) {
        try {
            // First, we need to get the user ID from the username
            Username usernameObj = new Username(username);
            UserId userId = userRepository.findByUsername(usernameObj)
                    .map(User::getId)
                    .orElseThrow(() -> {
                        log.warn("User not found with username: {}", username);
                        return new IllegalArgumentException("User not found: " + username);
                    });
            
            // Then get all wishlist items for the user and filter to only return public ones
            return wishlistItemRepository.findAllByUserId(userId)
                    .stream()
                    .filter(WishlistItem::isPublic)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting public wishlist for username: {}", username, e);
            throw new IllegalArgumentException("Error retrieving public wishlist: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistItem> getWishlistItemByUserIdAndBookId(UserId userId, BookId bookId) {
        return wishlistItemRepository.findByUserIdAndBookId(userId, bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndBookId(UserId userId, BookId bookId) {
        return wishlistItemRepository.existsByUserIdAndBookId(userId, bookId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public WishlistItem updateWishlistItem(UpdateWishlistItemCommand command) {
        log.info("Starting update process for wishlist item with ID: {}", command.id());
        log.info("Update details - description: {}, priority: {}, isPublic: {}", 
                 command.description(), command.priority(), command.isPublic());
        
        // Find wishlist item
        WishlistItem wishlistItem = wishlistItemRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Wishlist item not found: " + command.id()));
        
        log.info("Original wishlist item found: description: {}, priority: {}, isPublic: {}", 
                wishlistItem.getDescription(), wishlistItem.getPriority(), wishlistItem.isPublic());
        
        // Update wishlist item details
        wishlistItem.updateDetails(
                command.description(),
                command.priority(),
                command.isPublic()
        );
        
        log.info("After updating domain object: description: {}, priority: {}, isPublic: {}", 
                wishlistItem.getDescription(), wishlistItem.getPriority(), wishlistItem.isPublic());
        
        // Save updated wishlist item
        WishlistItem updatedWishlistItem = wishlistItemRepository.save(wishlistItem);
        log.info("After repository save: description: {}, priority: {}, isPublic: {}", 
                updatedWishlistItem.getDescription(), updatedWishlistItem.getPriority(), updatedWishlistItem.isPublic());
        
        return updatedWishlistItem;
    }

    @Override
    @Transactional
    public void deleteWishlistItem(WishlistItemId id) {
        log.info("Starting deletion process for wishlist item with ID: {}", id);
        
        // Check if wishlist item exists
        Optional<WishlistItem> wishlistItemOptional = wishlistItemRepository.findById(id);
        
        if (wishlistItemOptional.isEmpty()) {
            log.warn("Wishlist item not found for deletion: {}", id);
            return; // Silently ignore if not found
        }
        
        try {
            // Delete wishlist item
            wishlistItemRepository.deleteById(id);
            log.info("Successfully requested deletion of wishlist item with ID: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete wishlist item with ID: {}. Error: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete wishlist item", e);
        }
    }
} 