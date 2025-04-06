package com.wrappedup.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
    "com.wrappedup.backend.infrastructure.adapter.persistence.repository"
})
@EntityScan(basePackages = {
    "com.wrappedup.backend.infrastructure.adapter.persistence.entity"
})
public class WrappedUpBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WrappedUpBackendApplication.class, args);
	}

}
