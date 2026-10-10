package com.codeforge.backend.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codeforge.backend.dto.request.CodeRequest;
import com.codeforge.backend.dto.response.CodingTestSummaryResponse;
import com.codeforge.backend.dto.response.SubmissionDetailResponse;
import com.codeforge.backend.dto.response.TestAttemptResponse;
import com.codeforge.backend.dto.response.TestResultResponse;
import com.codeforge.backend.service.TestAttemptService;

import jakarta.validation.Valid;

@RestController
public class TestAttemptController {

    private final TestAttemptService testAttemptService;

    public TestAttemptController(TestAttemptService testAttemptService) {
        this.testAttemptService = testAttemptService;
    }

    @GetMapping("/api/tests")
    public List<CodingTestSummaryResponse> listTests() {
        return testAttemptService.listTests();
    }

    @PostMapping("/api/tests/{id}/start")
    public TestAttemptResponse start(@PathVariable Long id, Authentication auth) {
        return testAttemptService.start(id, auth.getName());
    }

    @GetMapping("/api/attempts/{id}")
    public TestAttemptResponse getAttempt(@PathVariable Long id, Authentication auth) {
        return testAttemptService.getAttempt(id, auth.getName());
    }

    @PostMapping("/api/attempts/{attemptId}/questions/{questionId}/submit")
    public SubmissionDetailResponse submitWithin(@PathVariable Long attemptId, @PathVariable Long questionId,
                                                 @Valid @RequestBody CodeRequest request, Authentication auth) {
        return testAttemptService.submitWithinAttempt(attemptId, questionId, request, auth);
    }

    @PostMapping("/api/attempts/{id}/finish")
    public TestResultResponse finish(@PathVariable Long id, Authentication auth) {
        return testAttemptService.finish(id, auth.getName(), "SUBMITTED");
    }

    @GetMapping("/api/attempts")
    public List<TestResultResponse> history(Authentication auth) {
        return testAttemptService.history(auth.getName());
    }
}