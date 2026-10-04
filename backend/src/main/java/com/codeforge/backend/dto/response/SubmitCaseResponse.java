package com.codeforge.backend.dto.response;

// Hidden cases carry only a number and a status. Their input and expected output never leave the server.
public record SubmitCaseResponse(
        int caseNumber,
        boolean sample,
        String status,
        String input,
        String expectedOutput,
        String actualOutput,
        String errorMessage,
        Double timeSeconds,
        Integer memoryKb
) {
}