package com.codeforge.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeforge.backend.dto.request.CodeRequest;
import com.codeforge.backend.dto.response.RunCaseResponse;
import com.codeforge.backend.dto.response.RunResponse;
import com.codeforge.backend.entity.Question;
import com.codeforge.backend.entity.TestCase;
import com.codeforge.backend.exception.ResourceNotFoundException;
import com.codeforge.backend.execution.CodeExecutor;
import com.codeforge.backend.execution.ExecutionResult;
import com.codeforge.backend.repository.QuestionRepository;
import com.codeforge.backend.repository.TestCaseRepository;

@Service
public class RunService {

    private static final int MAX_OUTPUT_CHARS = 2000;

    private final QuestionRepository questionRepository;
    private final TestCaseRepository testCaseRepository;
    private final CodeExecutor codeExecutor;
    private final int javaLanguageId;

    public RunService(QuestionRepository questionRepository,
                      TestCaseRepository testCaseRepository,
                      CodeExecutor codeExecutor,
                      @Value("${app.judge0.java-language-id}") int javaLanguageId) {
        this.questionRepository = questionRepository;
        this.testCaseRepository = testCaseRepository;
        this.codeExecutor = codeExecutor;
        this.javaLanguageId = javaLanguageId;
    }

    // Not @Transactional on purpose: we do not want a database connection held open
    // while waiting on the external execution service.
    public RunResponse run(Long questionId, CodeRequest request) {
        Question question = questionRepository.findById(questionId)
                .filter(Question::isPublished)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        List<TestCase> samples =
                testCaseRepository.findByQuestionIdAndSampleTrueOrderByDisplayOrderAsc(questionId);

        List<RunCaseResponse> results = new ArrayList<>();
        int passed = 0;
        int number = 1;

        for (TestCase tc : samples) {
            ExecutionResult exec = codeExecutor.execute(
                    request.sourceCode(), tc.getInputData(), javaLanguageId, question.getTimeLimitMs());

            String status = mapStatus(exec, tc.getExpectedOutput());
            if (status.equals("ACCEPTED")) {
                passed++;
            }

            results.add(new RunCaseResponse(
                    number++,
                    status,
                    tc.getInputData(),
                    tc.getExpectedOutput(),
                    truncate(exec.stdout()),
                    errorMessageFor(status, exec),
                    exec.timeSeconds(),
                    exec.memoryKb()));
        }

        String overall = results.stream()
                .map(RunCaseResponse::status)
                .filter(s -> !s.equals("ACCEPTED"))
                .findFirst()
                .orElse("ACCEPTED");

        return new RunResponse(overall, passed, results.size(), results);
    }

    // Judge0 status ids: 3 accepted, 4 wrong answer, 5 time limit, 6 compile error,
    // 7 to 12 runtime errors
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

    // Ignore trailing spaces on each line, line-ending style, and trailing blank lines
    private String normalize(String text) {
        if (text == null) return "";
        String[] lines = text.replace("\r\n", "\n").split("\n", -1);
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line.stripTrailing()).append("\n");
        }
        return sb.toString().stripTrailing();
    }

    // Never return raw server details. Compiler and runtime messages are shortened.
    private String errorMessageFor(String status, ExecutionResult exec) {
        if (status.equals("COMPILATION_ERROR")) return truncate(exec.compileOutput());
        if (status.equals("RUNTIME_ERROR")) return truncate(exec.stderr());
        if (status.equals("TIME_LIMIT_EXCEEDED")) return "Your program took too long to finish.";
        return null;
    }

    private String truncate(String text) {
        if (text == null) return null;
        return text.length() <= MAX_OUTPUT_CHARS ? text : text.substring(0, MAX_OUTPUT_CHARS) + "...";
    }
}