package com.wrappedup.backend.infrastructure.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.wrappedup.backend.domain.Book;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenLibraryService {
    private static final String SEARCH_URL = "https://openlibrary.org/search.json";
    private static final String COVER_URL = "https://covers.openlibrary.org/b/id/";
    private static final String WORKS_API_URL = "https://openlibrary.org";
    private final RestTemplate restTemplate;

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
                    log.warn("Clave de OpenLibrary con formato inválido: '{}'", openLibraryKey);
                }
            }
            
            String url = WORKS_API_URL + openLibraryKey + ".json";

            JsonNode workData;
            try {
                workData = restTemplate.getForObject(url, JsonNode.class);
            } catch (Exception e) {
                log.error("Error al hacer petición a OpenLibrary: {} - {}", e.getClass().getName(), e.getMessage(), e);
                return searchAlternative(openLibraryKey);
            }
            
            if (workData == null) {
                log.warn("La respuesta de la API es nula para la clave: '{}'", openLibraryKey);
                return searchAlternative(openLibraryKey);
            }
            
            String title;
            try {
                title = workData.has("title") ? workData.get("title").asText() : "Unknown Title";
            } catch (Exception e) {
                log.error("Error al extraer el título: {}", e.getMessage(), e);
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
                log.error("Error al extraer el año de publicación: {}", e.getMessage(), e);
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
                            log.warn("Error al obtener información del autor: {}", e.getMessage());
                            authorName = "Author: " + authorKey.replace("/authors/", "");
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error al procesar la información del autor: {}", e.getMessage(), e);
            }
            
            String coverUrl = null;
            try {
                if (workData.has("covers") && workData.get("covers").isArray() && workData.get("covers").size() > 0) {
                    long coverId = workData.get("covers").get(0).asLong();
                    coverUrl = COVER_URL + coverId + "-L.jpg";
                }
            } catch (Exception e) {
                log.error("Error al extraer la portada: {}", e.getMessage(), e);
            }
            
            Book book = new Book(
                title,
                authorName,
                firstPublishYear,
                "OpenLibrary",
                coverUrl,
                openLibraryKey
            );
            
            try {
                if (workData.has("description")) {
                    if (workData.get("description").isTextual()) {
                        String description = workData.get("description").asText();
                        book.setFirstSentence(description);
                    } else if (workData.get("description").has("value")) {
                        String description = workData.get("description").get("value").asText();
                        book.setFirstSentence(description);
                    }
                }
            } catch (Exception e) {
                log.error("Error al extraer la descripción: {}", e.getMessage(), e);
            }

            return Collections.singletonList(book);
            
        } catch (RestClientException e) {
            log.error("Error al consultar la API de Works para la clave {}: {}", openLibraryKey, e.getMessage());
            return searchAlternative(openLibraryKey);
        } catch (Exception e) {
            log.error("Error inesperado al procesar información de la API de Works: {}", e.getMessage());
            return searchAlternative(openLibraryKey);
        }
    }
    

    private List<Book> searchAlternative(String key) {
        String query = key;
        if (key.startsWith("/works/")) {
            query = key.substring(7);
        }
        
        try {
            List<Book> results = searchBooks("key:" + query);
            if (!results.isEmpty()) {
                return results;
            }
            
            results = searchBooks(query);
            if (!results.isEmpty()) {
                return results;
            }
            
            log.warn("No se encontraron resultados en búsqueda alternativa para: {}", key);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error en búsqueda alternativa: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<Book> searchBooks(String query) {
        if (query.startsWith("key:")) {
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
        
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);
        if (response == null || !response.has("docs")) {
            log.warn("No se encontraron resultados para la consulta: {}", query);
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