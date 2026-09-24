package com.codeforge.backend.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title is too long")
        String title,

        @NotBlank(message = "Statement is required")
        String statement,

        @NotBlank(message = "Input description is required")
        String inputDescription,

        @NotBlank(message = "Output description is required")
        String outputDescription,

        @NotBlank(message = "Constraints are required")
        String constraints,

        @NotNull(message = "Difficulty is required")
        String difficulty,

        @NotBlank(message = "Topic is required")
        String topic,

        @NotEmpty(message = "At least one test case is required")
        @Valid
        List<TestCaseRequest> testCases
) {
}