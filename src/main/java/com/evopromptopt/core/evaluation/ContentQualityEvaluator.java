package com.evopromptopt.core.evaluation;

import com.evopromptopt.core.execution.ExecutionResult;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Evaluates content quality using keyword matching and response structure.
 * Useful when you have key concepts or keywords that should appear in good responses.
 */
public class ContentQualityEvaluator implements EvaluationMetric {
    private final Set<String> requiredKeywords;
    private final Set<String> bonusKeywords;
    private final double keywordWeight;
    private final double structureWeight;
    private final double coherenceWeight;

    public ContentQualityEvaluator(Set<String> requiredKeywords, Set<String> bonusKeywords,
                                   double keywordWeight, double structureWeight, double coherenceWeight) {
        this.requiredKeywords = new HashSet<>();
        this.bonusKeywords = new HashSet<>();

        // Convert to lowercase for case-insensitive matching
        requiredKeywords.forEach(k -> this.requiredKeywords.add(k.toLowerCase()));
        bonusKeywords.forEach(k -> this.bonusKeywords.add(k.toLowerCase()));

        this.keywordWeight = keywordWeight;
        this.structureWeight = structureWeight;
        this.coherenceWeight = coherenceWeight;
    }

    public ContentQualityEvaluator(Set<String> requiredKeywords) {
        this(requiredKeywords, new HashSet<>(), 0.4, 0.3, 0.3);
    }

    @Override
    public double evaluate(String task, String expectedOutput, String actualOutput, ExecutionResult executionResult) {
        if (!executionResult.success() || actualOutput == null || actualOutput.trim().isEmpty()) {
            return 0.0;
        }

        String output = actualOutput.toLowerCase();

        // Calculate keyword score
        double keywordScore = calculateKeywordScore(output);

        // Calculate structure score
        double structureScore = calculateStructureScore(actualOutput);

        // Calculate coherence score
        double coherenceScore = calculateCoherenceScore(actualOutput);

        return keywordWeight * keywordScore +
               structureWeight * structureScore +
               coherenceWeight * coherenceScore;
    }

    private double calculateKeywordScore(String output) {
        if (requiredKeywords.isEmpty() && bonusKeywords.isEmpty()) {
            return 1.0; // No keywords to check
        }

        double score = 0.0;

        // Check required keywords
        if (!requiredKeywords.isEmpty()) {
            long foundRequired = requiredKeywords.stream()
                    .mapToLong(keyword -> output.contains(keyword) ? 1 : 0)
                    .sum();
            score += 0.7 * (foundRequired / (double) requiredKeywords.size());
        } else {
            score += 0.7; // No required keywords, give full credit
        }

        // Check bonus keywords
        if (!bonusKeywords.isEmpty()) {
            long foundBonus = bonusKeywords.stream()
                    .mapToLong(keyword -> output.contains(keyword) ? 1 : 0)
                    .sum();
            score += 0.3 * Math.min(1.0, foundBonus / (double) bonusKeywords.size());
        } else {
            score += 0.3; // No bonus keywords, give full credit
        }

        return Math.min(1.0, score);
    }

    private double calculateStructureScore(String output) {
        double score = 0.0;

        // Check for proper sentence structure
        String[] sentences = output.split("[.!?]+");
        if (sentences.length > 0) {
            score += 0.3;

            // Reward multiple sentences (better structure)
            if (sentences.length > 1) {
                score += 0.2;
            }

            // Check average sentence length
            double avgLength = Arrays.stream(sentences)
                    .mapToInt(s -> s.trim().length())
                    .average()
                    .orElse(0.0);

            if (avgLength > 10 && avgLength < 150) {
                score += 0.3;
            }
        }

        // Check for proper capitalization
        if (!output.trim().isEmpty() && Character.isUpperCase(output.trim().charAt(0))) {
            score += 0.2;
        }

        return Math.min(1.0, score);
    }

    private double calculateCoherenceScore(String output) {
        // Basic coherence metrics
        double score = 0.5; // Base score

        String[] words = output.toLowerCase().split("\\s+");
        if (words.length == 0) {
            return 0.0;
        }

        // Measure word diversity (avoid repetition)
        Set<String> uniqueWords = new HashSet<>(Arrays.asList(words));
        double diversity = (double) uniqueWords.size() / words.length;

        if (diversity > 0.6) {
            score += 0.3;
        } else if (diversity < 0.3) {
            score -= 0.2;
        }

        // Check for connecting words (indicates better flow)
        Set<String> connectors = Set.of("and", "but", "however", "therefore", "because",
                                       "since", "although", "while", "whereas", "moreover");
        long connectorCount = Arrays.stream(words)
                .mapToLong(word -> connectors.contains(word) ? 1 : 0)
                .sum();

        if (connectorCount > 0) {
            score += Math.min(0.2, connectorCount * 0.05);
        }

        return Math.max(0.0, Math.min(1.0, score));
    }

    @Override
    public String getName() {
        return "Content Quality";
    }

    @Override
    public String getDescription() {
        return "Evaluates content based on keyword matching, structure, and coherence";
    }
}
