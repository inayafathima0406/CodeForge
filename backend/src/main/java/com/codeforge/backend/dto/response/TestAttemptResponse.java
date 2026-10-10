package com.codeforge.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record TestAttemptResponse(
        Long attemptId,
        Long testId,
        String testTitle,
        LocalDateTime startedAt,
        int durationMinutes,
        String status,
        List<TestQuestionResponse> questions
) {
}