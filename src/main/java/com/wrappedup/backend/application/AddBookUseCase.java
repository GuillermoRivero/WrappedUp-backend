package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import com.wrappedup.backend.domain.util.BookIdGenerator;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class AddBookUseCase {
    private final BookRepository bookRepository;

    public Book execute(Book book) {
        if (book.getId() == null && book.getOpenLibraryKey() != null && !book.getOpenLibraryKey().isEmpty()) {
            book.setId(BookIdGenerator.generateUUIDFromKey(book.getOpenLibraryKey()));
        }
        return bookRepository.save(book);
    }
} 