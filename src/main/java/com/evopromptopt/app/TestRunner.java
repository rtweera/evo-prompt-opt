package com.evopromptopt.app;

import com.evopromptopt.core.evolution.EvolutionEngineFactory;
import com.evopromptopt.core.execution.MockPromptExecutor;
import com.evopromptopt.core.genome.PromptGenome;
import com.evopromptopt.core.genome.PromptGenotypeFactory;
import com.evopromptopt.core.tasks.TaskDefinition;
import com.evopromptopt.core.tasks.TaskLoader;
import com.evopromptopt.core.tasks.TaskRunner;
import io.jenetics.IntegerGene;
import io.jenetics.engine.EvolutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Test runner that demonstrates the evolutionary prompt optimization system
 * using a mock executor (no Ollama required)
 */
public class TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    public static void main(String[] args) {
        String taskFile = args.length > 0 ? args[0] : "src/main/java/com/evopromptopt/tasks/sample_tasks.json";
        int generations = args.length > 1 ? Integer.parseInt(args[1]) : 10;
        int populationSize = args.length > 2 ? Integer.parseInt(args[2]) : 20;

        logger.info("Starting evolutionary prompt optimization test...");
        logger.info("Task file: {}, Generations: {}, Population: {}", taskFile, generations, populationSize);

        try {
            // Initialize components with mock executor
            var mockExecutor = new MockPromptExecutor();

            // Load task definition
            var taskLoader = new TaskLoader();
            TaskDefinition task;

            try {
                String taskJson = Files.readString(Paths.get(taskFile));
                task = taskLoader.loadFromJson(taskJson);
                logger.info("Loaded task: {} with {} test cases", task.getName(), task.getTestCases().size());
            } catch (Exception e) {
                logger.error("Failed to load task file: {}", taskFile, e);
                return;
            }

            // Create task runner
            var taskRunner = new TaskRunner(mockExecutor, false);

            // Test a single genome first
            logger.info("Testing a single random genome...");
            var testGenome = PromptGenotypeFactory.decode(PromptGenotypeFactory.create());
            var testResult = taskRunner.evaluateGenome(testGenome, task);
            logger.info("Single genome test - Score: {:.4f}, Success rate: {:.2f}%",
                       testResult.overallScore(), testResult.successRate() * 100);

            // Create evolution engine
            var engine = EvolutionEngineFactory.create(taskRunner, task, populationSize, generations);

            logger.info("Starting evolution with {} generations...", generations);

            // Run evolution
            var evolutionStream = engine.stream()
                .limit(generations)
                .peek(result -> {
                    if (result.generation() % 2 == 0 || result.generation() == 1) {
                        logger.info("Generation {}: Best fitness = {:.4f}, Avg fitness = {:.4f}",
                                result.generation(),
                                result.bestFitness(),
                                result.population().stream().mapToDouble(p -> p.fitness()).average().orElse(0.0));
                    }
                });

            EvolutionResult<IntegerGene, Double> result = evolutionStream
                .collect(EvolutionResult.toBestEvolutionResult());

            // Display results
            displayResults(result, task);

            // Test the best genome
            testBestGenome(result, task, taskRunner);

            // Cleanup
            taskRunner.shutdown();

            logger.info("Evolution test completed successfully!");

        } catch (Exception e) {
            logger.error("Evolution test failed", e);
        }
    }

    private static void displayResults(EvolutionResult<IntegerGene, Double> result, TaskDefinition task) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("EVOLUTIONARY PROMPT OPTIMIZATION TEST RESULTS");
        System.out.println("=".repeat(60));

        System.out.printf("Task: %s%n", task.getName());
        System.out.printf("Best fitness achieved: %.4f%n", result.bestFitness());
        System.out.printf("Generation: %d%n", result.generation());
        System.out.printf("Total evaluations: %d%n", result.totalGenerations() * result.population().size());

        // Decode and display the best genome
        PromptGenome bestGenome = PromptGenotypeFactory.decode(result.bestPhenotype().genotype());

        System.out.println("\nBest Prompt Configuration:");
        System.out.println("-".repeat(40));
        System.out.printf("System Prompt: %s%n", bestGenome.systemPrompt());
        System.out.printf("Prompt Template: %s%n", bestGenome.promptTemplate());
        System.out.printf("Instruction Style: %s%n", bestGenome.instructionStyle());
        System.out.printf("Tool Policy: %s%n", bestGenome.toolPolicy());
        System.out.printf("Temperature: %.3f%n", bestGenome.temperature());
        System.out.printf("Max Tokens: %d%n", bestGenome.maxTokens());
        System.out.printf("Top P: %.3f%n", bestGenome.topP());
        System.out.printf("Top K: %d%n", bestGenome.topK());
        System.out.printf("Repeat Penalty: %.3f%n", bestGenome.repeatPenalty());
        System.out.printf("Response Format: %s%n", bestGenome.responseFormat());
    }

    private static void testBestGenome(EvolutionResult<IntegerGene, Double> result,
                                     TaskDefinition task, TaskRunner taskRunner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TESTING BEST GENOME");
        System.out.println("=".repeat(60));

        PromptGenome bestGenome = PromptGenotypeFactory.decode(result.bestPhenotype().genotype());
        var testResult = taskRunner.evaluateGenome(bestGenome, task);

        System.out.printf("Overall Score: %.4f%n", testResult.overallScore());
        System.out.printf("Success Rate: %.2f%% (%d/%d)%n",
                         testResult.successRate() * 100,
                         (int)(testResult.successRate() * testResult.testCaseResults().size()),
                         testResult.testCaseResults().size());
        System.out.printf("Total Execution Time: %d ms%n", testResult.totalExecutionTimeMs());

        System.out.println("\nDetailed Test Case Results:");
        System.out.println("-".repeat(40));

        for (int i = 0; i < testResult.testCaseResults().size(); i++) {
            var testCase = testResult.testCaseResults().get(i);
            System.out.printf("Test Case %d:%n", i + 1);
            System.out.printf("  Input: %s%n", testCase.input().length() > 50 ?
                             testCase.input().substring(0, 50) + "..." : testCase.input());
            System.out.printf("  Output: %s%n", testCase.actualOutput());
            System.out.printf("  Score: %.4f%n", testCase.score());
            System.out.printf("  Success: %s%n", testCase.success() ? "✓" : "✗");
            if (!testCase.success() && testCase.errorMessage() != null) {
                System.out.printf("  Error: %s%n", testCase.errorMessage());
            }
            System.out.println();
        }
    }
}
