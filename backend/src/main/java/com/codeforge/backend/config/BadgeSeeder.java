package com.codeforge.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codeforge.backend.entity.Badge;
import com.codeforge.backend.repository.BadgeRepository;

@Configuration
public class BadgeSeeder {

    @Bean
    CommandLineRunner seedBadges(BadgeRepository repository) {
        return args -> {
            addBadge(repository, "FIRST_SOLVE", "First Problem Solved", "Solved your first problem", "\uD83C\uDFC6");
            addBadge(repository, "BEGINNER", "DSA Beginner", "Made your first submission", "\uD83E\uDDE0");
            addBadge(repository, "STREAK_7", "7-Day Streak", "Submitted on 7 consecutive days", "\uD83D\uDD25");
            addBadge(repository, "SOLVED_25", "25 Problems Solved", "Solved 25 distinct problems", "\uD83D\uDCBB");
            addBadge(repository, "SOLVED_50", "50 Problems Solved", "Solved 50 distinct problems", "\uD83D\uDE80");
        };
    }

    private void addBadge(BadgeRepository repository, String code, String name, String desc, String icon) {
        if (repository.findByCode(code).isEmpty()) {
            repository.save(new Badge(code, name, desc, icon));
        }
    }
}