package com.codeforge.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.codeforge.backend.entity.TestAttempt;

public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {

    @Query("select a from TestAttempt a join fetch a.codingTest where a.user.username = :username order by a.startedAt desc")
    List<TestAttempt> findByUsernameWithTest(@Param("username") String username);

    @Query("select a from TestAttempt a join fetch a.codingTest where a.id = :id and a.user.username = :username")
    Optional<TestAttempt> findByIdAndUsername(@Param("id") Long id, @Param("username") String username);

    // Prevents a second simultaneous attempt at the same test
    Optional<TestAttempt> findByUserIdAndCodingTestIdAndStatus(Long userId, Long codingTestId, String status);
}