package com.wrappedup.backend.infrastructure.adapter.jpa.adapter;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.model.WishlistItem;
import com.wrappedup.backend.domain.model.WishlistItemId;
import com.wrappedup.backend.domain.port.out.WishlistItemRepository;
import com.wrappedup.backend.infrastructure.adapter.jpa.entity.WishlistItemJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.jpa.repository.WishlistItemJpaRepository;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class JpaWishlistItemRepositoryAdapter implements WishlistItemRepository {

    private final WishlistItemJpaRepository jpaRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public WishlistItem save(WishlistItem wishlistItem) {
        log.info("Saving wishlist item with ID: {}", wishlistItem.getId().getValue());
        log.info("Domain entity values - description: {}, priority: {}, isPublic: {}, updatedAt: {}", 
                wishlistItem.getDescription(), wishlistItem.getPriority(), 
                wishlistItem.isPublic(), wishlistItem.getUpdatedAt());
        
        WishlistItemJpaEntity entity = mapToJpaEntity(wishlistItem);
        log.info("Mapped to JPA entity - description: {}, priority: {}, isPublic: {}, updatedAt: {}", 
                entity.getDescription(), entity.getPriority(), 
                entity.getIsPublic(), entity.getUpdatedAt());
        
        // Check if entity exists
        boolean exists = jpaRepository.existsById(entity.getId());
        log.info("Entity exists in database: {}", exists);
        
        // Save entity
        WishlistItemJpaEntity savedEntity;
        if (exists) {
            // Merge and flush for explicit update
            savedEntity = entityManager.merge(entity);
            entityManager.flush();
            log.info("Entity merged and flushed explicitly");
        } else {
            savedEntity = jpaRepository.save(entity);
        }
        
        log.info("Saved JPA entity - description: {}, priority: {}, isPublic: {}, updatedAt: {}", 
                savedEntity.getDescription(), savedEntity.getPriority(), 
                savedEntity.getIsPublic(), savedEntity.getUpdatedAt());
        
        WishlistItem result = mapToDomainEntity(savedEntity);
        log.info("Mapped back to domain entity - description: {}, priority: {}, isPublic: {}, updatedAt: {}", 
                result.getDescription(), result.getPriority(), 
                result.isPublic(), result.getUpdatedAt());
        
        return result;
    }

    @Override
    public Optional<WishlistItem> findById(WishlistItemId id) {
        return jpaRepository.findById(id.getValue())
                .map(this::mapToDomainEntity);
    }

    @Override
    public List<WishlistItem> findAllByUserId(UserId userId) {
        return jpaRepository.findAllByUserId(userId.getValue())
                .stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WishlistItem> findByUserIdAndBookId(UserId userId, BookId bookId) {
        return jpaRepository.findByUserIdAndBookId(userId.getValue(), bookId.getValue())
                .map(this::mapToDomainEntity);
    }

    @Override
    public void deleteById(WishlistItemId id) {
        log.info("Attempting to delete wishlist item with ID: {}", id.getValue());
        try {
            if (jpaRepository.existsById(id.getValue())) {
                jpaRepository.deleteById(id.getValue());
                log.info("Successfully deleted wishlist item with ID: {}", id.getValue());
            } else {
                log.warn("Cannot delete wishlist item with ID: {} - Item does not exist in database", id.getValue());
            }
        } catch (Exception e) {
            log.error("Error deleting wishlist item with ID: {}", id.getValue(), e);
            throw e;
        }
    }

    @Override
    public boolean existsByUserIdAndBookId(UserId userId, BookId bookId) {
        return jpaRepository.existsByUserIdAndBookId(userId.getValue(), bookId.getValue());
    }

    private WishlistItemJpaEntity mapToJpaEntity(WishlistItem wishlistItem) {
        LocalDateTime now = LocalDateTime.now();
        return WishlistItemJpaEntity.builder()
                .id(wishlistItem.getId().getValue())
                .userId(wishlistItem.getUserId().getValue())
                .bookId(wishlistItem.getBookId().getValue())
                .description(wishlistItem.getDescription())
                .priority(wishlistItem.getPriority())
                .isPublic(wishlistItem.isPublic())
                .createdAt(wishlistItem.getCreatedAt() != null ? wishlistItem.getCreatedAt() : now)
                .updatedAt(wishlistItem.getUpdatedAt() != null ? wishlistItem.getUpdatedAt() : now)
                .build();
    }

    private WishlistItem mapToDomainEntity(WishlistItemJpaEntity entity) {
        return WishlistItem.reconstitute(
                WishlistItemId.fromUUID(entity.getId()),
                UserId.fromUUID(entity.getUserId()),
                BookId.fromUUID(entity.getBookId()),
                entity.getDescription(),
                entity.getPriority(),
                entity.getIsPublic(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
} 