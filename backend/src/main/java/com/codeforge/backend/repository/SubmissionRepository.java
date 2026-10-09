package com.codeforge.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeforge.backend.entity.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("select s from Submission s join fetch s.question q join fetch q.topic "
         + "where s.user.username = :username order by s.createdAt desc")
    List<Submission> findByUsernameWithQuestion(@Param("username") String username);

    @Query("select s from Submission s join fetch s.question q join fetch q.topic join fetch s.language "
         + "where s.id = :id and s.user.username = :username")
    Optional<Submission> findByIdAndUsername(@Param("id") Long id, @Param("username") String username);
    @org.springframework.data.jpa.repository.Query(
        "select distinct function('date', s.createdAt) from Submission s where s.user.username = :username order by 1 desc")
    java.util.List<java.sql.Date> findDistinctSubmissionDates(@org.springframework.data.repository.query.Param("username") String username);
}