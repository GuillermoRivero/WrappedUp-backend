package com.wrappedup.backend.domain.port;

import com.wrappedup.backend.domain.Book;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(UUID id);
    Optional<Book> findByOpenLibraryKey(String openLibraryKey);
    List<Book> findAll();
    List<Book> search(String query);
    List<Book> searchByTitleOrAuthor(String query);
} 