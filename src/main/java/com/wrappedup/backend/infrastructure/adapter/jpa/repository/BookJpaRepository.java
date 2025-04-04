package com.wrappedup.backend.infrastructure.adapter.jpa.repository;

import com.wrappedup.backend.infrastructure.adapter.jpa.entity.BookJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for BookJpaEntity.
 */
@Repository
public interface BookJpaRepository extends JpaRepository<BookJpaEntity, UUID> {
    
    List<BookJpaEntity> findByTitleContainingIgnoreCase(String title);
    
    List<BookJpaEntity> findByAuthorContainingIgnoreCase(String author);
    
    @Query("SELECT b FROM BookJpaEntity b JOIN b.isbns i WHERE i = :isbn")
    Optional<BookJpaEntity> findByIsbn(@Param("isbn") String isbn);
    
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM BookJpaEntity b JOIN b.isbns i WHERE i = :isbn")
    boolean existsByIsbn(@Param("isbn") String isbn);
    
    @Query("SELECT b FROM BookJpaEntity b JOIN b.genres g WHERE g = :genre")
    List<BookJpaEntity> findByGenre(@Param("genre") String genre);
    
    Optional<BookJpaEntity> findByOpenLibraryKey(String openLibraryKey);
    
    boolean existsByOpenLibraryKey(String openLibraryKey);
    
    Page<BookJpaEntity> findAll(Pageable pageable);
} 