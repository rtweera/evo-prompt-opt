package com.evopromptopt.core.execution;

import com.evopromptopt.core.genome.PromptGenome;

public class PromptExecutor {

    public ExecutionResult execute(PromptGenome prompt, String input) {
        // Execution logic will be model-specific
        // For now, return a deterministic stub result
        return new ExecutionResult(true, 120);
    }
}
