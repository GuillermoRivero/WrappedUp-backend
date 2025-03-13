package com.wrappedup.backend.infrastructure.adapter.jpa;

import com.wrappedup.backend.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataBookRepository extends JpaRepository<Book, UUID> {
    List<Book> findByTitleLikeIgnoreCaseOrAuthorLikeIgnoreCase(String titlePattern, String authorPattern);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    Optional<Book> findByOpenLibraryKey(String openLibraryKey);
} 