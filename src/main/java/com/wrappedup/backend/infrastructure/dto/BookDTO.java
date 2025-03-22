package com.wrappedup.backend.infrastructure.dto;

import com.wrappedup.backend.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private UUID id;
    private String title;
    private String subtitle;
    private String alternativeTitle;
    private String alternativeSubtitle;
    private String author;
    private Integer firstPublishYear;
    private String platform;
    private String coverUrl;
    private Long coverId;
    private String openLibraryKey;
    private String ebookAccess;
    private Integer editionCount;
    private String format;
    private String byStatement;
    private String firstSentence;
    private Boolean hasFulltext;
    private Integer numberOfPagesMedian;
    private Integer ratingsCount;
    private Integer wantToReadCount;
    
    public static BookDTO fromEntity(Book book) {
        if (book == null) {
            return null;
        }
        
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .subtitle(book.getSubtitle())
                .alternativeTitle(book.getAlternativeTitle())
                .alternativeSubtitle(book.getAlternativeSubtitle())
                .author(book.getAuthor())
                .firstPublishYear(book.getFirstPublishYear())
                .platform(book.getPlatform())
                .coverUrl(book.getCoverUrl())
                .coverId(book.getCoverId())
                .openLibraryKey(book.getOpenLibraryKey())
                .ebookAccess(book.getEbookAccess())
                .editionCount(book.getEditionCount())
                .format(book.getFormat())
                .byStatement(book.getByStatement())
                .firstSentence(book.getFirstSentence())
                .hasFulltext(book.getHasFulltext())
                .numberOfPagesMedian(book.getNumberOfPagesMedian())
                .ratingsCount(book.getRatingsCount())
                .wantToReadCount(book.getWantToReadCount())
                .build();
    }
} 