package com.codeforge.backend.dto.response;

import java.time.LocalDateTime;

public record TestResultResponse(
        Long attemptId,
        String testTitle,
        String status,
        int score,
        int totalScore,
        int questionsSolved,
        int totalQuestions,
        LocalDateTime startedAt,
        LocalDateTime submittedAt
) {
}