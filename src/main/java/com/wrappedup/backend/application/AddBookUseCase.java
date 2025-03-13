package com.wrappedup.backend.application;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddBookUseCase {
    private final BookRepository bookRepository;

    public Book execute(Book book) {
        return bookRepository.save(book);
    }
} 