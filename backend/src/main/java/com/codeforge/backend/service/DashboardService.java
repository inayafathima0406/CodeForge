package com.codeforge.backend.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.response.DashboardResponse;
import com.codeforge.backend.dto.response.SubmissionSummaryResponse;
import com.codeforge.backend.dto.response.TopicProgressResponse;
import com.codeforge.backend.entity.Difficulty;
import com.codeforge.backend.entity.Question;
import com.codeforge.backend.entity.Submission;
import com.codeforge.backend.repository.QuestionRepository;
import com.codeforge.backend.repository.SubmissionRepository;

@Service
public class DashboardService {

    private final SubmissionRepository submissionRepository;
    private final QuestionRepository questionRepository;

    public DashboardService(SubmissionRepository submissionRepository,
                            QuestionRepository questionRepository) {
        this.submissionRepository = submissionRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(String username) {
        List<Submission> submissions = submissionRepository.findByUsernameWithQuestion(username);
        List<Question> allQuestions = questionRepository.findAllPublishedWithTopic();

        var attemptedQuestionIds = submissions.stream()
                .map(s -> s.getQuestion().getId()).collect(Collectors.toSet());
        var solvedQuestionIds = submissions.stream()
                .filter(s -> s.getStatus().equals("ACCEPTED"))
                .map(s -> s.getQuestion().getId()).collect(Collectors.toSet());

        int totalSubmissions = submissions.size();
        int acceptedSubmissions = (int) submissions.stream().filter(s -> s.getStatus().equals("ACCEPTED")).count();
        int accuracy = totalSubmissions == 0 ? 0 : Math.round(100f * acceptedSubmissions / totalSubmissions);

        List<TopicProgressResponse> topicProgress = buildTopicProgress(allQuestions, solvedQuestionIds);
        Map<String, TopicProgressResponse> difficultyProgress = buildDifficultyProgress(allQuestions, solvedQuestionIds);

        int streak = computeStreak(submissionRepository.findDistinctSubmissionDates(username));

        List<SubmissionSummaryResponse> recent = submissions.stream()
                .limit(5)
                .map(s -> new SubmissionSummaryResponse(
                        s.getId(), s.getQuestion().getId(), s.getQuestion().getTitle(),
                        s.getQuestion().getTopic().getName(), s.getStatus(),
                        s.getPassedCount(), s.getTotalCount(), s.getExecTimeMs(), s.getMemoryKb(),
                        s.getCreatedAt()))
                .toList();

        return new DashboardResponse(
                attemptedQuestionIds.size(), solvedQuestionIds.size(), accuracy, streak,
                topicProgress, difficultyProgress, recent);
    }

    private List<TopicProgressResponse> buildTopicProgress(List<Question> all, java.util.Set<Long> solvedIds) {
        Map<String, List<Question>> byTopic = all.stream()
                .collect(Collectors.groupingBy(q -> q.getTopic().getName(), LinkedHashMap::new, Collectors.toList()));

        List<TopicProgressResponse> result = new ArrayList<>();
        byTopic.forEach((topic, questions) -> {
            int total = questions.size();
            int solved = (int) questions.stream().filter(q -> solvedIds.contains(q.getId())).count();
            int percent = total == 0 ? 0 : Math.round(100f * solved / total);
            result.add(new TopicProgressResponse(topic, solved, total, percent));
        });
        return result;
    }

    private Map<String, TopicProgressResponse> buildDifficultyProgress(List<Question> all, java.util.Set<Long> solvedIds) {
        Map<String, TopicProgressResponse> result = new LinkedHashMap<>();
        for (Difficulty d : Difficulty.values()) {
            List<Question> inDifficulty = all.stream().filter(q -> q.getDifficulty() == d).toList();
            int total = inDifficulty.size();
            int solved = (int) inDifficulty.stream().filter(q -> solvedIds.contains(q.getId())).count();
            int percent = total == 0 ? 0 : Math.round(100f * solved / total);
            result.put(d.name(), new TopicProgressResponse(d.name(), solved, total, percent));
        }
        return result;
    }

    // Consecutive days with at least one submission, counting backward from today.
    // A gap of exactly one day (nothing submitted today, but yesterday had activity) still counts.
    private int computeStreak(List<Date> submissionDates) {
        if (submissionDates.isEmpty()) return 0;

        var dates = submissionDates.stream().map(Date::toLocalDate).collect(Collectors.toCollection(java.util.TreeSet::new));
        LocalDate cursor = LocalDate.now();
        if (!dates.contains(cursor)) {
            cursor = cursor.minusDays(1);
            if (!dates.contains(cursor)) return 0;
        }

        int streak = 0;
        while (dates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }
}