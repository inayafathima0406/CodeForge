package com.codeforge.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CodeRequest(

        @NotBlank(message = "Source code is required")
        @Size(max = 50000, message = "Source code is too long")
        String sourceCode,

        // Only "java" is supported for now. More languages are added later as data, not code.
        String language
) {
}