package com.wrappedup.backend.infrastructure.adapter.web.dto;

import com.wrappedup.backend.domain.model.Review;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Review.
 */
@Data
@Builder
public class ReviewDTO {
    private UUID id;
    private UUID userId;
    private UUID bookId;
    private int rating;
    private String content;
    private String text;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Book information as a nested object
    private BookInfo book;
    
    @Data
    @Builder
    public static class BookInfo {
        private UUID id;
        private String title;
        private String author;
        private String coverUrl;
        private String openLibraryKey;
        private Integer releaseYear;
        private String description;
        private Integer numberOfPages;
        private String isbn;
    }

    /**
     * Maps a domain Review to a ReviewDTO.
     */
    public static ReviewDTO fromDomain(Review review) {
        return ReviewDTO.builder()
                .id(review.getId().getValue())
                .userId(review.getUserId().getValue())
                .bookId(review.getBookId().getValue())
                .rating(review.getRating())
                .content(review.getContent())
                .text(review.getContent())
                .startDate(review.getStartDate())
                .endDate(review.getEndDate())
                .isPublic(review.isPublic())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
} 