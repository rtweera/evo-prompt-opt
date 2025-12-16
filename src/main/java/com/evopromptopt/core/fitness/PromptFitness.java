package com.evopromptopt.core.fitness;

import io.jenetics.Genotype;
import io.jenetics.EnumGene;

public final class PromptFitness {

    private PromptFitness() {}

    public static double evaluate(Genotype<EnumGene<String>> genotype) {
        /*
         * Current evaluation simulates:
         * - task success likelihood
         * - verbosity penalty
         * - temperature stability
         *
         * This will later be replaced with real model execution.
         */

        double baseScore = simulatedTaskScore(genotype);
        double costPenalty = simulatedCostPenalty(genotype);

        return baseScore - costPenalty;
    }

    private static double simulatedTaskScore(Genotype<EnumGene<String>> genotype) {
        return 0.6 + Math.random() * 0.4;
    }

    private static double simulatedCostPenalty(Genotype<EnumGene<String>> genotype) {
        return Math.random() * 0.2;
    }
}
