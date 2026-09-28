package com.codeforge.backend.dto.response;

public record RunCaseResponse(
        int caseNumber,
        String status,
        String input,
        String expectedOutput,
        String actualOutput,
        String errorMessage,
        Double timeSeconds,
        Integer memoryKb
) {
}