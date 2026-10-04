package com.codeforge.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record SubmissionDetailResponse(
        Long id,
        Long questionId,
        String questionTitle,
        String language,
        String status,
        int passed,
        int total,
        Integer execTimeMs,
        Integer memoryKb,
        LocalDateTime submittedAt,
        String sourceCode,
        List<SubmitCaseResponse> cases
) {
}