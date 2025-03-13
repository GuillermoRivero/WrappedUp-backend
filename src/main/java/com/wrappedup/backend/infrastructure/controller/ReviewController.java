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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<Review> addReview(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ReviewRequest request) {
        // Try to find the book by Open Library key
        var book = bookRepository.findByOpenLibraryKey(request.openLibraryKey())
                .orElseGet(() -> {
                    // If book doesn't exist, search it in Open Library and save it
                    var books = openLibraryService.searchBooks("key:" + request.openLibraryKey());
                    if (books.isEmpty()) {
                        throw new IllegalArgumentException("Book not found in Open Library");
                    }
                    return bookRepository.save(books.get(0));
                });
                
        var review = new Review(
            user.getId(),
            book,
            request.text(),
            request.rating(),
            request.startDate(),
            request.endDate()
        );
        
        return ResponseEntity.ok(addReviewUseCase.execute(review));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getUserReviews(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable UUID userId) {
        if (!authenticatedUser.getId().equals(userId) && 
            authenticatedUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only access your own reviews");
        }
        return ResponseEntity.ok(getUserReviewsUseCase.execute(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Review>> getMyReviews(
            @AuthenticationPrincipal User authenticatedUser) {
        return ResponseEntity.ok(getUserReviewsUseCase.execute(authenticatedUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable UUID id) {
        var review = getUserReviewsUseCase.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
                
        if (!authenticatedUser.getId().equals(review.getUserId()) && 
            authenticatedUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only access your own reviews");
        }
        
        return ResponseEntity.ok(review);
    }
} 