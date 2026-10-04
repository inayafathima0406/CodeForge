package com.codeforge.backend.dto.response;

import java.time.LocalDateTime;

public record SubmissionSummaryResponse(
        Long id,
        Long questionId,
        String questionTitle,
        String topic,
        String status,
        int passed,
        int total,
        Integer execTimeMs,
        Integer memoryKb,
        LocalDateTime submittedAt
) {
}