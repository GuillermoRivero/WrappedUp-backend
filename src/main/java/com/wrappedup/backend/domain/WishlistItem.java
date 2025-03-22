package com.wrappedup.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wishlist_items")
@Getter
@Setter
@NoArgsConstructor
public class WishlistItem {
    
    private static final Logger log = LoggerFactory.getLogger(WishlistItem.class);
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(length = 500)
    private String description;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer priority = 3; 

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public WishlistItem(User user, Book book) {
        this.user = user;
        this.book = book;
        this.priority = 3;
        this.isPublic = false;
    }

    public WishlistItem(User user, Book book, String description, Integer priority, Boolean isPublic) {
        this.user = user;
        this.book = book;
        this.description = description;
        this.priority = priority != null ? priority : 3;
        this.isPublic = isPublic != null ? isPublic : false;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
} 