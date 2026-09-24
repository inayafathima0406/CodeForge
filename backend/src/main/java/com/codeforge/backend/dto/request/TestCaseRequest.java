package com.codeforge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TestCaseRequest(

        @NotBlank(message = "Input data is required")
        String inputData,

        @NotBlank(message = "Expected output is required")
        String expectedOutput,

        boolean sample,

        String explanation
) {
}