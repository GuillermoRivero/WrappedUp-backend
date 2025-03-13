package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetBookInfoUseCase {
    
    private final BookRepository bookRepository;

    public Optional<Book> execute(UUID id) {
        return bookRepository.findById(id);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String query) {
        return bookRepository.searchByTitleOrAuthor(query);
    }
} 