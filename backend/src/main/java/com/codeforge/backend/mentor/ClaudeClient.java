package com.codeforge.backend.mentor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ClaudeClient {

    private static final String SYSTEM_PROMPT = """
            You are a friendly, encouraging coding mentor for a DSA practice platform called CodeForge.
            A student's code failed a test case. Analyze it and respond with ONLY a JSON object, no other text,
            matching exactly this shape:

            {
              "errorType": one of "Logical error", "Runtime error", "Syntax/compilation error", "Edge-case issue", "Time complexity issue",
              "explanation": a short, beginner-friendly explanation of what likely went wrong, 2-4 sentences,
              "hint": a nudge toward the fix, NOT the corrected code or the full solution,
              "conceptsToRevise": a comma-separated list of 1-3 short concept names, e.g. "HashMap, Two Pointer",
              "complexityFeedback": one sentence on the code's current time and space complexity, or null if not applicable,
              "suggestedSolution": null unless the student explicitly asked for the full solution, in which case provide clean, correct, commented Java code as a string
            }

            Never include the full corrected solution in "hint" or "explanation". Only fill "suggestedSolution"
            if explicitly told the student wants the solution. Respond with ONLY the JSON object, nothing before or after it.
            """;

    private final RestClient restClient;
    private final String model;
    private final ObjectMapper mapper = new ObjectMapper();

    public ClaudeClient(@Value("${app.claude.api-key}") String apiKey,
                        @Value("${app.claude.model}") String model) {
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.anthropic.com")
                .defaultHeader("x-api-key", apiKey == null ? "" : apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .build();
    }

    @SuppressWarnings("unchecked")
    public MentorAnalysisResult analyze(MentorAnalysisRequest req) {
        String userMessage = buildUserMessage(req);

        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 1000,
                "system", SYSTEM_PROMPT,
                "messages", List.of(Map.of("role", "user", "content", userMessage)));

        Map<String, Object> response;
        try {
            response = restClient.post()
                    .uri("/v1/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new MentorUnavailableException("AI mentor is currently unavailable");
        }

        if (response == null || !response.containsKey("content")) {
            throw new MentorUnavailableException("AI mentor returned no result");
        }

        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        String text = (String) content.get(0).get("text");

        try {
            String json = extractJson(text);
            Map<String, Object> parsed = mapper.readValue(json, Map.class);
            return new MentorAnalysisResult(
                    (String) parsed.get("errorType"),
                    (String) parsed.get("explanation"),
                    (String) parsed.get("hint"),
                    (String) parsed.get("conceptsToRevise"),
                    (String) parsed.get("complexityFeedback"),
                    (String) parsed.get("suggestedSolution"));
        } catch (Exception e) {
            throw new MentorUnavailableException("AI mentor response could not be understood");
        }
    }

    // Claude is instructed to return only JSON, but this strips any stray text just in case
    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start == -1 || end == -1 || end < start) {
            throw new IllegalStateException("No JSON object found in response");
        }
        return text.substring(start, end + 1);
    }

    private String buildUserMessage(MentorAnalysisRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("Problem: ").append(req.questionTitle()).append("\n\n");
        sb.append("Statement:\n").append(req.questionStatement()).append("\n\n");
        sb.append("Student's code:\n```java\n").append(req.sourceCode()).append("\n```\n\n");
        sb.append("Result: ").append(req.submissionStatus())
          .append(" on test case ").append(req.caseNumber())
          .append(req.sample() ? " (a visible sample case)" : " (a hidden case; input and expected output are not shown to the student, so do not guess or invent them)")
          .append("\n");
        if (req.sample()) {
            sb.append("Input: ").append(req.input()).append("\n");
            sb.append("Expected output: ").append(req.expectedOutput()).append("\n");
            sb.append("Actual output: ").append(req.actualOutput()).append("\n");
        }
        if (req.errorMessage() != null) {
            sb.append("Error/compiler message: ").append(req.errorMessage()).append("\n");
        }
        if (req.wantsSolution()) {
            sb.append("\nThe student has explicitly asked for the full solution this time. Fill suggestedSolution with clean, correct, commented Java code.\n");
        } else {
            sb.append("\nGive a hint only. Do not reveal the full corrected code.\n");
        }
        return sb.toString();
    }
}