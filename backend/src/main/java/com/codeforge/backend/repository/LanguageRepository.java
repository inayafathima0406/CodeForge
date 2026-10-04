package com.codeforge.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeforge.backend.entity.Language;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> findByCodeAndActiveTrue(String code);
}