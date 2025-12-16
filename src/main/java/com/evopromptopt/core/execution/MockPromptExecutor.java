package com.evopromptopt.core.execution;

import com.evopromptopt.core.genome.PromptGenome;

/**
 * A mock executor for testing purposes that doesn't require Ollama
 */
public class MockPromptExecutor implements PromptExecutor {

    @Override
    public ExecutionResult execute(PromptGenome genome, String task) {
        // Simulate execution with deterministic results based on genome
        long startTime = System.currentTimeMillis();

        // Simulate some processing time
        try {
            Thread.sleep(50 + (int)(Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long executionTime = System.currentTimeMillis() - startTime;

        // Create a mock response based on the task
        String response = generateMockResponse(task, genome);

        // Simulate token counts
        int outputTokens = response.length() / 4; // Rough estimate
        int inputTokens = task.length() / 4;

        return new ExecutionResult(
                response,
                true,
                null,
                executionTime,
                outputTokens,
                inputTokens
        );
    }

    private String generateMockResponse(String task, PromptGenome genome) {
        // Generate deterministic responses based on task content
        if (task.toLowerCase().contains("what is") && task.contains("+")) {
            // Math problem
            return extractAndSolveMath(task);
        } else if (task.toLowerCase().contains("positive") || task.toLowerCase().contains("negative")) {
            // Sentiment classification
            if (task.toLowerCase().contains("love") || task.toLowerCase().contains("great")) {
                return "positive";
            } else if (task.toLowerCase().contains("terrible") || task.toLowerCase().contains("poor")) {
                return "negative";
            } else {
                return "neutral";
            }
        } else {
            // Generic response influenced by genome parameters
            String baseResponse = "This is a mock response for the task.";

            // Modify based on instruction style
            return switch (genome.instructionStyle()) {
                case CONCISE -> "Brief: " + baseResponse;
                case ANALYTICAL ->
                        "Analysis: " + baseResponse + " This requires careful consideration of multiple factors.";
                case STEP_BY_STEP -> "Step 1: Understanding the task. Step 2: " + baseResponse;
                default -> baseResponse;
            };
        }
    }

    private String extractAndSolveMath(String task) {
        // Simple math extraction and solving
        if (task.contains("15 + 27")) return "42";
        if (task.contains("8 * 9")) return "72";
        if (task.contains("144 / 12")) return "12";
        if (task.contains("25 - 13")) return "12";
        return "Unknown math problem";
    }
}
