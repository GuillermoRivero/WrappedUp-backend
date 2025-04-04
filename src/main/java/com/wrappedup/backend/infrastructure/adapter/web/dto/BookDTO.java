package com.wrappedup.backend.infrastructure.adapter.web.dto;

import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for Book entities.
 */
public class BookDTO {
    private UUID id;
    private String title;
    private String subtitle;
    private String alternativeTitle;
    private String alternativeSubtitle;
    private String author;
    private String isbn;
    private String description;
    private String coverImageUrl;
    private Long coverId;
    private Integer pageCount;
    private List<String> genres;
    private String language;
    private LocalDate publicationDate;
    private Integer firstPublishYear;
    private String publisher;
    private String openLibraryKey;
    private String platform = "system";
    private String ebookAccess;
    private Integer editionCount;
    private String format;
    private String byStatement;
    private String firstSentence;
    private Boolean hasFulltext;
    private Integer ratingsCount;
    private Integer wantToReadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Maps a domain Book entity to a BookDTO
     */
    public static BookDTO fromDomain(Book book) {
        if (book == null) {
            return null;
        }
        
        BookDTO dto = new BookDTO();
        dto.setId(book.getId().getValue());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setFirstSentence(book.getDescription()); // For compatibility with old frontend
        dto.setCoverImageUrl(book.getCoverImageUrl());
        dto.setCoverUrl(book.getCoverImageUrl()); // For compatibility with old frontend
        dto.setPageCount(book.getPageCount());
        dto.setNumberOfPagesMedian(book.getPageCount()); // For compatibility with old frontend
        dto.setGenres(book.getGenres() != null ? new ArrayList<>(book.getGenres()) : new ArrayList<>());
        dto.setLanguage(book.getLanguage());
        dto.setPublicationDate(book.getPublicationDate());
        
        // Set firstPublishYear from publicationDate if available
        if (book.getPublicationDate() != null) {
            dto.setFirstPublishYear(book.getPublicationDate().getYear());
        }
        
        dto.setPublisher(book.getPublisher());
        dto.setOpenLibraryKey(book.getOpenLibraryKey());
        dto.setPlatform("system");
        dto.setCreatedAt(book.getCreatedAt());
        dto.setUpdatedAt(book.getUpdatedAt());
        
        return dto;
    }

    /**
     * Maps a BookDTO to a domain Book entity
     */
    public Book toDomain() {
        return Book.reconstitute(
            id != null ? BookId.of(id) : BookId.generate(),
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
            createdAt != null ? createdAt : LocalDateTime.now(),
            updatedAt != null ? updatedAt : LocalDateTime.now()
        );
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAlternativeTitle() {
        return alternativeTitle;
    }

    public void setAlternativeTitle(String alternativeTitle) {
        this.alternativeTitle = alternativeTitle;
    }

    public String getAlternativeSubtitle() {
        return alternativeSubtitle;
    }

    public void setAlternativeSubtitle(String alternativeSubtitle) {
        this.alternativeSubtitle = alternativeSubtitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Long getCoverId() {
        return coverId;
    }

    public void setCoverId(Long coverId) {
        this.coverId = coverId;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Integer getFirstPublishYear() {
        return firstPublishYear;
    }

    public void setFirstPublishYear(Integer firstPublishYear) {
        this.firstPublishYear = firstPublishYear;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getOpenLibraryKey() {
        return openLibraryKey;
    }

    public void setOpenLibraryKey(String openLibraryKey) {
        this.openLibraryKey = openLibraryKey;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCoverUrl() {
        return coverImageUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverImageUrl = coverUrl;
    }

    public String getEbookAccess() {
        return ebookAccess;
    }

    public void setEbookAccess(String ebookAccess) {
        this.ebookAccess = ebookAccess;
    }

    public Integer getEditionCount() {
        return editionCount;
    }

    public void setEditionCount(Integer editionCount) {
        this.editionCount = editionCount;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getByStatement() {
        return byStatement;
    }

    public void setByStatement(String byStatement) {
        this.byStatement = byStatement;
    }

    public String getFirstSentence() {
        return firstSentence;
    }

    public void setFirstSentence(String firstSentence) {
        this.firstSentence = firstSentence;
    }

    public Boolean getHasFulltext() {
        return hasFulltext;
    }

    public void setHasFulltext(Boolean hasFulltext) {
        this.hasFulltext = hasFulltext;
    }

    public Integer getNumberOfPagesMedian() {
        return pageCount;
    }

    public void setNumberOfPagesMedian(Integer numberOfPagesMedian) {
        this.pageCount = numberOfPagesMedian;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Integer ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public Integer getWantToReadCount() {
        return wantToReadCount;
    }

    public void setWantToReadCount(Integer wantToReadCount) {
        this.wantToReadCount = wantToReadCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 