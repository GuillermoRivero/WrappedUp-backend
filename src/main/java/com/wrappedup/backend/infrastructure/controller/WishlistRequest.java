package com.wrappedup.backend.infrastructure.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistRequest {
    
    private UUID bookId;
    
    private String openLibraryKey;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @Min(value = 1, message = "Priority must be between 1 and 5")
    @Max(value = 5, message = "Priority must be between 1 and 5")
    private Integer priority;
    
    private Boolean isPublic = false;
    
    public Boolean getIsPublic() {
        return isPublic;
    }
    
    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic != null ? isPublic : false;
    }
    
    @NotNull(message = "Either bookId or openLibraryKey must be provided")
    public Object getBookIdentifier() {
        return bookId != null ? bookId : openLibraryKey;
    }
} 