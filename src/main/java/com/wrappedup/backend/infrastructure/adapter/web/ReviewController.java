package com.wrappedup.backend.infrastructure.adapter.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wrappedup.backend.domain.model.BookId;
import com.wrappedup.backend.domain.model.Book;
import com.wrappedup.backend.domain.model.Review;
import com.wrappedup.backend.domain.model.ReviewId;
import com.wrappedup.backend.domain.model.UserId;
import com.wrappedup.backend.domain.port.in.CreateReviewUseCase;
import com.wrappedup.backend.domain.port.in.DeleteReviewUseCase;
import com.wrappedup.backend.domain.port.in.GetReviewUseCase;
import com.wrappedup.backend.domain.port.in.UpdateReviewUseCase;
import com.wrappedup.backend.domain.port.out.UserIdPort;
import com.wrappedup.backend.application.service.GetBookService;
import com.wrappedup.backend.infrastructure.adapter.web.dto.ReviewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * REST controller for managing reviews.
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final CreateReviewUseCase createReviewUseCase;
    private final GetReviewUseCase getReviewUseCase;
    private final UpdateReviewUseCase updateReviewUseCase;
    private final DeleteReviewUseCase deleteReviewUseCase;
    private final UserIdPort userIdPort;
    private final GetBookService getBookService;

    /**
     * POST /api/reviews : Create a new review or update an existing one.
     */
    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateReviewRequest request) {
        
        try {
            UUID userId = userIdPort.extractUserId(token.substring(7));
            
            BookId bookId;
            Book book = null;
            
            // If openLibraryKey is provided, fetch the book from OpenLibrary and persist it
            if (request.openLibraryKey != null && !request.openLibraryKey.trim().isEmpty()) {
                // Fetch and persist book from OpenLibrary
                Optional<Book> bookOpt = getBookService.getAndPersistBookByOpenLibraryKey(request.openLibraryKey.trim());
                if (bookOpt.isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body(new ErrorResponse("Book not found with OpenLibrary key: " + request.openLibraryKey));
                }
                book = bookOpt.get();
                bookId = book.getId();
            } else if (request.bookId != null) {
                // Use provided book ID
                bookId = BookId.fromUUID(request.bookId);
                // Try to load the book for enriching the response
                book = getBookService.getBookById(bookId).orElse(null);
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Either bookId or openLibraryKey must be provided"));
            }
            
            // Use either the content field or the text field (for backwards compatibility)
            String reviewContent = request.content;
            if (reviewContent == null) {
                reviewContent = request.text;
            }
            
            ReviewId reviewId = createReviewUseCase.createReview(
                    new CreateReviewUseCase.CreateReviewCommand(
                            UserId.fromUUID(userId),
                            bookId,
                            request.rating,
                            reviewContent,
                            request.startDate,
                            request.endDate,
                            request.isPublic != null ? request.isPublic : false
                    )
            );
            
            // Retrieve the created/updated review
            Review createdReview = getReviewUseCase.getReviewById(reviewId)
                    .orElseThrow(() -> new IllegalStateException("Created review not found"));
            
            ReviewDTO dto = ReviewDTO.fromDomain(createdReview);
            
            // If we have the book object, use it directly for better performance
            if (book != null) {
                ReviewDTO.BookInfo bookInfo = ReviewDTO.BookInfo.builder()
                    .id(book.getId().getValue())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .coverUrl(book.getCoverImageUrl())
                    .openLibraryKey(book.getOpenLibraryKey())
                    .description(book.getDescription())
                    .numberOfPages(book.getPageCount())
                    .isbn(book.getIsbn())
                    .build();
                    
                // Set releaseYear if publication date is available
                if (book.getPublicationDate() != null) {
                    bookInfo.setReleaseYear(book.getPublicationDate().getYear());
                }
                
                dto.setBook(bookInfo);
            } else {
                enrichReviewDTOWithBookInfo(dto, createdReview.getBookId());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dto);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Could not create review: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reviews : Get the current user's reviews.
     */
    @GetMapping
    public ResponseEntity<?> getMyReviews(@RequestHeader("Authorization") String token) {
        try {
            UUID userId = userIdPort.extractUserId(token.substring(7));
            
            List<Review> reviews = getReviewUseCase.getReviewsByUserId(UserId.fromUUID(userId));
            
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(review -> {
                        ReviewDTO dto = ReviewDTO.fromDomain(review);
                        enrichReviewDTOWithBookInfo(dto, review.getBookId());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(reviewDTOs);
            
        } catch (Exception e) {
            log.error("Error getting user reviews", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Could not retrieve reviews: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reviews/me : Get the current user's reviews (alternative endpoint).
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserReviews(@RequestHeader("Authorization") String token) {
        // Delegate to the standard endpoint
        return getMyReviews(token);
    }

    /**
     * GET /api/reviews/{id} : Get a review by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id) {
        
        try {
            UUID userId = userIdPort.extractUserId(token.substring(7));
            
            Review review = getReviewUseCase.getReviewById(ReviewId.fromUUID(id))
                    .orElseThrow(() -> new IllegalArgumentException("Review not found"));
            
            // Check if the user is the owner of the review
            if (!review.getUserId().getValue().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("You can only access your own reviews"));
            }
            
            ReviewDTO dto = ReviewDTO.fromDomain(review);
            enrichReviewDTOWithBookInfo(dto, review.getBookId());
            
            return ResponseEntity.ok(dto);
            
        } catch (IllegalArgumentException e) {
            log.warn("Review not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Could not retrieve review: " + e.getMessage()));
        }
    }

    /**
     * GET /api/reviews/book/{bookId} : Get all public reviews for a book.
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<?> getPublicReviewsByBookId(@PathVariable UUID bookId) {
        try {
            List<Review> reviews = getReviewUseCase.getPublicReviewsByBookId(BookId.fromUUID(bookId));
            
            List<ReviewDTO> reviewDTOs = reviews.stream()
                    .map(review -> {
                        ReviewDTO dto = ReviewDTO.fromDomain(review);
                        enrichReviewDTOWithBookInfo(dto, review.getBookId());
                        return dto;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(reviewDTOs);
            
        } catch (Exception e) {
            log.error("Error getting public reviews for book", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Could not retrieve reviews: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/reviews/{id} : Update a review.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReviewRequest request) {
        
        try {
            UUID userId = userIdPort.extractUserId(token.substring(7));
            
            // Check if the user is the owner of the review
            Review existingReview = getReviewUseCase.getReviewById(ReviewId.fromUUID(id))
                    .orElseThrow(() -> new IllegalArgumentException("Review not found"));
            
            if (!existingReview.getUserId().getValue().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("You can only update your own reviews"));
            }
            
            Review updatedReview = updateReviewUseCase.updateReview(
                    new UpdateReviewUseCase.UpdateReviewCommand(
                            ReviewId.fromUUID(id),
                            request.rating,
                            request.content,
                            request.startDate,
                            request.endDate,
                            request.isPublic
                    )
            );
            
            return ResponseEntity.ok(ReviewDTO.fromDomain(updatedReview));
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request or review not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Could not update review: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/reviews/{id} : Delete a review.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id) {
        
        try {
            UUID userId = userIdPort.extractUserId(token.substring(7));
            
            // Check if the user is the owner of the review
            Review existingReview = getReviewUseCase.getReviewById(ReviewId.fromUUID(id))
                    .orElse(null);
            
            if (existingReview == null) {
                // Review already deleted or not found
                return ResponseEntity.noContent().build();
            }
            
            if (!existingReview.getUserId().getValue().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("You can only delete your own reviews"));
            }
            
            deleteReviewUseCase.deleteReview(ReviewId.fromUUID(id));
            
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Error deleting review", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Could not delete review: " + e.getMessage()));
        }
    }

    // Request and Response DTOs
    
    public record CreateReviewRequest(
            UUID bookId,
            
            @JsonProperty("open_library_key")
            String openLibraryKey,
            
            String content,
            
            String text,
            
            @NotNull
            @Min(1)
            @Max(5)
            int rating,
            
            @JsonProperty("start_date")
            LocalDate startDate,
            
            @JsonProperty("end_date")
            LocalDate endDate,
            
            @JsonProperty("is_public")
            Boolean isPublic
    ) {}
    
    public record UpdateReviewRequest(
            @Min(1)
            @Max(5)
            Integer rating,
            
            String content,
            
            LocalDate startDate,
            
            LocalDate endDate,
            
            Boolean isPublic
    ) {}
    
    public record ErrorResponse(String message) {}

    /**
     * Helper method to enrich a ReviewDTO with book information
     */
    private void enrichReviewDTOWithBookInfo(ReviewDTO dto, BookId bookId) {
        getBookService.getBookById(bookId).ifPresent(book -> {
            ReviewDTO.BookInfo bookInfo = ReviewDTO.BookInfo.builder()
                .id(book.getId().getValue())
                .title(book.getTitle())
                .author(book.getAuthor())
                .coverUrl(book.getCoverImageUrl())
                .openLibraryKey(book.getOpenLibraryKey())
                .description(book.getDescription())
                .numberOfPages(book.getPageCount())
                .isbn(book.getIsbn())
                .build();
                
            // Set releaseYear if publication date is available
            if (book.getPublicationDate() != null) {
                bookInfo.setReleaseYear(book.getPublicationDate().getYear());
            }
            
            dto.setBook(bookInfo);
        });
    }
} 