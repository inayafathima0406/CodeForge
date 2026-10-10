package com.codeforge.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.request.CodeRequest;
import com.codeforge.backend.dto.response.CodingTestSummaryResponse;
import com.codeforge.backend.dto.response.SubmissionDetailResponse;
import com.codeforge.backend.dto.response.TestAttemptResponse;
import com.codeforge.backend.dto.response.TestQuestionResponse;
import com.codeforge.backend.dto.response.TestResultResponse;
import com.codeforge.backend.entity.CodingTest;
import com.codeforge.backend.entity.CodingTestQuestion;
import com.codeforge.backend.entity.Submission;
import com.codeforge.backend.entity.TestAttempt;
import com.codeforge.backend.entity.User;
import com.codeforge.backend.exception.DuplicateResourceException;
import com.codeforge.backend.exception.ResourceNotFoundException;
import com.codeforge.backend.repository.CodingTestQuestionRepository;
import com.codeforge.backend.repository.CodingTestRepository;
import com.codeforge.backend.repository.SubmissionRepository;
import com.codeforge.backend.repository.TestAttemptRepository;
import com.codeforge.backend.repository.UserRepository;

@Service
public class TestAttemptService {

    private final CodingTestRepository codingTestRepository;
    private final CodingTestQuestionRepository codingTestQuestionRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final SubmissionService submissionService;

    public TestAttemptService(CodingTestRepository codingTestRepository,
                              CodingTestQuestionRepository codingTestQuestionRepository,
                              TestAttemptRepository testAttemptRepository,
                              SubmissionRepository submissionRepository,
                              UserRepository userRepository,
                              SubmissionService submissionService) {
        this.codingTestRepository = codingTestRepository;
        this.codingTestQuestionRepository = codingTestQuestionRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.submissionService = submissionService;
    }

    @Transactional(readOnly = true)
    public List<CodingTestSummaryResponse> listTests() {
        return codingTestRepository.findByActiveTrue().stream()
                .map(t -> new CodingTestSummaryResponse(t.getId(), t.getTitle(), t.getDurationMinutes(),
                        codingTestQuestionRepository.findByCodingTestIdOrderByDisplayOrderAsc(t.getId()).size()))
                .toList();
    }

    @Transactional
    public TestAttemptResponse start(Long testId, String username) {
        CodingTest test = codingTestRepository.findByIdAndActiveTrue(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        testAttemptRepository.findByUserIdAndCodingTestIdAndStatus(user.getId(), testId, "IN_PROGRESS")
                .ifPresent(a -> { throw new DuplicateResourceException("You already have this test in progress"); });

        List<CodingTestQuestion> questions = codingTestQuestionRepository.findByCodingTestIdOrderByDisplayOrderAsc(testId);
        int totalScore = questions.stream().mapToInt(CodingTestQuestion::getPoints).sum();

        TestAttempt attempt = testAttemptRepository.save(new TestAttempt(user, test, totalScore));

        return toAttemptResponse(attempt, questions);
    }

    @Transactional(readOnly = true)
    public TestAttemptResponse getAttempt(Long attemptId, String username) {
        TestAttempt attempt = testAttemptRepository.findByIdAndUsername(attemptId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));
        List<CodingTestQuestion> questions =
                codingTestQuestionRepository.findByCodingTestIdOrderByDisplayOrderAsc(attempt.getCodingTest().getId());
        return toAttemptResponse(attempt, questions);
    }

    // Submits within a test attempt: validates the attempt is still in progress and not timed out,
    // then delegates to the normal submission pipeline with the attempt id attached.
    public SubmissionDetailResponse submitWithinAttempt(Long attemptId, Long questionId, CodeRequest request,
                                                        Authentication auth) {
        TestAttempt attempt = testAttemptRepository.findByIdAndUsername(attemptId, auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (!attempt.getStatus().equals("IN_PROGRESS")) {
            throw new IllegalStateException("This test attempt is no longer active");
        }
        if (isExpired(attempt)) {
            finish(attemptId, auth.getName(), "TIME_UP");
            throw new IllegalStateException("Time is up for this test");
        }

        return submissionService.submit(questionId, request, auth, attemptId);
    }

    @Transactional
    public TestResultResponse finish(Long attemptId, String username, String reason) {
        TestAttempt attempt = testAttemptRepository.findByIdAndUsername(attemptId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));

        if (!attempt.getStatus().equals("IN_PROGRESS")) {
            return toResultResponse(attempt, 0, codingTestQuestionRepository
                    .findByCodingTestIdOrderByDisplayOrderAsc(attempt.getCodingTest().getId()).size());
        }

        List<CodingTestQuestion> questions =
                codingTestQuestionRepository.findByCodingTestIdOrderByDisplayOrderAsc(attempt.getCodingTest().getId());
        List<Submission> attemptSubmissions = submissionRepository.findByUsernameWithQuestion(username).stream()
                .filter(s -> attemptId.equals(s.getTestAttemptId()))
                .toList();

        int score = 0;
        int solved = 0;
        for (CodingTestQuestion ctq : questions) {
            boolean hasAccepted = attemptSubmissions.stream()
                    .anyMatch(s -> s.getQuestion().getId().equals(ctq.getQuestion().getId())
                            && s.getStatus().equals("ACCEPTED"));
            if (hasAccepted) {
                score += ctq.getPoints();
                solved++;
            }
        }

        attempt.setScore(score);
        attempt.setStatus(reason);
        attempt.setSubmittedAt(LocalDateTime.now());
        testAttemptRepository.save(attempt);

        return toResultResponse(attempt, solved, questions.size());
    }

    @Transactional(readOnly = true)
    public List<TestResultResponse> history(String username) {
        return testAttemptRepository.findByUsernameWithTest(username).stream()
                .map(a -> {
                    List<CodingTestQuestion> qs = codingTestQuestionRepository
                            .findByCodingTestIdOrderByDisplayOrderAsc(a.getCodingTest().getId());
                    int solved = a.getTotalScore() == 0 ? 0 :
                            (int) Math.round((double) a.getScore() / a.getTotalScore() * qs.size());
                    return toResultResponse(a, solved, qs.size());
                })
                .toList();
    }

    private boolean isExpired(TestAttempt attempt) {
        LocalDateTime deadline = attempt.getStartedAt().plusMinutes(attempt.getCodingTest().getDurationMinutes());
        return LocalDateTime.now().isAfter(deadline);
    }

    private TestAttemptResponse toAttemptResponse(TestAttempt attempt, List<CodingTestQuestion> questions) {
        List<TestQuestionResponse> qList = questions.stream()
                .map(ctq -> new TestQuestionResponse(
                        ctq.getQuestion().getId(), ctq.getQuestion().getTitle(),
                        ctq.getQuestion().getDifficulty().name(), ctq.getPoints()))
                .toList();
        return new TestAttemptResponse(
                attempt.getId(), attempt.getCodingTest().getId(), attempt.getCodingTest().getTitle(),
                attempt.getStartedAt(), attempt.getCodingTest().getDurationMinutes(), attempt.getStatus(), qList);
    }

    private TestResultResponse toResultResponse(TestAttempt attempt, int solved, int totalQuestions) {
        return new TestResultResponse(
                attempt.getId(), attempt.getCodingTest().getTitle(), attempt.getStatus(),
                attempt.getScore(), attempt.getTotalScore(), solved, totalQuestions,
                attempt.getStartedAt(), attempt.getSubmittedAt());
    }
}