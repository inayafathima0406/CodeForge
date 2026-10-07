package com.codeforge.backend.dto.response;

public record MentorResponse(
        String errorType,
        String explanation,
        String hint,
        String conceptsToRevise,
        String complexityFeedback,
        String suggestedSolution
) {
}