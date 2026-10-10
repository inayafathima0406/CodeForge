package com.codeforge.backend.dto.response;

public record TestQuestionResponse(
        Long questionId,
        String title,
        String difficulty,
        int points
) {
}