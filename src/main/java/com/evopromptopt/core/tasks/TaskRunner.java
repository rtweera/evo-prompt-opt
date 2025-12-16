package com.evopromptopt.core.tasks;

import com.evopromptopt.core.evaluation.EvaluationMetric;
import com.evopromptopt.core.execution.ExecutionResult;
import com.evopromptopt.core.execution.PromptExecutor;
import com.evopromptopt.core.genome.PromptGenome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executes task evaluation for prompt genomes and calculates fitness scores
 */
public class TaskRunner {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    private final PromptExecutor promptExecutor;
    private final ExecutorService executorService;
    private final boolean parallelExecution;

    public TaskRunner(PromptExecutor promptExecutor, boolean parallelExecution) {
        this.promptExecutor = promptExecutor;
        this.parallelExecution = parallelExecution;
        this.executorService = parallelExecution ?
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) : null;
    }

    public TaskRunner(PromptExecutor promptExecutor) {
        this(promptExecutor, false); // Default to sequential execution
    }

    /**
     * Evaluates a prompt genome against a task definition and returns fitness score
     */
    public TaskEvaluationResult evaluateGenome(PromptGenome genome, TaskDefinition task) {
        List<TestCaseResult> testCaseResults = new ArrayList<>();

        if (parallelExecution && task.getTestCases().size() > 1) {
            // Parallel execution of test cases
            List<CompletableFuture<TestCaseResult>> futures = task.getTestCases().stream()
                .map(testCase -> CompletableFuture.supplyAsync(() ->
                    executeTestCase(genome, testCase, task.getEvaluationMetrics()), executorService))
                .toList();

            // Collect results
            for (CompletableFuture<TestCaseResult> future : futures) {
                try {
                    testCaseResults.add(future.get());
                } catch (Exception e) {
                    logger.error("Failed to execute test case", e);
                    testCaseResults.add(new TestCaseResult("", "", 0.0, 0, false, e.getMessage()));
                }
            }
        } else {
            // Sequential execution
            for (TaskDefinition.TestCase testCase : task.getTestCases()) {
                testCaseResults.add(executeTestCase(genome, testCase, task.getEvaluationMetrics()));
            }
        }

        // Calculate overall fitness score
        double totalScore = testCaseResults.stream()
            .mapToDouble(TestCaseResult::score)
            .average()
            .orElse(0.0);

        // Calculate success rate
        long successCount = testCaseResults.stream()
            .mapToLong(result -> result.success() ? 1 : 0)
            .sum();
        double successRate = (double) successCount / testCaseResults.size();

        // Calculate total execution time
        long totalExecutionTime = testCaseResults.stream()
            .mapToLong(TestCaseResult::executionTimeMs)
            .sum();

        return new TaskEvaluationResult(
            task.getName(),
            totalScore,
            successRate,
            totalExecutionTime,
            testCaseResults
        );
    }

    private TestCaseResult executeTestCase(PromptGenome genome, TaskDefinition.TestCase testCase,
                                         List<EvaluationMetric> evaluationMetrics) {
        try {
            // Execute the prompt
            ExecutionResult executionResult = promptExecutor.execute(genome, testCase.getInput());

            if (!executionResult.success()) {
                return new TestCaseResult(
                    testCase.getInput(),
                    "",
                    0.0,
                    executionResult.executionTimeMs(),
                    false,
                    executionResult.errorMessage()
                );
            }

            // Calculate score using evaluation metrics
            double totalScore = 0.0;
            int metricCount = evaluationMetrics.size();

            for (EvaluationMetric metric : evaluationMetrics) {
                double score = metric.evaluate(
                    testCase.getInput(),
                    testCase.getExpectedOutput(),
                    executionResult.response(),
                    executionResult
                );
                totalScore += score;
                logger.debug("Metric '{}' scored: {}", metric.getName(), score);
            }

            // Average score across all metrics
            double averageScore = metricCount > 0 ? totalScore / metricCount : 0.0;

            return new TestCaseResult(
                testCase.getInput(),
                executionResult.response(),
                averageScore,
                executionResult.executionTimeMs(),
                true,
                null
            );

        } catch (Exception e) {
            logger.error("Failed to execute test case: {}", testCase.getInput(), e);
            return new TestCaseResult(
                testCase.getInput(),
                "",
                0.0,
                0,
                false,
                "Execution failed: " + e.getMessage()
            );
        }
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Result of evaluating a single test case
     */
    public record TestCaseResult(
        String input,
        String actualOutput,
        double score,
        long executionTimeMs,
        boolean success,
        String errorMessage
    ) {}

    /**
     * Result of evaluating a genome against an entire task
     */
    public record TaskEvaluationResult(
        String taskName,
        double overallScore,
        double successRate,
        long totalExecutionTimeMs,
        List<TestCaseResult> testCaseResults
    ) {}
}
