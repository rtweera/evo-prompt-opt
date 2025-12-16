package com.evopromptopt.core.execution;

public record ExecutionResult(
        String response,
        boolean success,
        String errorMessage,
        long executionTimeMs,
        int outputTokens,
        int inputTokens
) {
    public int getTotalTokens() {
        return inputTokens + outputTokens;
    }

    public static ExecutionResult failure(String errorMessage, long executionTimeMs) {
        return new ExecutionResult("", false, errorMessage, executionTimeMs, 0, 0);
    }

    public static ExecutionResult success(String response, long executionTimeMs, int outputTokens, int inputTokens) {
        return new ExecutionResult(response, true, null, executionTimeMs, outputTokens, inputTokens);
    }
}
