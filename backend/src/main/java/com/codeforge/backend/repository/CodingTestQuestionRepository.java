package com.codeforge.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeforge.backend.entity.CodingTestQuestion;

public interface CodingTestQuestionRepository extends JpaRepository<CodingTestQuestion, Long> {

    @Query("select ctq from CodingTestQuestion ctq join fetch ctq.question q join fetch q.topic "
         + "where ctq.codingTest.id = :testId order by ctq.displayOrder asc")
    List<CodingTestQuestion> findByCodingTestIdOrderByDisplayOrderAsc(@Param("testId") Long testId);
}