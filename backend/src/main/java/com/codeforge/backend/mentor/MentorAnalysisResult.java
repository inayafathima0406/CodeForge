package com.codeforge.backend.mentor;

public record MentorAnalysisResult(
        String errorType,
        String explanation,
        String hint,
        String conceptsToRevise,
        String complexityFeedback,
        String suggestedSolution
) {
}