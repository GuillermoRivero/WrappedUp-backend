package com.wrappedup.backend.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Domain entity representing a book in the system.
 */
public class Book {
    private final BookId id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String coverImageUrl;
    private Integer pageCount;
    private List<String> genres;
    private String language;
    private LocalDate publicationDate;
    private String publisher;
    private String openLibraryKey;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Book(
            BookId id,
            String title,
            String author,
            String isbn,
            String description,
            String coverImageUrl,
            Integer pageCount,
            List<String> genres,
            String language,
            LocalDate publicationDate,
            String publisher,
            String openLibraryKey,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "Book ID cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.author = Objects.requireNonNull(author, "Author cannot be null");
        this.isbn = isbn;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.pageCount = pageCount;
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
        this.language = language;
        this.publicationDate = publicationDate;
        this.publisher = publisher;
        this.openLibraryKey = openLibraryKey;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated at cannot be null");
    }
    
    /**
     * Creates a new book with generated ID.
     */
    public static Book createNewBook(
            String title,
            String author,
            String isbn,
            String description,
            String coverImageUrl,
            Integer pageCount,
            List<String> genres,
            String language,
            LocalDate publicationDate,
            String publisher,
            String openLibraryKey) {
        LocalDateTime now = LocalDateTime.now();
        return new Book(
                BookId.generate(),
                title,
                author,
                isbn,
                description,
                coverImageUrl,
                pageCount,
                genres,
                language,
                publicationDate,
                publisher,
                openLibraryKey,
                now,
                now
        );
    }
    
    /**
     * Reconstructs an existing book from persistence.
     */
    public static Book reconstitute(
            BookId id,
            String title,
            String author,
            String isbn,
            String description,
            String coverImageUrl,
            Integer pageCount,
            List<String> genres,
            String language,
            LocalDate publicationDate,
            String publisher,
            String openLibraryKey,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        return new Book(
                id,
                title,
                author,
                isbn,
                description,
                coverImageUrl,
                pageCount,
                genres,
                language,
                publicationDate,
                publisher,
                openLibraryKey,
                createdAt,
                updatedAt
        );
    }
    
    // Domain behavior methods
    
    public void updateDetails(
            String title,
            String author,
            String isbn,
            String description,
            String coverImageUrl,
            Integer pageCount,
            List<String> genres,
            String language,
            LocalDate publicationDate,
            String publisher,
            String openLibraryKey) {
        if (title != null) {
            this.title = title;
        }
        if (author != null) {
            this.author = author;
        }
        if (isbn != null) {
            this.isbn = isbn;
        }
        if (description != null) {
            this.description = description;
        }
        if (coverImageUrl != null) {
            this.coverImageUrl = coverImageUrl;
        }
        if (pageCount != null) {
            this.pageCount = pageCount;
        }
        if (genres != null) {
            this.genres = new ArrayList<>(genres);
        }
        if (language != null) {
            this.language = language;
        }
        if (publicationDate != null) {
            this.publicationDate = publicationDate;
        }
        if (publisher != null) {
            this.publisher = publisher;
        }
        if (openLibraryKey != null) {
            this.openLibraryKey = openLibraryKey;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    
    public BookId getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public Integer getPageCount() {
        return pageCount;
    }
    
    public List<String> getGenres() {
        return new ArrayList<>(genres);
    }
    
    public String getLanguage() {
        return language;
    }
    
    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public String getOpenLibraryKey() {
        return openLibraryKey;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Object methods
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                '}';
    }
} 