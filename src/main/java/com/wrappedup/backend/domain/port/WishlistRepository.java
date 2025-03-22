package com.wrappedup.backend.domain.port;

import com.wrappedup.backend.domain.WishlistItem;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository {
    WishlistItem save(WishlistItem wishlistItem);
    Optional<WishlistItem> findById(UUID id);
    List<WishlistItem> findAllByUser(User user);
    Optional<WishlistItem> findByUserAndBook(User user, Book book);
    Optional<WishlistItem> findByUserIdAndBookId(UUID userId, UUID bookId);
    void delete(WishlistItem wishlistItem);
    boolean existsByUserAndBook(User user, Book book);
} 