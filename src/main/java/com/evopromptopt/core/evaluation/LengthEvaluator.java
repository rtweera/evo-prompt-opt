package com.evopromptopt.core.evaluation;

import com.evopromptopt.core.execution.ExecutionResult;

/**
 * Evaluates response quality based on length constraints and basic content quality.
 * Useful for summarization tasks or when specific length requirements exist.
 */
public class LengthEvaluator implements EvaluationMetric {
    private final int targetMinLength;
    private final int targetMaxLength;
    private final double lengthWeight;
    private final double contentWeight;

    public LengthEvaluator(int targetMinLength, int targetMaxLength, double lengthWeight, double contentWeight) {
        this.targetMinLength = targetMinLength;
        this.targetMaxLength = targetMaxLength;
        this.lengthWeight = lengthWeight;
        this.contentWeight = contentWeight;
    }

    public LengthEvaluator(int targetMinLength, int targetMaxLength) {
        this(targetMinLength, targetMaxLength, 0.4, 0.6); // Balanced weights
    }

    @Override
    public double evaluate(String task, String expectedOutput, String actualOutput, ExecutionResult executionResult) {
        if (!executionResult.success() || actualOutput == null || actualOutput.trim().isEmpty()) {
            return 0.0;
        }

        String output = actualOutput.trim();
        int length = output.length();

        // Calculate length score
        double lengthScore;
        if (length < targetMinLength) {
            lengthScore = (double) length / targetMinLength;
        } else if (length > targetMaxLength) {
            lengthScore = Math.max(0.0, 1.0 - ((length - targetMaxLength) / (double) targetMaxLength));
        } else {
            lengthScore = 1.0; // Perfect length
        }

        // Calculate basic content quality score
        double contentScore = evaluateContentQuality(output);

        return lengthWeight * lengthScore + contentWeight * contentScore;
    }

    private double evaluateContentQuality(String output) {
        // Basic content quality heuristics
        double score = 0.5; // Base score

        // Penalize very short sentences
        String[] sentences = output.split("[.!?]+");
        if (sentences.length == 0) {
            return 0.0;
        }

        // Reward proper sentence structure
        double avgSentenceLength = (double) output.length() / sentences.length;
        if (avgSentenceLength > 10 && avgSentenceLength < 100) {
            score += 0.2;
        }

        // Reward capitalization (indicates proper formatting)
        if (Character.isUpperCase(output.charAt(0))) {
            score += 0.1;
        }

        // Penalize repetitive content
        String[] words = output.toLowerCase().split("\\s+");
        long uniqueWords = java.util.Arrays.stream(words).distinct().count();
        double uniquenessRatio = (double) uniqueWords / words.length;
        if (uniquenessRatio > 0.7) {
            score += 0.2;
        } else if (uniquenessRatio < 0.3) {
            score -= 0.2;
        }

        return Math.max(0.0, Math.min(1.0, score));
    }

    @Override
    public String getName() {
        return "Length Quality";
    }

    @Override
    public String getDescription() {
        return "Evaluates response based on length constraints and basic content quality";
    }
}
