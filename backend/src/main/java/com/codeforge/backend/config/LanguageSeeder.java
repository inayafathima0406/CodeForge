package com.codeforge.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codeforge.backend.entity.Language;
import com.codeforge.backend.repository.LanguageRepository;

@Configuration
public class LanguageSeeder {

    @Bean
    CommandLineRunner seedLanguages(LanguageRepository repository) {
        return args -> {
            if (repository.findByCodeAndActiveTrue("java").isEmpty()) {
                repository.save(new Language("java", "Java", 62));
            }
        };
    }
}