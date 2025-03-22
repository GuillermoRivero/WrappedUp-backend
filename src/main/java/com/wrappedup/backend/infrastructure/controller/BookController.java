package com.wrappedup.backend.infrastructure.controller;

import com.wrappedup.backend.application.AddBookUseCase;
import com.wrappedup.backend.application.BookService;
import com.wrappedup.backend.application.GetBookInfoUseCase;
import com.wrappedup.backend.application.WishlistService;
import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.infrastructure.dto.BookDTO;
import com.wrappedup.backend.infrastructure.service.OpenLibraryService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {

    private final AddBookUseCase addBookUseCase;
    private final GetBookInfoUseCase getBookInfoUseCase;
    private final OpenLibraryService openLibraryService;
    private final WishlistService wishlistService;
    private final BookService bookService;

    public BookController(
            AddBookUseCase addBookUseCase,
            GetBookInfoUseCase getBookInfoUseCase,
            OpenLibraryService openLibraryService,
            WishlistService wishlistService,
            BookService bookService) {
        this.addBookUseCase = addBookUseCase;
        this.getBookInfoUseCase = getBookInfoUseCase;
        this.openLibraryService = openLibraryService;
        this.wishlistService = wishlistService;
        this.bookService = bookService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<BookDTO> addBook(@Valid @RequestBody Book book) {
        Book savedBook = bookService.save(book);
        return ResponseEntity.created(null).body(BookDTO.fromEntity(savedBook));
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<BookDTO> getBook(@PathVariable UUID id) {
        return bookService.findById(id)
                .map(BookDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Transactional
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String query) {
        List<Book> books = bookService.searchInOpenLibrary(query);
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<Book> books = bookService.findAll();
        List<BookDTO> bookDTOs = books.stream()
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookDTOs);
    }

    @GetMapping("/openlibrary/{key}")
    @Transactional
    public ResponseEntity<BookDTO> findOrFetchBookByOpenLibraryKey(@PathVariable String key) {
        try {
            Book book = bookService.findAndPersistBookByKey(key);
            return ResponseEntity.ok(BookDTO.fromEntity(book));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
} 