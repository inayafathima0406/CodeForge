package com.codeforge.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeforge.backend.entity.CodingTest;

public interface CodingTestRepository extends JpaRepository<CodingTest, Long> {

    List<CodingTest> findByActiveTrue();

    Optional<CodingTest> findByIdAndActiveTrue(Long id);
}