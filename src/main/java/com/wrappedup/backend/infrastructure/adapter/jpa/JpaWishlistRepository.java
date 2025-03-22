package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.WishlistItem;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaWishlistRepository implements WishlistRepository {

    private final SpringDataWishlistRepository repository;

    @Override
    public WishlistItem save(WishlistItem wishlistItem) {
        return repository.save(wishlistItem);
    }

    @Override
    public Optional<WishlistItem> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<WishlistItem> findAllByUser(User user) {
        return repository.findAllByUser(user);
    }

    @Override
    public Optional<WishlistItem> findByUserAndBook(User user, Book book) {
        return repository.findByUserAndBook(user, book);
    }

    @Override
    public Optional<WishlistItem> findByUserIdAndBookId(UUID userId, UUID bookId) {
        // Implementación personalizada para buscar por IDs
        return repository.findAll().stream()
                .filter(item -> item.getUser().getId().equals(userId) && 
                         item.getBook().getId().equals(bookId))
                .findFirst();
    }

    @Override
    public void delete(WishlistItem wishlistItem) {
        repository.delete(wishlistItem);
    }

    @Override
    public boolean existsByUserAndBook(User user, Book book) {
        return repository.existsByUserAndBook(user, book);
    }
} 