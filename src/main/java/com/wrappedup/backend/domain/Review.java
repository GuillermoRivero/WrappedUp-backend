package com.wrappedup.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Review {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;
    
    @NotNull
    @Column(name = "user_id", nullable = false)
    @JsonProperty("user_id")
    private UUID userId;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(columnDefinition = "TEXT")
    private String text;
    
    @NotNull
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;
    
    @Column(name = "start_date")
    @JsonProperty("start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    @JsonProperty("end_date")
    private LocalDate endDate;

    public Review(UUID userId, Book book, String text, int rating, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.book = book;
        this.text = text;
        this.rating = rating;
        this.startDate = startDate;
        this.endDate = endDate;
    }
} 