package com.codeforge.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeforge.backend.entity.MentorFeedback;

public interface MentorFeedbackRepository extends JpaRepository<MentorFeedback, Long> {

    Optional<MentorFeedback> findBySubmissionId(Long submissionId);
}