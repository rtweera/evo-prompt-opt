package com.evopromptopt.core.evaluation;

import com.evopromptopt.core.execution.ExecutionResult;

/**
 * Evaluates accuracy by checking if the actual output matches the expected output.
 * Useful for tasks with definitive correct answers like math problems or classification.
 */
public class AccuracyEvaluator implements EvaluationMetric {
    private final boolean caseSensitive;
    private final boolean trimWhitespace;

    public AccuracyEvaluator(boolean caseSensitive, boolean trimWhitespace) {
        this.caseSensitive = caseSensitive;
        this.trimWhitespace = trimWhitespace;
    }

    public AccuracyEvaluator() {
        this(false, true); // Default: case-insensitive, trim whitespace
    }

    @Override
    public double evaluate(String task, String expectedOutput, String actualOutput, ExecutionResult executionResult) {
        if (!executionResult.success() || actualOutput == null || expectedOutput == null) {
            return 0.0;
        }

        String expected = expectedOutput;
        String actual = actualOutput;

        if (trimWhitespace) {
            expected = expected.trim();
            actual = actual.trim();
        }

        if (!caseSensitive) {
            expected = expected.toLowerCase();
            actual = actual.toLowerCase();
        }

        return expected.equals(actual) ? 1.0 : 0.0;
    }

    @Override
    public String getName() {
        return "Accuracy";
    }

    @Override
    public String getDescription() {
        return "Measures exact match between expected and actual output";
    }
}
