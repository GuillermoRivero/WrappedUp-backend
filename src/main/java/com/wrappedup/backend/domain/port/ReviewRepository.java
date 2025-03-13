package com.wrappedup.backend.domain.port;

import com.wrappedup.backend.domain.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {
    Review save(Review review);
    List<Review> findByUserId(UUID userId);
    Optional<Review> findById(UUID id);
} 