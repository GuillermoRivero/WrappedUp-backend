package com.wrappedup.backend.infrastructure.config;

import com.wrappedup.backend.application.AddBookUseCase;
import com.wrappedup.backend.application.AddReviewUseCase;
import com.wrappedup.backend.application.GetBookInfoUseCase;
import com.wrappedup.backend.application.GetUserReviewsUseCase;
import com.wrappedup.backend.domain.port.BookRepository;
import com.wrappedup.backend.domain.port.ReviewRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public AddReviewUseCase addReviewUseCase(ReviewRepository reviewRepository) {
        return new AddReviewUseCase(reviewRepository);
    }

    @Bean
    public GetUserReviewsUseCase getUserReviewsUseCase(ReviewRepository reviewRepository) {
        return new GetUserReviewsUseCase(reviewRepository);
    }

    @Bean
    public AddBookUseCase addBookUseCase(BookRepository bookRepository) {
        return new AddBookUseCase(bookRepository);
    }

    @Bean
    public GetBookInfoUseCase getBookInfoUseCase(BookRepository bookRepository) {
        return new GetBookInfoUseCase(bookRepository);
    }
} 