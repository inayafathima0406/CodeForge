package com.codeforge.backend.dto.response;

public record CodingTestSummaryResponse(
        Long id,
        String title,
        int durationMinutes,
        int questionCount
) {
}