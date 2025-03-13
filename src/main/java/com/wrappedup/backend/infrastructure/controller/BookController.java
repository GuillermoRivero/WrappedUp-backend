package com.wrappedup.backend.infrastructure.controller;

import com.wrappedup.backend.application.AddBookUseCase;
import com.wrappedup.backend.application.GetBookInfoUseCase;
import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.infrastructure.service.OpenLibraryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {

    private final AddBookUseCase addBookUseCase;
    private final GetBookInfoUseCase getBookInfoUseCase;
    private final OpenLibraryService openLibraryService;

    public BookController(
            AddBookUseCase addBookUseCase,
            GetBookInfoUseCase getBookInfoUseCase,
            OpenLibraryService openLibraryService) {
        this.addBookUseCase = addBookUseCase;
        this.getBookInfoUseCase = getBookInfoUseCase;
        this.openLibraryService = openLibraryService;
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        return ResponseEntity.ok(addBookUseCase.execute(book));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable UUID id) {
        return getBookInfoUseCase.execute(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(openLibraryService.searchBooks(query));
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(getBookInfoUseCase.findAll());
    }
} 