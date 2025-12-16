package com.evopromptopt.core.tasks;

import com.evopromptopt.core.evaluation.EvaluationMetric;
import java.util.List;
import java.util.Map;

/**
 * Represents a task for prompt optimization with test cases and evaluation criteria.
 */
public class TaskDefinition {
    private final String name;
    private final String description;
    private final List<TestCase> testCases;
    private final List<EvaluationMetric> evaluationMetrics;
    private final Map<String, Object> configuration;

    public TaskDefinition(String name, String description, List<TestCase> testCases,
                         List<EvaluationMetric> evaluationMetrics, Map<String, Object> configuration) {
        this.name = name;
        this.description = description;
        this.testCases = testCases;
        this.evaluationMetrics = evaluationMetrics;
        this.configuration = configuration;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<TestCase> getTestCases() { return testCases; }
    public List<EvaluationMetric> getEvaluationMetrics() { return evaluationMetrics; }
    public Map<String, Object> getConfiguration() { return configuration; }

    /**
     * Represents a single test case within a task
     */
    public static class TestCase {
        private final String input;
        private final String expectedOutput;
        private final Map<String, Object> metadata;

        public TestCase(String input, String expectedOutput, Map<String, Object> metadata) {
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.metadata = metadata;
        }

        public TestCase(String input, String expectedOutput) {
            this(input, expectedOutput, Map.of());
        }

        public String getInput() { return input; }
        public String getExpectedOutput() { return expectedOutput; }
        public Map<String, Object> getMetadata() { return metadata; }
    }
}
