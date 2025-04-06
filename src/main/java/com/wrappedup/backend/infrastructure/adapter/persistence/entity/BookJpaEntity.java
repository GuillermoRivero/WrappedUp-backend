package com.wrappedup.backend.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for Book persistence.
 */
@Entity
@Table(name = "books", 
       indexes = {
           @Index(name = "idx_open_library_key", columnList = "open_library_key", unique = true)
       })
@Getter
@Setter
@NoArgsConstructor
@OptimisticLocking(type = OptimisticLockType.NONE)
public class BookJpaEntity {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
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

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_author_alternative_names", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author_name")
    private List<String> authorAlternativeNames = new ArrayList<>();

    @Column(name = "first_publish_year", nullable = true)
    private Integer firstPublishYear;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_publish_years", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publish_year")
    private List<Integer> publishYears = new ArrayList<>();

    @Column(nullable = false)
    private String platform;

    @Column(name = "cover_url")
    private String coverUrl;

    @Column(name = "cover_i")
    private Long coverId;

    @Column(name = "open_library_key")
    private String openLibraryKey;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_redirects", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "redirect")
    private List<String> redirects = new ArrayList<>();

    @Column(name = "ebook_access")
    private String ebookAccess;

    @Column(name = "edition_count")
    private Integer editionCount;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_edition_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "edition_key")
    private List<String> editionKeys = new ArrayList<>();

    @Column
    private String format;

    @Column(name = "by_statement")
    private String byStatement;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_publish_dates", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publish_date")
    private List<String> publishDates = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_lccns", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "lccn")
    private List<String> lccns = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_ia_ids", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "ia_id")
    private List<String> iaIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_oclcs", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "oclc")
    private List<String> oclcs = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_isbns", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "isbn")
    private List<String> isbns = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    private List<String> genres = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_contributors", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "contributor")
    private List<String> contributors = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_publish_places", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publish_place")
    private List<String> publishPlaces = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_publishers", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "publisher")
    private List<String> publishers = new ArrayList<>();

    @Column(name = "first_sentence", columnDefinition = "TEXT")
    private String firstSentence;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_author_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author_key")
    private List<String> authorKeys = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_author_names", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "author_name")
    private List<String> authorNames = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_subjects", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "subject_name")
    private List<String> subjects = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_persons", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "person_name")
    private List<String> persons = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_places", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "place_name")
    private List<String> places = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_times", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "time_name")
    private List<String> times = new ArrayList<>();

    @Column(name = "has_fulltext")
    private Boolean hasFulltext;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_languages", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "language_name")
    private List<String> languages = new ArrayList<>();

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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_subject_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "subject_key")
    private List<String> subjectKeys = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_person_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "person_key")
    private List<String> personKeys = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_place_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "place_key")
    private List<String> placeKeys = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_time_keys", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "time_key")
    private List<String> timeKeys = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_lccs", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "lcc")
    private List<String> lccs = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_ddcs", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "ddc")
    private List<String> ddcs = new ArrayList<>();

    @Column(name = "lcc_sort")
    private String lccSort;

    @Column(name = "ddc_sort")
    private String ddcSort;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Explicitly mark that no version field should be used
    @Transient
    private transient Object ignoreVersion;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 