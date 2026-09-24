package com.codeforge.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeforge.backend.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findByTitle(String title);

    // join fetch loads the topic in the same query, so there is no lazy proxy to trip over
    @Query("select q from Question q join fetch q.topic where q.published = true order by q.id")
    List<Question> findAllPublishedWithTopic();

    @Query("select q from Question q join fetch q.topic where q.id = :id and q.published = true")
    Optional<Question> findPublishedWithTopicById(@Param("id") Long id);
}