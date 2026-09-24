package com.codeforge.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeforge.backend.entity.TestCase;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    long countByQuestionId(Long questionId);

    // Everything, for judging a Submit (never sent to the browser)
    List<TestCase> findByQuestionIdOrderByDisplayOrderAsc(Long questionId);

    // Sample cases only, safe to show to students and used by Run
    List<TestCase> findByQuestionIdAndSampleTrueOrderByDisplayOrderAsc(Long questionId);
}
