package com.codeforge.backend.dto.response;

import java.util.List;

import com.codeforge.backend.entity.Question;
import com.codeforge.backend.entity.TestCase;

public record QuestionDetailResponse(
        Long id,
        String title,
        String difficulty,
        String topic,
        String statement,
        String inputDescription,
        String outputDescription,
        String constraints,
        int timeLimitMs,
        int memoryLimitKb,
        List<TestCaseSampleResponse> samples
) {

    // Only sample test cases are passed in, so hidden ones cannot be included by mistake
    public static QuestionDetailResponse from(Question question, List<TestCase> sampleCases) {
        return new QuestionDetailResponse(
                question.getId(),
                question.getTitle(),
                question.getDifficulty().name(),
                question.getTopic().getName(),
                question.getStatement(),
                question.getInputDescription(),
                question.getOutputDescription(),
                question.getConstraintsText(),
                question.getTimeLimitMs(),
                question.getMemoryLimitKb(),
                sampleCases.stream().map(TestCaseSampleResponse::from).toList());
    }
}