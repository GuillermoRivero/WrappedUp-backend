package com.wrappedup.backend.infrastructure.adapter.web.dto;

import com.wrappedup.backend.domain.model.WishlistItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemDTO {
    private UUID id;
    private UUID userId;
    private UUID bookId;
    private String description;
    private Integer priority;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookDTO book; // Include book details when needed

    /**
     * Creates a DTO from a domain entity without book details.
     */
    public static WishlistItemDTO fromDomain(WishlistItem wishlistItem) {
        return WishlistItemDTO.builder()
                .id(wishlistItem.getId().getValue())
                .userId(wishlistItem.getUserId().getValue())
                .bookId(wishlistItem.getBookId().getValue())
                .description(wishlistItem.getDescription())
                .priority(wishlistItem.getPriority())
                .isPublic(wishlistItem.isPublic())
                .createdAt(wishlistItem.getCreatedAt())
                .updatedAt(wishlistItem.getUpdatedAt())
                .build();
    }

    /**
     * Creates a DTO from a domain entity with book details.
     */
    public static WishlistItemDTO fromDomainWithBook(WishlistItem wishlistItem, BookDTO book) {
        WishlistItemDTO dto = fromDomain(wishlistItem);
        dto.setBook(book);
        return dto;
    }
} 