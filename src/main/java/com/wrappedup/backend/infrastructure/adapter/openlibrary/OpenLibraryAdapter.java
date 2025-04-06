package com.wrappedup.backend.infrastructure.adapter.openlibrary;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.port.out.OpenLibraryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adapter implementation for OpenLibrary operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OpenLibraryAdapter implements OpenLibraryPort {
    
    private static final String SEARCH_URL = "https://openlibrary.org/search.json";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/";
    private static final String WORKS_API_URL = "https://openlibrary.org";
    
    private final RestTemplate restTemplate;
    
    @Override
    public List<Book> searchBooks(String query) {
        return searchBooks(query, false);
    }
    
    /**
     * Search for books with a flag to prevent recursive calls
     * @param query The search query
     * @param isRecursiveCall Flag to prevent infinite recursion
     * @return List of books matching the query
     */
    private List<Book> searchBooks(String query, boolean isRecursiveCall) {
        if (!isRecursiveCall && query.startsWith("key:")) {
            String key = query.substring(4);
            
            List<Book> directResult = getBookByKey(key);
            if (!directResult.isEmpty()) {
                return directResult;
            }
        }
        
        String url;
        
        if (query.startsWith("key:")) {
            String key = query.substring(4); 
            url = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                    .queryParam("q", "key:" + key) 
                    .queryParam("mode", "everything")
                    .queryParam("fields", String.join(",", 
                        "key", "redirects", "title", "subtitle", "alternative_title", "alternative_subtitle",
                        "cover_i", "ebook_access", "edition_count", "edition_key", "format", "by_statement",
                        "publish_date", "lccn", "ia", "oclc", "isbn", "contributor", "publish_place",
                        "publisher", "first_sentence", "author_key", "author_name", "author_alternative_name",
                        "subject", "person", "place", "time", "has_fulltext", "title_suggest", "publish_year",
                        "language", "number_of_pages_median", "ia_count", "publisher_facet", "author_facet",
                        "first_publish_year", "ratings_count", "readinglog_count", "want_to_read_count",
                        "currently_reading_count", "already_read_count", "subject_key", "person_key",
                        "place_key", "time_key", "lcc", "ddc", "lcc_sort", "ddc_sort"
                    ))
                    .build()
                    .toUriString();
        } else {
            url = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
                    .queryParam("q", query)
                    .queryParam("fields", String.join(",", 
                        "key", "redirects", "title", "subtitle", "alternative_title", "alternative_subtitle",
                        "cover_i", "ebook_access", "edition_count", "edition_key", "format", "by_statement",
                        "publish_date", "lccn", "ia", "oclc", "isbn", "contributor", "publish_place",
                        "publisher", "first_sentence", "author_key", "author_name", "author_alternative_name",
                        "subject", "person", "place", "time", "has_fulltext", "title_suggest", "publish_year",
                        "language", "number_of_pages_median", "ia_count", "publisher_facet", "author_facet",
                        "first_publish_year", "ratings_count", "readinglog_count", "want_to_read_count",
                        "currently_reading_count", "already_read_count", "subject_key", "person_key",
                        "place_key", "time_key", "lcc", "ddc", "lcc_sort", "ddc_sort"
                    ))
                    .build()
                    .toUriString();
        }
        
        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response == null || !response.has("docs")) {
                log.warn("No results found for query: {}", query);
                return Collections.emptyList();
            }
    
            return StreamSupport.stream(response.get("docs").spliterator(), false)
                    .map(this::mapToBook)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching books: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Book> getBookByKey(String openLibraryKey) {
        try {
            openLibraryKey = openLibraryKey.trim();
            if (!openLibraryKey.startsWith("/works/")) {
                if (openLibraryKey.matches("OL\\d+W")) {
                    openLibraryKey = "/works/" + openLibraryKey;
                } 
                else if (openLibraryKey.startsWith("works/")) {
                    openLibraryKey = "/" + openLibraryKey;
                }
                else {
                    log.warn("Invalid OpenLibrary key format: '{}'", openLibraryKey);
                }
            }
            
            String url = WORKS_API_URL + openLibraryKey + ".json";
            final String finalOpenLibraryKey = openLibraryKey; // Create final variable for lambda

            JsonNode workData;
            try {
                workData = restTemplate.getForObject(url, JsonNode.class);
            } catch (Exception e) {
                log.error("Error requesting OpenLibrary: {} - {}", e.getClass().getName(), e.getMessage(), e);
                return searchAlternative(finalOpenLibraryKey);
            }
            
            if (workData == null) {
                log.warn("API response is null for key: '{}'", openLibraryKey);
                return searchAlternative(finalOpenLibraryKey);
            }
            
            String title;
            try {
                title = workData.has("title") ? workData.get("title").asText() : "Unknown Title";
            } catch (Exception e) {
                log.error("Error extracting title: {}", e.getMessage(), e);
                title = "Unknown Title";
            }
            
            int firstPublishYear = 0;
            try {
                if (workData.has("first_publish_date")) {
                    String dateStr = workData.get("first_publish_date").asText();
                    if (dateStr.matches(".*\\d{4}.*")) {
                        firstPublishYear = Integer.parseInt(dateStr.replaceAll(".*?(\\d{4}).*", "$1"));
                    }
                }
            } catch (Exception e) {
                log.error("Error extracting publication year: {}", e.getMessage(), e);
            }
            
            String authorName = "Unknown Author";
            try {
                if (workData.has("authors") && workData.get("authors").isArray() && workData.get("authors").size() > 0) {
                    JsonNode authorNode = workData.get("authors").get(0);
                    
                    if (authorNode.has("author") && authorNode.get("author").has("key")) {
                        String authorKey = authorNode.get("author").get("key").asText();
                        try {
                            String authorUrl = WORKS_API_URL + authorKey + ".json";
                            JsonNode authorData = restTemplate.getForObject(authorUrl, JsonNode.class);
                            if (authorData != null && authorData.has("name")) {
                                authorName = authorData.get("name").asText();
                            } else {
                                authorName = "Author: " + authorKey.replace("/authors/", "");
                            }
                        } catch (Exception e) {
                            log.warn("Error getting author information: {}", e.getMessage());
                            authorName = "Author: " + authorKey.replace("/authors/", "");
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error processing author information: {}", e.getMessage(), e);
            }
            
            String coverUrl = null;
            try {
                if (workData.has("covers") && workData.get("covers").isArray() && workData.get("covers").size() > 0) {
                    long coverId = workData.get("covers").get(0).asLong();
                    coverUrl = COVER_URL + coverId + "-L.jpg";
                }
            } catch (Exception e) {
                log.error("Error extracting cover: {}", e.getMessage(), e);
            }
            
            // Calculate the description
            String description = null;
            try {
                if (workData.has("description")) {
                    if (workData.get("description").isTextual()) {
                        description = workData.get("description").asText();
                    } else if (workData.get("description").has("value")) {
                        description = workData.get("description").get("value").asText();
                    }
                }
            } catch (Exception e) {
                log.error("Error extracting description: {}", e.getMessage(), e);
            }
            
            List<String> genres = new ArrayList<>();
            try {
                if (workData.has("subjects") && workData.get("subjects").isArray()) {
                    workData.get("subjects").forEach(subject -> 
                        genres.add(subject.asText()));
                }
            } catch (Exception e) {
                log.error("Error extracting genres: {}", e.getMessage(), e);
            }
            
            // Get primary ISBN if available
            String isbn = null;
            try {
                if (workData.has("identifiers") && workData.get("identifiers").has("isbn_10") && 
                    workData.get("identifiers").get("isbn_10").isArray() && 
                    workData.get("identifiers").get("isbn_10").size() > 0) {
                    isbn = workData.get("identifiers").get("isbn_10").get(0).asText();
                } else if (workData.has("identifiers") && workData.get("identifiers").has("isbn_13") && 
                    workData.get("identifiers").get("isbn_13").isArray() && 
                    workData.get("identifiers").get("isbn_13").size() > 0) {
                    isbn = workData.get("identifiers").get("isbn_13").get(0).asText();
                }
            } catch (Exception e) {
                log.error("Error extracting ISBN: {}", e.getMessage(), e);
            }
            
            // Create the domain model
            LocalDateTime now = LocalDateTime.now();
            
            Book book = Book.reconstitute(
                BookId.generate(),
                title,
                authorName,
                isbn,
                description,
                coverUrl,
                null, // pageCount
                genres,
                null, // language
                firstPublishYear > 0 ? LocalDate.of(firstPublishYear, 1, 1) : null,
                null, // publisher
                finalOpenLibraryKey, // Add the OpenLibrary key
                now,
                now
            );
            
            return Collections.singletonList(book);
            
        } catch (RestClientException e) {
            log.error("Error querying Works API for key {}: {}", openLibraryKey, e.getMessage());
            return searchAlternative(openLibraryKey);
        } catch (Exception e) {
            log.error("Unexpected error processing Works API information: {}", e.getMessage());
            return searchAlternative(openLibraryKey);
        }
    }
    
    private List<Book> searchAlternative(String key) {
        String query = key;
        if (key.startsWith("/works/")) {
            query = key.substring(7);
        }
        
        try {
            List<Book> results = searchBooks("key:" + query, true);
            if (!results.isEmpty()) {
                return results;
            }
            
            results = searchBooks(query, true);
            if (!results.isEmpty()) {
                return results;
            }
            
            log.warn("No results found in alternative search for: {}", key);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error in alternative search: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
    
    private Book mapToBook(JsonNode doc) {
        String title = getTextValue(doc, "title");
        String author = getFirstAuthorName(doc);
        int firstPublishYear = getFirstPublishYear(doc);
        
        // Extract cover details
        String coverUrl = null;
        Long coverId = null;
        if (doc.has("cover_i")) {
            coverId = doc.get("cover_i").asLong(0);
            if (coverId > 0) {
                coverUrl = COVER_URL + coverId + "-L.jpg";
            }
        }
        
        // Extract OpenLibrary key - ensure it's properly formatted
        String key = getTextValue(doc, "key");
        if (key != null && !key.startsWith("/works/") && !key.startsWith("works/")) {
            key = "/works/" + key;
        }
        
        String isbn = null;
        if (doc.has("isbn") && doc.get("isbn").isArray() && doc.get("isbn").size() > 0) {
            isbn = doc.get("isbn").get(0).asText();
        }
        
        String description = getTextValue(doc, "first_sentence");
        
        List<String> genres = new ArrayList<>();
        if (doc.has("subject") && doc.get("subject").isArray()) {
            doc.get("subject").forEach(subject -> genres.add(subject.asText()));
        }
        
        String language = null;
        if (doc.has("language") && doc.get("language").isArray() && doc.get("language").size() > 0) {
            language = doc.get("language").get(0).asText();
        }
        
        Integer pageCount = getIntegerValue(doc, "number_of_pages_median");
        
        String publisher = null;
        if (doc.has("publisher") && doc.get("publisher").isArray() && doc.get("publisher").size() > 0) {
            publisher = doc.get("publisher").get(0).asText();
        }
        
        LocalDate publicationDate = null;
        if (firstPublishYear > 0) {
            publicationDate = LocalDate.of(firstPublishYear, 1, 1);
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        return Book.reconstitute(
            BookId.generate(),
            title,
            author,
            isbn,
            description,
            coverUrl,
            pageCount,
            genres,
            language,
            publicationDate,
            publisher,
            key, // Include the OpenLibrary key
            now,
            now
        );
    }
    
    private String getFirstAuthorName(JsonNode doc) {
        if (doc.has("author_name") && doc.get("author_name").isArray() && doc.get("author_name").size() > 0) {
            return doc.get("author_name").get(0).asText();
        }
        return "Unknown Author";
    }
    
    private int getFirstPublishYear(JsonNode doc) {
        if (doc.has("first_publish_year")) {
            return doc.get("first_publish_year").asInt(0);
        }
        return 0;
    }
    
    private String getTextValue(JsonNode doc, String field) {
        return doc.has(field) && !doc.get(field).isNull() ? doc.get(field).asText() : null;
    }
    
    private Integer getIntegerValue(JsonNode doc, String field) {
        return doc.has(field) && !doc.get(field).isNull() ? doc.get(field).asInt() : null;
    }
} 