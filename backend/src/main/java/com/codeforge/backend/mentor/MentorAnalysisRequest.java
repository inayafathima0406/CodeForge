package com.codeforge.backend.mentor;

public record MentorAnalysisRequest(
        String questionTitle,
        String questionStatement,
        String sourceCode,
        String submissionStatus,
        int caseNumber,
        boolean sample,
        String input,
        String expectedOutput,
        String actualOutput,
        String errorMessage,
        boolean wantsSolution
) {
}