package com.evopromptopt.app;

import com.evopromptopt.core.evolution.EvolutionEngineFactory;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.EnumGene;

public class EvoPromptRunner {

    public static void main(String[] args) {
        var engine = EvolutionEngineFactory.create();

        EvolutionResult<EnumGene<String>, Double> result = engine.stream()
                .limit(25)
                .collect(EvolutionResult.toBestEvolutionResult());

        System.out.println("Best fitness: " + result.bestFitness());
        System.out.println("Best prompt configuration:");
        System.out.println(result.bestPhenotype().genotype());
    }
}
