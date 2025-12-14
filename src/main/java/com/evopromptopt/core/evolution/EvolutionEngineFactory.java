package com.evopromptopt.evolution;

import com.evopromptopt.core.fitness.PromptFitness;
import com.evopromptopt.core.genome.PromptGenotypeFactory;
import io.jenetics.*;
import io.jenetics.engine.*;

public final class EvolutionEngineFactory {

    private EvolutionEngineFactory() {}

    public static Engine<Gene<?, ?>, Double> create() {
        return Engine.builder(
                        PromptFitness::evaluate,
                        PromptGenotypeFactory::create
                )
                .populationSize(40)
                .optimize(Optimize.MAXIMUM)
                .alterers(
                        new Mutator<>(0.25),
                        new SinglePointCrossover<>(0.2)
                )
                .build();
    }
}
