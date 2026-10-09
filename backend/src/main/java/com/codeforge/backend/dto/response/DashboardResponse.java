package com.codeforge.backend.dto.response;

import java.util.List;
import java.util.Map;

public record DashboardResponse(
        int totalAttempted,
        int totalSolved,
        int accuracyPercent,
        int currentStreak,
        List<TopicProgressResponse> topicProgress,
        Map<String, TopicProgressResponse> difficultyProgress,
        List<SubmissionSummaryResponse> recentSubmissions
) {
}