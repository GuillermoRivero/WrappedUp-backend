package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.WishlistItem;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataWishlistRepository extends JpaRepository<WishlistItem, UUID> {
    List<WishlistItem> findAllByUser(User user);
    Optional<WishlistItem> findByUserAndBook(User user, Book book);
    boolean existsByUserAndBook(User user, Book book);
} 