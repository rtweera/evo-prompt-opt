package com.evopromptopt.core.execution;

public record ExecutionResult(
        boolean success,
        int tokenUsage
) {}
