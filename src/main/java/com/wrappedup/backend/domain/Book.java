package com.wrappedup.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book {
    @Id
    @GeneratedValue
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column
    private String subtitle;

    @Column(name = "alternative_title")
    private String alternativeTitle;

    @Column(name = "alternative_subtitle")
    private String alternativeSubtitle;

    @NotBlank
    @Column(nullable = false)
    private String author;

    @ElementCollection
    @CollectionTable(name = "book_author_alternative_names", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author_name")
    private List<String> authorAlternativeNames;

    @NotNull
    @Min(1000)
    @Column(name = "first_publish_year", nullable = false)
    private Integer firstPublishYear;

    @ElementCollection
    @CollectionTable(name = "book_publish_years", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publish_year")
    private List<Integer> publishYears;

    @NotBlank
    @Column(nullable = false)
    private String platform;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "cover_i")
    private Long coverId;

    @Column(name = "open_library_key")
    private String openLibraryKey;

    @ElementCollection
    @CollectionTable(name = "book_redirects", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "redirect")
    private List<String> redirects;

    @Column(name = "ebook_access")
    private String ebookAccess;

    @Column(name = "edition_count")
    private Integer editionCount;

    @ElementCollection
    @CollectionTable(name = "book_edition_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "edition_key")
    private List<String> editionKeys;

    @Column
    private String format;

    @Column(name = "by_statement")
    private String byStatement;

    @ElementCollection
    @CollectionTable(name = "book_publish_dates", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publish_date")
    private List<String> publishDates;

    @ElementCollection
    @CollectionTable(name = "book_lccns", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "lccn")
    private List<String> lccns;

    @ElementCollection
    @CollectionTable(name = "book_ia_ids", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "ia_id")
    private List<String> iaIds;

    @ElementCollection
    @CollectionTable(name = "book_oclcs", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "oclc")
    private List<String> oclcs;

    @ElementCollection
    @CollectionTable(name = "book_isbns", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "isbn")
    private List<String> isbns;

    @ElementCollection
    @CollectionTable(name = "book_contributors", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "contributor")
    private List<String> contributors;

    @ElementCollection
    @CollectionTable(name = "book_publish_places", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publish_place")
    private List<String> publishPlaces;

    @ElementCollection
    @CollectionTable(name = "book_publishers", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publisher")
    private List<String> publishers;

    @Column(name = "first_sentence", columnDefinition = "TEXT")
    private String firstSentence;

    @ElementCollection
    @CollectionTable(name = "book_author_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author_key")
    private List<String> authorKeys;

    @ElementCollection
    @CollectionTable(name = "book_author_names", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author_name")
    private List<String> authorNames;

    @ElementCollection
    @CollectionTable(name = "book_subjects", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "subject_name")
    private List<String> subjects;

    @ElementCollection
    @CollectionTable(name = "book_persons", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "person_name")
    private List<String> persons;

    @ElementCollection
    @CollectionTable(name = "book_places", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "place_name")
    private List<String> places;

    @ElementCollection
    @CollectionTable(name = "book_times", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "time_name")
    private List<String> times;

    @Column(name = "has_fulltext")
    private Boolean hasFulltext;

    @ElementCollection
    @CollectionTable(name = "book_languages", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "language_name")
    private List<String> languages;

    @Column(name = "number_of_pages_median")
    private Integer numberOfPagesMedian;

    @Column(name = "ia_count")
    private Integer iaCount;

    @Column(name = "ratings_count")
    private Integer ratingsCount;

    @Column(name = "readinglog_count")
    private Integer readinglogCount;

    @Column(name = "want_to_read_count")
    private Integer wantToReadCount;

    @Column(name = "currently_reading_count")
    private Integer currentlyReadingCount;

    @Column(name = "already_read_count")
    private Integer alreadyReadCount;

    @ElementCollection
    @CollectionTable(name = "book_subject_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "subject_key")
    private List<String> subjectKeys;

    @ElementCollection
    @CollectionTable(name = "book_person_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "person_key")
    private List<String> personKeys;

    @ElementCollection
    @CollectionTable(name = "book_place_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "place_key")
    private List<String> placeKeys;

    @ElementCollection
    @CollectionTable(name = "book_time_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "time_key")
    private List<String> timeKeys;

    @ElementCollection
    @CollectionTable(name = "book_lccs", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "lcc")
    private List<String> lccs;

    @ElementCollection
    @CollectionTable(name = "book_ddcs", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "ddc")
    private List<String> ddcs;

    @Column(name = "lcc_sort")
    private String lccSort;

    @Column(name = "ddc_sort")
    private String ddcSort;

    public Book(
            String title,
            String author,
            int firstPublishYear,
            String platform,
            String coverUrl,
            String openLibraryKey
    ) {
        this.title = title;
        this.author = author;
        this.firstPublishYear = firstPublishYear;
        this.platform = platform;
        this.coverUrl = coverUrl;
        this.openLibraryKey = openLibraryKey;
    }
} 