package com.codeforge.backend.dto.response;

import com.codeforge.backend.entity.TestCase;

public record TestCaseSampleResponse(
        String input,
        String expectedOutput,
        String explanation
) {

    public static TestCaseSampleResponse from(TestCase testCase) {
        return new TestCaseSampleResponse(
                testCase.getInputData(),
                testCase.getExpectedOutput(),
                testCase.getExplanation());
    }
}