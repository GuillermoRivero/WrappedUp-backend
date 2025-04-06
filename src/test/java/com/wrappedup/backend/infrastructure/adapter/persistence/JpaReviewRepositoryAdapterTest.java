package com.wrappedup.backend.infrastructure.adapter.persistence;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.infrastructure.adapter.persistence.entity.ReviewJpaEntity;
import com.wrappedup.backend.infrastructure.adapter.persistence.repository.ReviewJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaReviewRepositoryAdapterTest {

    @Mock
    private ReviewJpaRepository jpaRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private JpaReviewRepositoryAdapter adapter;

    private UUID reviewId;
    private UUID userId;
    private UUID bookId;
    private Review testReview;
    private ReviewJpaEntity testJpaEntity;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        userId = UUID.randomUUID();
        bookId = UUID.randomUUID();
        
        testReview = createTestReview(reviewId, userId, bookId);
        testJpaEntity = createTestJpaEntity(reviewId, userId, bookId);
        
        // Set the entityManager using ReflectionTestUtils to work around @PersistenceContext
        ReflectionTestUtils.setField(adapter, "entityManager", entityManager);
    }

    @Test
    void save_ShouldReturnSavedReview_WhenSavingNewReview() {
        // Arrange
        when(jpaRepository.existsById(reviewId)).thenReturn(false);
        when(jpaRepository.save(any(ReviewJpaEntity.class))).thenReturn(testJpaEntity);
        
        // Act
        Review result = adapter.save(testReview);
        
        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getId().getValue());
        assertEquals(userId, result.getUserId().getValue());
        assertEquals(bookId, result.getBookId().getValue());
        assertEquals(testReview.getRating(), result.getRating());
        assertEquals(testReview.getContent(), result.getContent());
        verify(jpaRepository).save(any(ReviewJpaEntity.class));
        verify(entityManager, never()).merge(any());
        verify(entityManager, never()).flush();
    }
    
    @Test
    void save_ShouldReturnSavedReview_WhenUpdatingExistingReview() {
        // Arrange
        when(jpaRepository.existsById(reviewId)).thenReturn(true);
        when(entityManager.merge(any(ReviewJpaEntity.class))).thenReturn(testJpaEntity);
        
        // Act
        Review result = adapter.save(testReview);
        
        // Assert
        assertNotNull(result);
        assertEquals(reviewId, result.getId().getValue());
        assertEquals(userId, result.getUserId().getValue());
        assertEquals(bookId, result.getBookId().getValue());
        assertEquals(testReview.getRating(), result.getRating());
        assertEquals(testReview.getContent(), result.getContent());
        verify(jpaRepository, never()).save(any(ReviewJpaEntity.class));
        verify(entityManager).merge(any(ReviewJpaEntity.class));
        verify(entityManager).flush();
    }

    @Test
    void findById_ShouldReturnReview_WhenReviewExists() {
        // Arrange
        when(jpaRepository.findById(reviewId)).thenReturn(Optional.of(testJpaEntity));
        
        // Act
        Optional<Review> result = adapter.findById(ReviewId.fromUUID(reviewId));
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(reviewId, result.get().getId().getValue());
        assertEquals(userId, result.get().getUserId().getValue());
        assertEquals(bookId, result.get().getBookId().getValue());
        verify(jpaRepository).findById(reviewId);
    }
    
    @Test
    void findById_ShouldReturnEmpty_WhenReviewDoesNotExist() {
        // Arrange
        when(jpaRepository.findById(reviewId)).thenReturn(Optional.empty());
        
        // Act
        Optional<Review> result = adapter.findById(ReviewId.fromUUID(reviewId));
        
        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository).findById(reviewId);
    }
    
    @Test
    void findByUserId_ShouldReturnReviews_WhenUserHasReviews() {
        // Arrange
        List<ReviewJpaEntity> entities = Arrays.asList(
                testJpaEntity,
                createTestJpaEntity(UUID.randomUUID(), userId, UUID.randomUUID())
        );
        
        when(jpaRepository.findByUserId(userId)).thenReturn(entities);
        
        // Act
        List<Review> result = adapter.findByUserId(UserId.fromUUID(userId));
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getUserId().getValue());
        assertEquals(userId, result.get(1).getUserId().getValue());
        verify(jpaRepository).findByUserId(userId);
    }
    
    @Test
    void findByUserId_ShouldReturnEmptyList_WhenUserHasNoReviews() {
        // Arrange
        when(jpaRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        
        // Act
        List<Review> result = adapter.findByUserId(UserId.fromUUID(userId));
        
        // Assert
        assertTrue(result.isEmpty());
        verify(jpaRepository).findByUserId(userId);
    }
    
    @Test
    void findByUserIdAndBookId_ShouldReturnReview_WhenReviewExists() {
        // Arrange
        when(jpaRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.of(testJpaEntity));
        
        // Act
        Optional<Review> result = adapter.findByUserIdAndBookId(
                UserId.fromUUID(userId),
                BookId.fromUUID(bookId)
        );
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(reviewId, result.get().getId().getValue());
        assertEquals(userId, result.get().getUserId().getValue());
        assertEquals(bookId, result.get().getBookId().getValue());
        verify(jpaRepository).findByUserIdAndBookId(userId, bookId);
    }
    
    @Test
    void findByUserIdAndBookId_ShouldReturnEmpty_WhenReviewDoesNotExist() {
        // Arrange
        when(jpaRepository.findByUserIdAndBookId(userId, bookId)).thenReturn(Optional.empty());
        
        // Act
        Optional<Review> result = adapter.findByUserIdAndBookId(
                UserId.fromUUID(userId),
                BookId.fromUUID(bookId)
        );
        
        // Assert
        assertFalse(result.isPresent());
        verify(jpaRepository).findByUserIdAndBookId(userId, bookId);
    }
    
    @Test
    void findPublicReviewsByBookId_ShouldReturnPublicReviews() {
        // Arrange
        List<ReviewJpaEntity> entities = Arrays.asList(
                testJpaEntity,
                createTestJpaEntity(UUID.randomUUID(), UUID.randomUUID(), bookId)
        );
        
        when(jpaRepository.findByBookIdAndIsPublicTrue(bookId)).thenReturn(entities);
        
        // Act
        List<Review> result = adapter.findPublicReviewsByBookId(BookId.fromUUID(bookId));
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(bookId, result.get(0).getBookId().getValue());
        assertEquals(bookId, result.get(1).getBookId().getValue());
        verify(jpaRepository).findByBookIdAndIsPublicTrue(bookId);
    }
    
    @Test
    void findPublicReviewsByBookId_ShouldReturnEmptyList_WhenNoPublicReviewsExist() {
        // Arrange
        when(jpaRepository.findByBookIdAndIsPublicTrue(bookId)).thenReturn(Collections.emptyList());
        
        // Act
        List<Review> result = adapter.findPublicReviewsByBookId(BookId.fromUUID(bookId));
        
        // Assert
        assertTrue(result.isEmpty());
        verify(jpaRepository).findByBookIdAndIsPublicTrue(bookId);
    }
    
    @Test
    void deleteById_ShouldDeleteReview_WhenReviewExists() {
        // Arrange
        when(jpaRepository.existsById(reviewId)).thenReturn(true);
        
        // Act
        adapter.deleteById(ReviewId.fromUUID(reviewId));
        
        // Assert
        verify(jpaRepository).deleteById(reviewId);
    }
    
    @Test
    void deleteById_ShouldNotThrowException_WhenReviewDoesNotExist() {
        // Arrange
        when(jpaRepository.existsById(reviewId)).thenReturn(false);
        
        // Act & Assert
        assertDoesNotThrow(() -> adapter.deleteById(ReviewId.fromUUID(reviewId)));
        verify(jpaRepository, never()).deleteById(reviewId);
    }
    
    @Test
    void deleteById_ShouldPropagateException_WhenExceptionIsThrown() {
        // Arrange
        when(jpaRepository.existsById(reviewId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(jpaRepository).deleteById(reviewId);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> adapter.deleteById(ReviewId.fromUUID(reviewId)));
    }

    private Review createTestReview(UUID reviewId, UUID userId, UUID bookId) {
        return Review.reconstitute(
                ReviewId.fromUUID(reviewId),
                UserId.fromUUID(userId),
                BookId.fromUUID(bookId),
                4,
                "Great book!",
                LocalDate.now().minusDays(10),
                LocalDate.now(),
                true,
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now()
        );
    }

    private ReviewJpaEntity createTestJpaEntity(UUID reviewId, UUID userId, UUID bookId) {
        return ReviewJpaEntity.builder()
                .id(reviewId)
                .userId(userId)
                .bookId(bookId)
                .rating(4)
                .content("Great book!")
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now())
                .isPublic(true)
                .createdAt(LocalDateTime.now().minusDays(5))
                .updatedAt(LocalDateTime.now())
                .build();
    }
} 