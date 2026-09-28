package com.codeforge.backend.execution;

public interface CodeExecutor {

    ExecutionResult execute(String sourceCode, String stdin, int languageId, int timeLimitMs);
}
