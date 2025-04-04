package com.wrappedup.backend.infrastructure.adapter.jpa.adapter;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.out.ReviewRepository;
import com.wrappedup.backend.infrastructure.adapter.jpa.entity.ReviewJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.jpa.repository.ReviewJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA adapter implementation of the ReviewRepository port.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JpaReviewRepositoryAdapter implements ReviewRepository {

    private final ReviewJpaRepository jpaRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Review save(Review review) {
        log.info("Saving review with ID: {}", review.getId().getValue());
        
        ReviewJpaEntity entity = mapToJpaEntity(review);
        
        // Check if entity exists
        boolean exists = jpaRepository.existsById(entity.getId());
        log.debug("Review exists in database: {}", exists);
        
        // Save entity
        ReviewJpaEntity savedEntity;
        if (exists) {
            // Merge and flush for explicit update
            savedEntity = entityManager.merge(entity);
            entityManager.flush();
            log.debug("Review merged and flushed explicitly");
        } else {
            savedEntity = jpaRepository.save(entity);
        }
        
        return mapToDomainEntity(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findById(ReviewId id) {
        log.debug("Finding review by ID: {}", id.getValue());
        return jpaRepository.findById(id.getValue())
                .map(this::mapToDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findByUserId(UserId userId) {
        log.debug("Finding reviews by user ID: {}", userId.getValue());
        return jpaRepository.findByUserId(userId.getValue())
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> findByUserIdAndBookId(UserId userId, BookId bookId) {
        log.debug("Finding review by user ID: {} and book ID: {}", userId.getValue(), bookId.getValue());
        return jpaRepository.findByUserIdAndBookId(userId.getValue(), bookId.getValue())
                .map(this::mapToDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> findPublicReviewsByBookId(BookId bookId) {
        log.debug("Finding public reviews by book ID: {}", bookId.getValue());
        return jpaRepository.findByBookIdAndIsPublicTrue(bookId.getValue())
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(ReviewId id) {
        log.info("Deleting review with ID: {}", id.getValue());
        try {
            if (jpaRepository.existsById(id.getValue())) {
                jpaRepository.deleteById(id.getValue());
                log.info("Successfully deleted review with ID: {}", id.getValue());
            } else {
                log.warn("Cannot delete review with ID: {} - Review does not exist", id.getValue());
            }
        } catch (Exception e) {
            log.error("Error deleting review with ID: {}", id.getValue(), e);
            throw e;
        }
    }

    private ReviewJpaEntity mapToJpaEntity(Review review) {
        LocalDateTime now = LocalDateTime.now();
        return ReviewJpaEntity.builder()
                .id(review.getId().getValue())
                .userId(review.getUserId().getValue())
                .bookId(review.getBookId().getValue())
                .rating(review.getRating())
                .content(review.getContent())
                .startDate(review.getStartDate())
                .endDate(review.getEndDate())
                .isPublic(review.isPublic())
                .createdAt(review.getCreatedAt() != null ? review.getCreatedAt() : now)
                .updatedAt(review.getUpdatedAt() != null ? review.getUpdatedAt() : now)
                .build();
    }

    private Review mapToDomainEntity(ReviewJpaEntity entity) {
        return Review.reconstitute(
                ReviewId.fromUUID(entity.getId()),
                UserId.fromUUID(entity.getUserId()),
                BookId.fromUUID(entity.getBookId()),
                entity.getRating(),
                entity.getContent(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isPublic(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
} 