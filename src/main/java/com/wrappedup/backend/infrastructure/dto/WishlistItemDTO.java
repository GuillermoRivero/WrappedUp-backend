package com.wrappedup.backend.infrastructure.dto;

import com.wrappedup.backend.domain.WishlistItem;
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
    private String username; 
    private BookDTO book;
    private String description;
    private Integer priority;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WishlistItemDTO fromEntity(WishlistItem item) {
        if (item == null) {
            return null;
        }
        
        return WishlistItemDTO.builder()
                .id(item.getId())
                .userId(item.getUser() != null ? item.getUser().getId() : null)
                .username(item.getUser() != null ? item.getUser().getUsername() : null)
                .book(BookDTO.fromEntity(item.getBook()))
                .description(item.getDescription())
                .priority(item.getPriority())
                .isPublic(item.isPublic())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
} 