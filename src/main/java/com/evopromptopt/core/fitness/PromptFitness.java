package com.evopromptopt.core.fitness;

import io.jenetics.Genotype;
import io.jenetics.IntegerGene;
import com.evopromptopt.core.genome.PromptGenome;
import com.evopromptopt.core.genome.PromptGenotypeFactory;
import com.evopromptopt.core.tasks.TaskDefinition;
import com.evopromptopt.core.tasks.TaskRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fitness function that evaluates prompt genomes using actual task execution and evaluation metrics
 */
public final class PromptFitness {
    private static final Logger logger = LoggerFactory.getLogger(PromptFitness.class);

    private final TaskRunner taskRunner;
    private final TaskDefinition taskDefinition;
    private final double executionTimeWeight;
    private final double successRateWeight;
    private final double scoreWeight;

    private PromptFitness() {
        // Private constructor for static methods
        this.taskRunner = null;
        this.taskDefinition = null;
        this.executionTimeWeight = 0.1;
        this.successRateWeight = 0.3;
        this.scoreWeight = 0.6;
    }

    public PromptFitness(TaskRunner taskRunner, TaskDefinition taskDefinition) {
        this(taskRunner, taskDefinition, 0.1, 0.3, 0.6);
    }

    public PromptFitness(TaskRunner taskRunner, TaskDefinition taskDefinition,
                        double executionTimeWeight, double successRateWeight, double scoreWeight) {
        this.taskRunner = taskRunner;
        this.taskDefinition = taskDefinition;
        this.executionTimeWeight = executionTimeWeight;
        this.successRateWeight = successRateWeight;
        this.scoreWeight = scoreWeight;
    }

    /**
     * Evaluates a genotype by decoding it to a PromptGenome and running task evaluation
     */
    public double evaluate(Genotype<IntegerGene> genotype) {
        try {
            // Decode genotype to PromptGenome
            PromptGenome genome = PromptGenotypeFactory.decode(genotype);

            // Run task evaluation
            var result = taskRunner.evaluateGenome(genome, taskDefinition);

            // Calculate composite fitness score
            double fitnessScore = calculateFitness(result);

            logger.debug("Genome fitness: {} (score: {}, success: {}, time: {}ms)",
                        fitnessScore, result.overallScore(), result.successRate(), result.totalExecutionTimeMs());

            return fitnessScore;

        } catch (Exception e) {
            logger.error("Failed to evaluate genome fitness", e);
            return 0.0; // Return minimum fitness for failed evaluations
        }
    }

    private double calculateFitness(TaskRunner.TaskEvaluationResult result) {
        // Normalize execution time (assume max reasonable time is 10 seconds per test case)
        double maxReasonableTime = taskDefinition.getTestCases().size() * 10000.0; // 10 seconds per test case
        double timeScore = Math.max(0.0, 1.0 - (result.totalExecutionTimeMs() / maxReasonableTime));

        // Combine different aspects of performance
        double fitness = scoreWeight * result.overallScore() +
                        successRateWeight * result.successRate() +
                        executionTimeWeight * timeScore;

        return Math.max(0.0, Math.min(1.0, fitness)); // Clamp to [0, 1]
    }

}
