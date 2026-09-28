package com.codeforge.backend.execution;

public record ExecutionResult(
        String stdout,
        String stderr,
        String compileOutput,
        int statusId,
        String statusDescription,
        Double timeSeconds,
        Integer memoryKb
) {
}
