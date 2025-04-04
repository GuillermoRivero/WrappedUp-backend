package com.wrappedup.backend.infrastructure.config;

import com.wrappedup.backend.application.service.CreateBookService;
import com.wrappedup.backend.application.service.DeleteBookService;
import com.wrappedup.backend.application.service.GetBookService;
import com.wrappedup.backend.application.service.UpdateBookService;
import com.wrappedup.backend.domain.port.in.CreateBookUseCase;
import com.wrappedup.backend.domain.port.in.DeleteBookUseCase;
import com.wrappedup.backend.domain.port.in.UpdateBookUseCase;
import com.wrappedup.backend.domain.port.out.OpenLibraryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateBookUseCase createBookUseCase(com.wrappedup.backend.domain.port.out.BookRepository bookRepository) {
        return new CreateBookService(bookRepository);
    }

    @Bean
    public UpdateBookUseCase updateBookUseCase(com.wrappedup.backend.domain.port.out.BookRepository bookRepository) {
        return new UpdateBookService(bookRepository);
    }

    @Bean
    public DeleteBookUseCase deleteBookUseCase(com.wrappedup.backend.domain.port.out.BookRepository bookRepository) {
        return new DeleteBookService(bookRepository);
    }

    @Bean
    public GetBookService getBookService(com.wrappedup.backend.domain.port.out.BookRepository bookRepository, 
                                         OpenLibraryPort openLibraryPort) {
        return new GetBookService(bookRepository, openLibraryPort);
    }
} 