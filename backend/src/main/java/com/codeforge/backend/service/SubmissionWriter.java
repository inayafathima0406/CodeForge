package com.codeforge.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.entity.Language;
import com.codeforge.backend.entity.Question;
import com.codeforge.backend.entity.Submission;
import com.codeforge.backend.entity.SubmissionResult;
import com.codeforge.backend.entity.User;
import com.codeforge.backend.repository.SubmissionRepository;
import com.codeforge.backend.repository.SubmissionResultRepository;

// A separate bean so @Transactional actually applies (Spring proxies cannot
// intercept a method called on "this" from within the same class).
@Service
public class SubmissionWriter {

    private final SubmissionRepository submissionRepository;
    private final SubmissionResultRepository submissionResultRepository;

    public SubmissionWriter(SubmissionRepository submissionRepository,
                            SubmissionResultRepository submissionResultRepository) {
        this.submissionRepository = submissionRepository;
        this.submissionResultRepository = submissionResultRepository;
    }

    @Transactional
    public Submission save(User user, Question question, Language language, String sourceCode,
                           String status, Integer time, Integer memory, int passed, int total,
                           List<SubmissionService.RunOutcome> outcomes) {
        Submission submission = submissionRepository.save(
                new Submission(user, question, language, sourceCode, status, time, memory, passed, total));

        for (SubmissionService.RunOutcome o : outcomes) {
            submissionResultRepository.save(new SubmissionResult(
                    submission, o.testCase(), o.number(), o.status(),
                    o.exec().timeSeconds() == null ? null : (int) Math.round(o.exec().timeSeconds() * 1000),
                    o.exec().memoryKb()));
        }
        return submission;
    }
}