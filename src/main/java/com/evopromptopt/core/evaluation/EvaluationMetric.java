package com.evopromptopt.core.evaluation;

import com.evopromptopt.core.execution.ExecutionResult;

/**
 * Interface for evaluating the quality of a model's response to a task.
 * Different implementations can provide domain-specific evaluation logic.
 */
public interface EvaluationMetric {
    /**
     * Evaluates the quality of a model response
     *
     * @param task           The original task/prompt
     * @param expectedOutput The expected output (if available)
     * @param actualOutput   The model's actual response
     * @param executionResult The full execution result with metrics
     * @return              A score between 0.0 (worst) and 1.0 (best)
     */
    double evaluate(String task, String expectedOutput, String actualOutput, ExecutionResult executionResult);

    /**
     * @return A human-readable name for this evaluation metric
     */
    String getName();

    /**
     * @return A description of what this metric measures
     */
    String getDescription();
}
