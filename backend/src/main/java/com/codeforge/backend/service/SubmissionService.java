package com.codeforge.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.request.CodeRequest;
import com.codeforge.backend.dto.response.SubmissionDetailResponse;
import com.codeforge.backend.dto.response.SubmissionSummaryResponse;
import com.codeforge.backend.dto.response.SubmitCaseResponse;
import com.codeforge.backend.entity.Language;
import com.codeforge.backend.entity.Question;
import com.codeforge.backend.entity.Submission;
import com.codeforge.backend.entity.SubmissionResult;
import com.codeforge.backend.entity.TestCase;
import com.codeforge.backend.entity.User;
import com.codeforge.backend.exception.ResourceNotFoundException;
import com.codeforge.backend.execution.CodeExecutor;
import com.codeforge.backend.execution.ExecutionResult;
import com.codeforge.backend.repository.LanguageRepository;
import com.codeforge.backend.repository.QuestionRepository;
import com.codeforge.backend.repository.SubmissionRepository;
import com.codeforge.backend.repository.SubmissionResultRepository;
import com.codeforge.backend.repository.TestCaseRepository;
import com.codeforge.backend.repository.UserRepository;

@Service
public class SubmissionService {

    private static final int MAX_OUTPUT_CHARS = 2000;

    private final QuestionRepository questionRepository;
    private final TestCaseRepository testCaseRepository;
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionResultRepository submissionResultRepository;
    private final SubmissionWriter submissionWriter;
    private final CodeExecutor codeExecutor;
    private final int javaLanguageId;

    public SubmissionService(QuestionRepository questionRepository,
                             TestCaseRepository testCaseRepository,
                             UserRepository userRepository,
                             LanguageRepository languageRepository,
                             SubmissionRepository submissionRepository,
                             SubmissionResultRepository submissionResultRepository,
                             SubmissionWriter submissionWriter,
                             CodeExecutor codeExecutor,
                             @Value("${app.judge0.java-language-id}") int javaLanguageId) {
        this.questionRepository = questionRepository;
        this.testCaseRepository = testCaseRepository;
        this.userRepository = userRepository;
        this.languageRepository = languageRepository;
        this.submissionRepository = submissionRepository;
        this.submissionResultRepository = submissionResultRepository;
        this.submissionWriter = submissionWriter;
        this.codeExecutor = codeExecutor;
        this.javaLanguageId = javaLanguageId;
    }

    // No @Transactional here: Judge0 calls can take several seconds each,
    // and we do not want a database connection held open while waiting on them.
public SubmissionDetailResponse submit(Long questionId, CodeRequest request, Authentication auth) {
        return submit(questionId, request, auth, null);
    }

    public SubmissionDetailResponse submit(Long questionId, CodeRequest request, Authentication auth, Long testAttemptId) {
        Question question = questionRepository.findById(questionId)
                .filter(Question::isPublished)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Language language = languageRepository.findByCodeAndActiveTrue("java")
                .orElseThrow(() -> new ResourceNotFoundException("Language not supported"));

        List<TestCase> allCases = testCaseRepository.findByQuestionIdOrderByDisplayOrderAsc(questionId);

        List<SubmitCaseResponse> caseResponses = new ArrayList<>();
        List<RunOutcome> outcomes = new ArrayList<>();
        String finalStatus = "ACCEPTED";
        int number = 1;
        boolean stopped = false;

        for (TestCase tc : allCases) {
            if (stopped) {
                // Skip remaining cases after a compile error; they would all fail the same way
                break;
            }

            ExecutionResult exec = codeExecutor.execute(
                    request.sourceCode(), tc.getInputData(), javaLanguageId, question.getTimeLimitMs());

            String status = mapStatus(exec, tc.getExpectedOutput());

            outcomes.add(new RunOutcome(tc, number, status, exec));
            caseResponses.add(toResponse(tc, number, status, exec));

            if (!status.equals("ACCEPTED") && finalStatus.equals("ACCEPTED")) {
                finalStatus = status;
            }
            if (status.equals("COMPILATION_ERROR")) {
                stopped = true;
            }
            number++;
        }

        int passed = (int) outcomes.stream().filter(o -> o.status.equals("ACCEPTED")).count();
        Integer maxTime = outcomes.stream()
                .map(o -> o.exec.timeSeconds())
                .filter(t -> t != null)
                .map(t -> (int) Math.round(t * 1000))
                .max(Integer::compareTo).orElse(null);
        Integer maxMemory = outcomes.stream()
                .map(o -> o.exec.memoryKb())
                .filter(m -> m != null)
                .max(Integer::compareTo).orElse(null);

        Submission submission = submissionWriter.save(user, question, language, request.sourceCode(),
                finalStatus, maxTime, maxMemory, passed, allCases.size(), outcomes, testAttemptId);

        return new SubmissionDetailResponse(
                submission.getId(), question.getId(), question.getTitle(), language.getName(),
                finalStatus, passed, allCases.size(), maxTime, maxMemory,
                submission.getCreatedAt(), request.sourceCode(), caseResponses);
    }


    @Transactional(readOnly = true)
    public List<SubmissionSummaryResponse> listForUser(String username) {
        return submissionRepository.findByUsernameWithQuestion(username).stream()
                .map(s -> new SubmissionSummaryResponse(
                        s.getId(), s.getQuestion().getId(), s.getQuestion().getTitle(),
                        s.getQuestion().getTopic().getName(), s.getStatus(),
                        s.getPassedCount(), s.getTotalCount(), s.getExecTimeMs(), s.getMemoryKb(),
                        s.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public SubmissionDetailResponse getForUser(Long id, String username) {
        Submission s = submissionRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        List<SubmissionResult> results =
                submissionResultRepository.findBySubmissionIdOrderByCaseNumberAsc(id);

        List<SubmitCaseResponse> cases = results.stream()
                .map(r -> {
                    boolean sample = r.getCaseNumber() <= countSamples(s.getQuestion().getId());
                    return new SubmitCaseResponse(
                            r.getCaseNumber(), sample, r.getStatus(),
                            null, null, null, null,
                            r.getExecTimeMs() == null ? null : r.getExecTimeMs() / 1000.0,
                            r.getMemoryKb());
                })
                .toList();

        return new SubmissionDetailResponse(
                s.getId(), s.getQuestion().getId(), s.getQuestion().getTitle(), s.getLanguage().getName(),
                s.getStatus(), s.getPassedCount(), s.getTotalCount(), s.getExecTimeMs(), s.getMemoryKb(),
                s.getCreatedAt(), s.getSourceCode(), cases);
    }

    private long countSamples(Long questionId) {
        return testCaseRepository.findByQuestionIdAndSampleTrueOrderByDisplayOrderAsc(questionId).size();
    }

    private String mapStatus(ExecutionResult exec, String expected) {
        int id = exec.statusId();
        if (id == 6) return "COMPILATION_ERROR";
        if (id == 5) return "TIME_LIMIT_EXCEEDED";
        if (id >= 7 && id <= 12) return "RUNTIME_ERROR";
        if (id == 3 || id == 4) {
            return normalize(exec.stdout()).equals(normalize(expected)) ? "ACCEPTED" : "WRONG_ANSWER";
        }
        return "RUNTIME_ERROR";
    }

    private String normalize(String text) {
        if (text == null) return "";
        String[] lines = text.replace("\r\n", "\n").split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line.stripTrailing()).append("\n");
        }
        return sb.toString().stripTrailing();
    }

    // Hidden test cases never expose their input, expected output, or the student's actual output.
    // Only sample cases show that detail, matching what Run already reveals.
    private SubmitCaseResponse toResponse(TestCase tc, int number, String status, ExecutionResult exec) {
        boolean sample = tc.isSample();
        String errorMessage = null;
        if (status.equals("COMPILATION_ERROR")) errorMessage = truncate(exec.compileOutput());
        else if (status.equals("RUNTIME_ERROR")) errorMessage = truncate(exec.stderr());
        else if (status.equals("TIME_LIMIT_EXCEEDED")) errorMessage = "Your program took too long to finish.";

        return new SubmitCaseResponse(
                number, sample, status,
                sample ? tc.getInputData() : null,
                sample ? tc.getExpectedOutput() : null,
                sample ? truncate(exec.stdout()) : null,
                errorMessage,
                exec.timeSeconds(), exec.memoryKb());
    }

    private String truncate(String text) {
        if (text == null) return null;
        return text.length() <= MAX_OUTPUT_CHARS ? text : text.substring(0, MAX_OUTPUT_CHARS) + "...";
    }

    record RunOutcome(TestCase testCase, int number, String status, ExecutionResult exec) {
    }
}