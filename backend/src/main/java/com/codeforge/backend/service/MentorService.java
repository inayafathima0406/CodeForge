package com.codeforge.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.response.MentorResponse;
import com.codeforge.backend.entity.MentorFeedback;
import com.codeforge.backend.entity.Submission;
import com.codeforge.backend.entity.SubmissionResult;
import com.codeforge.backend.exception.ResourceNotFoundException;
import com.codeforge.backend.mentor.ClaudeClient;
import com.codeforge.backend.mentor.MentorAnalysisRequest;
import com.codeforge.backend.mentor.MentorAnalysisResult;
import com.codeforge.backend.repository.MentorFeedbackRepository;
import com.codeforge.backend.repository.SubmissionRepository;
import com.codeforge.backend.repository.SubmissionResultRepository;
import com.codeforge.backend.repository.TestCaseRepository;

@Service
public class MentorService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionResultRepository submissionResultRepository;
    private final MentorFeedbackRepository mentorFeedbackRepository;
    private final TestCaseRepository testCaseRepository;
    private final ClaudeClient claudeClient;

    public MentorService(SubmissionRepository submissionRepository,
                         SubmissionResultRepository submissionResultRepository,
                         MentorFeedbackRepository mentorFeedbackRepository,
                         TestCaseRepository testCaseRepository,
                         ClaudeClient claudeClient) {
        this.submissionRepository = submissionRepository;
        this.submissionResultRepository = submissionResultRepository;
        this.mentorFeedbackRepository = mentorFeedbackRepository;
        this.testCaseRepository = testCaseRepository;
        this.claudeClient = claudeClient;
    }

    @Transactional
    public MentorResponse getFeedback(Long submissionId, String username, boolean wantsSolution) {
        Submission submission = submissionRepository.findByIdAndUsername(submissionId, username)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        if (submission.getStatus().equals("ACCEPTED")) {
            throw new IllegalStateException("This submission already passed. No mentor feedback needed.");
        }

        var existing = mentorFeedbackRepository.findBySubmissionId(submissionId);

        // Already analyzed, and either we're not being asked for the solution now,
        // or the solution was already given before: just return the saved result.
        if (existing.isPresent()) {
            MentorFeedback f = existing.get();
            if (!wantsSolution || f.getSuggestedSolution() != null) {
                return toResponse(f);
            }
            // Solution requested for the first time on an already-analyzed submission:
            // call Claude again, just for the solution, and attach it to the existing row.
            MentorAnalysisResult result = callClaude(submission, username, true);
            f.setSuggestedSolution(result.suggestedSolution());
            mentorFeedbackRepository.save(f);
            return toResponse(f);
        }

        MentorAnalysisResult result = callClaude(submission, username, wantsSolution);

        MentorFeedback feedback = new MentorFeedback(
                submission, result.errorType(), result.explanation(), result.hint(),
                result.conceptsToRevise(), result.complexityFeedback());
        if (wantsSolution) {
            feedback.setSuggestedSolution(result.suggestedSolution());
        }
        mentorFeedbackRepository.save(feedback);

        return toResponse(feedback);
    }

    private MentorAnalysisResult callClaude(Submission submission, String username, boolean wantsSolution) {
        SubmissionResult failedCase = submissionResultRepository
                .findBySubmissionIdOrderByCaseNumberAsc(submission.getId()).stream()
                .filter(r -> !r.getStatus().equals("ACCEPTED"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No failing case found for this submission"));

        long sampleCount = testCaseRepository
                .findByQuestionIdAndSampleTrueOrderByDisplayOrderAsc(submission.getQuestion().getId()).size();
        boolean sample = failedCase.getCaseNumber() <= sampleCount;

        MentorAnalysisRequest req = new MentorAnalysisRequest(
                submission.getQuestion().getTitle(),
                submission.getQuestion().getStatement(),
                submission.getSourceCode(),
                failedCase.getStatus(),
                failedCase.getCaseNumber(),
                sample,
                null, null, null, null,
                wantsSolution);

        return claudeClient.analyze(req);
    }

    private MentorResponse toResponse(MentorFeedback f) {
        return new MentorResponse(
                f.getErrorType(), f.getExplanation(), f.getHint(),
                f.getConceptsToRevise(), f.getComplexityFeedback(), f.getSuggestedSolution());
    }
}