package com.wrappedup.backend.application.service;

import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.GetReviewUseCase;
import com.wrappedup.backend.domain.port.out.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation of the GetReviewUseCase.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetReviewService implements GetReviewUseCase {

    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(ReviewId id) {
        log.debug("Getting review by ID: {}", id);
        return reviewRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviewsByUserId(UserId userId) {
        log.debug("Getting reviews by user ID: {}", userId);
        if (userId == null) {
            log.warn("Attempted to get reviews with null user ID");
            return Collections.emptyList();
        }
        return reviewRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> getReviewByUserIdAndBookId(UserId userId, BookId bookId) {
        log.debug("Getting review by user ID: {} and book ID: {}", userId, bookId);
        if (userId == null || bookId == null) {
            log.warn("Attempted to get review with null user ID or book ID");
            return Optional.empty();
        }
        return reviewRepository.findByUserIdAndBookId(userId, bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Review> getPublicReviewsByBookId(BookId bookId) {
        log.debug("Getting public reviews by book ID: {}", bookId);
        if (bookId == null) {
            log.warn("Attempted to get public reviews with null book ID");
            return Collections.emptyList();
        }
        return reviewRepository.findPublicReviewsByBookId(bookId);
    }
} 