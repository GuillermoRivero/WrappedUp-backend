package com.wrappedup.backend.infrastructure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrappedup.backend.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class OpenLibraryService {
    private static final String SEARCH_URL = "https://openlibrary.org/search.json";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<Book> searchBooks(String query) {
        String url = UriComponentsBuilder.fromHttpUrl(SEARCH_URL)
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

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response == null || !response.has("docs")) {
            return Collections.emptyList();
        }

        return StreamSupport.stream(response.get("docs").spliterator(), false)
                .map(this::mapToBook)
                .collect(Collectors.toList());
    }

    private Book mapToBook(JsonNode doc) {
        Book book = new Book(
                getTextValue(doc, "title"),
                getFirstAuthorName(doc),
                getFirstPublishYear(doc),
                "OpenLibrary",
                getCoverUrl(doc),
                getTextValue(doc, "key")
        );

        // Basic fields
        book.setSubtitle(getTextValue(doc, "subtitle"));
        book.setAlternativeTitle(getTextValue(doc, "alternative_title"));
        book.setAlternativeSubtitle(getTextValue(doc, "alternative_subtitle"));
        book.setCoverId(getLongValue(doc, "cover_i"));
        book.setEbookAccess(getTextValue(doc, "ebook_access"));
        book.setEditionCount(getIntegerValue(doc, "edition_count"));
        book.setFormat(getTextValue(doc, "format"));
        book.setByStatement(getTextValue(doc, "by_statement"));
        book.setFirstSentence(getTextValue(doc, "first_sentence"));
        book.setHasFulltext(getBooleanValue(doc, "has_fulltext"));
        book.setNumberOfPagesMedian(getIntegerValue(doc, "number_of_pages_median"));
        book.setIaCount(getIntegerValue(doc, "ia_count"));

        // Reading stats
        book.setRatingsCount(getIntegerValue(doc, "ratings_count"));
        book.setReadinglogCount(getIntegerValue(doc, "readinglog_count"));
        book.setWantToReadCount(getIntegerValue(doc, "want_to_read_count"));
        book.setCurrentlyReadingCount(getIntegerValue(doc, "currently_reading_count"));
        book.setAlreadyReadCount(getIntegerValue(doc, "already_read_count"));

        // Classifications
        book.setLccSort(getTextValue(doc, "lcc_sort"));
        book.setDdcSort(getTextValue(doc, "ddc_sort"));

        // Lists
        book.setRedirects(getStringList(doc, "redirects"));
        book.setEditionKeys(getStringList(doc, "edition_key"));
        book.setPublishDates(getStringList(doc, "publish_date"));
        book.setPublishYears(getIntegerList(doc, "publish_year"));
        book.setLccns(getStringList(doc, "lccn"));
        book.setIaIds(getStringList(doc, "ia"));
        book.setOclcs(getStringList(doc, "oclc"));
        book.setIsbns(getStringList(doc, "isbn"));
        book.setContributors(getStringList(doc, "contributor"));
        book.setPublishPlaces(getStringList(doc, "publish_place"));
        book.setPublishers(getStringList(doc, "publisher"));
        book.setAuthorKeys(getStringList(doc, "author_key"));
        book.setAuthorNames(getStringList(doc, "author_name"));
        book.setAuthorAlternativeNames(getStringList(doc, "author_alternative_name"));
        book.setSubjects(getStringList(doc, "subject"));
        book.setPersons(getStringList(doc, "person"));
        book.setPlaces(getStringList(doc, "place"));
        book.setTimes(getStringList(doc, "time"));
        book.setLanguages(getStringList(doc, "language"));
        book.setSubjectKeys(getStringList(doc, "subject_key"));
        book.setPersonKeys(getStringList(doc, "person_key"));
        book.setPlaceKeys(getStringList(doc, "place_key"));
        book.setTimeKeys(getStringList(doc, "time_key"));
        book.setLccs(getStringList(doc, "lcc"));
        book.setDdcs(getStringList(doc, "ddc"));

        return book;
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

    private String getCoverUrl(JsonNode doc) {
        if (doc.has("cover_i")) {
            return COVER_URL + doc.get("cover_i").asText() + "-L.jpg";
        }
        return null;
    }

    private String getTextValue(JsonNode doc, String field) {
        return doc.has(field) ? doc.get(field).asText(null) : null;
    }

    private Integer getIntegerValue(JsonNode doc, String field) {
        return doc.has(field) ? doc.get(field).asInt() : null;
    }

    private Long getLongValue(JsonNode doc, String field) {
        return doc.has(field) ? doc.get(field).asLong() : null;
    }

    private Boolean getBooleanValue(JsonNode doc, String field) {
        return doc.has(field) ? doc.get(field).asBoolean() : null;
    }

    private List<String> getStringList(JsonNode doc, String field) {
        if (doc.has(field) && doc.get(field).isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode item : doc.get(field)) {
                list.add(item.asText());
            }
            return list;
        }
        return Collections.emptyList();
    }

    private List<Integer> getIntegerList(JsonNode doc, String field) {
        if (doc.has(field) && doc.get(field).isArray()) {
            List<Integer> list = new ArrayList<>();
            for (JsonNode item : doc.get(field)) {
                list.add(item.asInt());
            }
            return list;
        }
        return Collections.emptyList();
    }
} 