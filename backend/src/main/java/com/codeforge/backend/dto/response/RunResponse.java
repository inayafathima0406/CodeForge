package com.codeforge.backend.dto.response;

import java.util.List;

public record RunResponse(
        String overallStatus,
        int passed,
        int total,
        List<RunCaseResponse> cases
) {
}