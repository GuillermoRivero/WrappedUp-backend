package com.wrappedup.backend.infrastructure.service;

import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookPersistenceService {

    private final BookRepository bookRepository;
    private final DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    private static final ConcurrentHashMap<UUID, Object> BOOK_LOCKS = new ConcurrentHashMap<>();

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Book saveBookWithConcurrencyHandling(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        UUID bookId = book.getId();
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        Book detachedBook = createDetachedCopy(book);
        
        Optional<Book> existingBook = bookRepository.findById(bookId);
        if (existingBook.isPresent()) {
            return existingBook.get();
        }

        if (detachedBook.getOpenLibraryKey() != null && !detachedBook.getOpenLibraryKey().isEmpty()) {
            existingBook = bookRepository.findByOpenLibraryKey(detachedBook.getOpenLibraryKey());
            if (existingBook.isPresent()) {
                return existingBook.get();
            }
        }

        Object lock = BOOK_LOCKS.computeIfAbsent(bookId, k -> new Object());

        synchronized (lock) {
            try {
                if (entityManager != null) {
                    entityManager.clear();
                }

                existingBook = bookRepository.findById(bookId);
                if (existingBook.isPresent()) {
                    return existingBook.get();
                }

                if (detachedBook.getOpenLibraryKey() != null && !detachedBook.getOpenLibraryKey().isEmpty()) {
                    existingBook = bookRepository.findByOpenLibraryKey(detachedBook.getOpenLibraryKey());
                    if (existingBook.isPresent()) {
                        return existingBook.get();
                    }
                }

                return tryVariousSaveStrategies(detachedBook);

            } finally {
                BOOK_LOCKS.remove(bookId);
                log.info("Liberado bloqueo para libro con ID: {}", bookId);
            }
        }
    }
    

    private Book createDetachedCopy(Book originalBook) {
        Book detachedBook = new Book();
        detachedBook.setId(originalBook.getId());
        detachedBook.setTitle(originalBook.getTitle());
        detachedBook.setAuthor(originalBook.getAuthor());
        detachedBook.setOpenLibraryKey(originalBook.getOpenLibraryKey());
        detachedBook.setPlatform(originalBook.getPlatform());
        detachedBook.setFirstPublishYear(originalBook.getFirstPublishYear());
        detachedBook.setCoverUrl(originalBook.getCoverUrl());
        detachedBook.setFirstSentence(originalBook.getFirstSentence());
        detachedBook.setSubtitle(originalBook.getSubtitle());
        detachedBook.setAlternativeTitle(originalBook.getAlternativeTitle());
        detachedBook.setAlternativeSubtitle(originalBook.getAlternativeSubtitle());
        
        return detachedBook;
    }

    private Book tryVariousSaveStrategies(Book book) {
        try {
            saveBookWithJdbc(book);

            Optional<Book> savedBook = bookRepository.findById(book.getId());
            if (savedBook.isPresent()) {
                return savedBook.get();
            }
            return book; 
        } catch (Exception e) {
            log.warn("Error guardando libro con JDBC: {} - {}", e.getClass().getName(), e.getMessage());

            Optional<Book> existingBook = bookRepository.findById(book.getId());
            if (existingBook.isPresent()) {
                return existingBook.get();
            }

            try {
                if (entityManager != null) {
                    entityManager.clear(); 
                }
                
                Book freshBook = createDetachedCopy(book);
                Book savedBook = bookRepository.save(freshBook);
                return savedBook;
            } catch (Exception e2) {
                log.warn("Error guardando libro con repositorio: {} - {}", e2.getClass().getName(), e2.getMessage());

                existingBook = bookRepository.findById(book.getId());
                if (existingBook.isPresent()) {
                    return existingBook.get();
                }

                try {
                    if (entityManager != null) {
                        entityManager.clear();
                        Book freshBook = createDetachedCopy(book);
                        entityManager.persist(freshBook);
                        entityManager.flush();
                        log.info("Libro guardado exitosamente con EntityManager directo");
                        return freshBook;
                    }
                } catch (Exception e3) {
                    log.warn("Error guardando libro con EntityManager: {} - {}", e3.getClass().getName(), e3.getMessage());
                    
                    existingBook = bookRepository.findById(book.getId());
                    if (existingBook.isPresent()) {
                        return existingBook.get();
                    }
                }

                log.error("Todos los intentos de guardar el libro fallaron");
                throw new RuntimeException("No se pudo guardar el libro usando ninguna estrategia: " + e.getMessage(), e);
            }
        }
    }


    private void saveBookWithJdbc(Book book) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String checkSql = "SELECT COUNT(*) FROM books WHERE id = ? OR (open_library_key = ? AND open_library_key IS NOT NULL)";
                boolean exists = false;
                
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setObject(1, book.getId());
                    checkStmt.setString(2, book.getOpenLibraryKey());
                    var rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        exists = true;
                    }
                }
                
                if (exists) {
                    return;
                }
                
                String sql = "INSERT INTO books (id, title, author, open_library_key, platform, first_publish_year, cover_url, version) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setObject(1, book.getId());
                    stmt.setString(2, book.getTitle() != null ? book.getTitle() : "Unknown Title");
                    stmt.setString(3, book.getAuthor() != null ? book.getAuthor() : "Unknown Author");
                    stmt.setString(4, book.getOpenLibraryKey());
                    stmt.setString(5, book.getPlatform());
                    if (book.getFirstPublishYear() != null) {
                        stmt.setInt(6, book.getFirstPublishYear());
                    } else {
                        stmt.setNull(6, java.sql.Types.INTEGER);
                    }
                    stmt.setString(7, book.getCoverUrl());
                    
                    stmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                log.error("Error en la transacción JDBC: {}", e.getMessage());
                throw e;
            }
        }
    }
} 