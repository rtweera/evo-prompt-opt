package com.evopromptopt.app;

import com.evopromptopt.evolution.EvolutionEngineFactory;
import io.jenetics.engine.EvolutionResult;

public class EvoPromptRunner {

    public static void main(String[] args) {
        var engine = EvolutionEngineFactory.create();

        EvolutionResult<?, Double> result = engine.stream()
                .limit(25)
                .collect(EvolutionResult.toBestEvolutionResult());

        System.out.println("Best fitness: " + result.bestFitness());
        System.out.println("Best prompt configuration:");
        System.out.println(result.bestPhenotype().genotype());
    }
}
