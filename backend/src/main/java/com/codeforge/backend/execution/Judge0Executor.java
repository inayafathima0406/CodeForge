package com.codeforge.backend.execution;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class Judge0Executor implements CodeExecutor {

    private final RestClient restClient;

    public Judge0Executor(@Value("${app.judge0.url}") String baseUrl,
                          @Value("${app.judge0.api-key}") String apiKey) {
        RestClient.Builder builder = RestClient.builder().baseUrl(baseUrl);
        if (apiKey != null && !apiKey.isBlank()) {
            // Only needed for hosted plans such as RapidAPI. The key never reaches the browser.
            builder.defaultHeader("X-RapidAPI-Key", apiKey);
        }
        this.restClient = builder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ExecutionResult execute(String sourceCode, String stdin, int languageId, int timeLimitMs) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("language_id", languageId);
        request.put("source_code", sourceCode);
        request.put("stdin", stdin == null ? "" : stdin);
        request.put("cpu_time_limit", Math.max(1, timeLimitMs / 1000.0));

        Map<String, Object> response;
        try {
            response = restClient.post()
                    .uri("/submissions?base64_encoded=false&wait=true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            throw new ExecutionUnavailableException("Code execution service is unavailable");
        }

        if (response == null) {
            throw new ExecutionUnavailableException("Code execution service returned no result");
        }

        Map<String, Object> status = (Map<String, Object>) response.get("status");
        int statusId = status == null ? 0 : ((Number) status.get("id")).intValue();
        String statusDescription = status == null ? "Unknown" : String.valueOf(status.get("description"));

        Object time = response.get("time");
        Object memory = response.get("memory");

        return new ExecutionResult(
                (String) response.get("stdout"),
                (String) response.get("stderr"),
                (String) response.get("compile_output"),
                statusId,
                statusDescription,
                time == null ? null : Double.valueOf(time.toString()),
                memory == null ? null : ((Number) memory).intValue());
    }
}
