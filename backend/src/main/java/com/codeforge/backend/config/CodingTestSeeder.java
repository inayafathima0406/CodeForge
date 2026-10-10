package com.codeforge.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codeforge.backend.entity.CodingTest;
import com.codeforge.backend.entity.CodingTestQuestion;
import com.codeforge.backend.repository.CodingTestQuestionRepository;
import com.codeforge.backend.repository.CodingTestRepository;
import com.codeforge.backend.repository.QuestionRepository;

@Configuration
public class CodingTestSeeder {

    @Bean
    CommandLineRunner seedCodingTest(CodingTestRepository codingTestRepository,
                                     CodingTestQuestionRepository codingTestQuestionRepository,
                                     QuestionRepository questionRepository) {
        return args -> {
            String title = "Placement Practice Test 1";
            if (codingTestRepository.findByActiveTrue().stream().noneMatch(t -> t.getTitle().equals(title))) {
                CodingTest test = codingTestRepository.save(new CodingTest(title, 45));

                String[] titles = { "Find Maximum Element", "Two Sum", "Binary Search" };
                int order = 1;
                for (String qTitle : titles) {
                    var q = questionRepository.findByTitle(qTitle).orElse(null);
                    if (q != null) {
                        codingTestQuestionRepository.save(new CodingTestQuestion(test, q, 10, order++));
                    }
                }
            }
        };
    }
}