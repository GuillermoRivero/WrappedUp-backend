package com.wrappedup.backend.infrastructure.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReviewRequest(
    @NotNull
    @JsonProperty("open_library_key")
    String openLibraryKey,
    
    String text,
    
    @NotNull
    @Min(1)
    @Max(5)
    int rating,
    
    @JsonProperty("start_date")
    LocalDate startDate,
    
    @JsonProperty("end_date")
    LocalDate endDate
) {} 