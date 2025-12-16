package com.evopromptopt.core.execution;

import com.evopromptopt.core.genome.PromptGenome;

public interface PromptExecutor {
    /**
     * Executes a prompt with the given genome configuration on a specific task
     *
     * @param genome The prompt genome containing all parameters
     * @param task   The task/input to process
     * @return       ExecutionResult containing response and metrics
     */
    ExecutionResult execute(PromptGenome genome, String task);
}
