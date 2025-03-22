package com.wrappedup.backend.infrastructure.controller;

import com.wrappedup.backend.application.AddReviewUseCase;
import com.wrappedup.backend.application.GetUserReviewsUseCase;
import com.wrappedup.backend.domain.Review;
import com.wrappedup.backend.domain.Role;
import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.Book;
import com.wrappedup.backend.domain.port.BookRepository;
import com.wrappedup.backend.infrastructure.service.OpenLibraryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin
public class ReviewController {

    private final AddReviewUseCase addReviewUseCase;
    private final GetUserReviewsUseCase getUserReviewsUseCase;
    private final BookRepository bookRepository;
    private final OpenLibraryService openLibraryService;

    public ReviewController(
            AddReviewUseCase addReviewUseCase,
            GetUserReviewsUseCase getUserReviewsUseCase,
            BookRepository bookRepository,
            OpenLibraryService openLibraryService) {
        this.addReviewUseCase = addReviewUseCase;
        this.getUserReviewsUseCase = getUserReviewsUseCase;
        this.bookRepository = bookRepository;
        this.openLibraryService = openLibraryService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> addReview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewRequest request) {
        try {            
            Book book = bookRepository.findByOpenLibraryKey(request.openLibraryKey())
                    .orElseGet(() -> {
                        try {
                            var books = openLibraryService.searchBooks("key:" + request.openLibraryKey());
                            if (books.isEmpty()) {
                                throw new IllegalArgumentException("Book not found in Open Library");
                            }
                            return bookRepository.save(books.get(0));
                        } catch (Exception e) {
                            log.error("Error fetching book from Open Library: {}", e.getMessage(), e);
                            throw new IllegalArgumentException("Error fetching book: " + e.getMessage());
                        }
                    });
                                
            var review = new Review(
                user.getId(),
                book,
                request.text(),
                request.rating(),
                request.startDate(),
                request.endDate()
            );
            
            Review savedReview = addReviewUseCase.execute(review);
            
            return ResponseEntity.ok(savedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding review: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Could not save review: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserReviews(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable UUID userId) {
        try {
            if (!authenticatedUser.getId().equals(userId) && 
                authenticatedUser.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("You can only access your own reviews");
            }
            return ResponseEntity.ok(getUserReviewsUseCase.execute(userId));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting user reviews: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Could not retrieve reviews: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyReviews(
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            return ResponseEntity.ok(getUserReviewsUseCase.execute(authenticatedUser.getId()));
        } catch (Exception e) {
            log.error("Error getting reviews for user {}: {}", authenticatedUser.getId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Could not retrieve reviews: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReview(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable UUID id) {
        try {
            var review = getUserReviewsUseCase.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Review not found"));
                    
            if (!authenticatedUser.getId().equals(review.getUserId()) && 
                authenticatedUser.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("You can only access your own reviews");
            }
            
            return ResponseEntity.ok(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting review {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Could not retrieve review: " + e.getMessage()));
        }
    }
    
    public record ErrorResponse(String message) {}
} 