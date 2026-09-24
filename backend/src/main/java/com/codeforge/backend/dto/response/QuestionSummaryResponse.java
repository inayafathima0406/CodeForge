package com.codeforge.backend.dto.response;

import com.codeforge.backend.entity.Question;

public record QuestionSummaryResponse(
        Long id,
        String title,
        String difficulty,
        String topic
) {

    public static QuestionSummaryResponse from(Question question) {
        return new QuestionSummaryResponse(
                question.getId(),
                question.getTitle(),
                question.getDifficulty().name(),
                question.getTopic().getName());
    }
}