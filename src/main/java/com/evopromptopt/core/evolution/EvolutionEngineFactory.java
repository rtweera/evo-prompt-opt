package com.evopromptopt.core.evolution;

import com.evopromptopt.core.fitness.PromptFitness;
import com.evopromptopt.core.genome.PromptGenotypeFactory;
import com.evopromptopt.core.tasks.TaskDefinition;
import com.evopromptopt.core.tasks.TaskRunner;
import io.jenetics.*;
import io.jenetics.engine.*;

public final class EvolutionEngineFactory {

    private EvolutionEngineFactory() {}

    /**
     * Creates an evolution engine configured for prompt optimization
     */
    public static Engine<IntegerGene, Double> create(TaskRunner taskRunner, TaskDefinition taskDefinition) {
        return create(taskRunner, taskDefinition, 50, 100); // Default population and generations
    }

    /**
     * Creates an evolution engine with custom population size and generation limit
     */
    public static Engine<IntegerGene, Double> create(TaskRunner taskRunner, TaskDefinition taskDefinition,
                                                   int populationSize, int maxGenerations) {

        PromptFitness fitnessFunction = new PromptFitness(taskRunner, taskDefinition);

        return Engine.builder(
                        fitnessFunction::evaluate,
                        PromptGenotypeFactory::create
                )
                .populationSize(populationSize)
                .optimize(Optimize.MAXIMUM) // We want to maximize fitness
                .alterers(
                        new Mutator<>(0.15),           // Standard mutation
                        new SinglePointCrossover<>(0.65) // Standard crossover
                )
                .selector(new TournamentSelector<>(3))    // Tournament selection with tournament size 3
                .survivorsSelector(new EliteSelector<>()) // Elite selection for survivors
                .build();
    }

    /**
     * Creates an evolution engine with advanced configuration options
     */
    public static Engine<IntegerGene, Double> createAdvanced(TaskRunner taskRunner, TaskDefinition taskDefinition,
                                                           EvolutionConfig config) {
        PromptFitness fitnessFunction = new PromptFitness(
                taskRunner, taskDefinition,
                config.executionTimeWeight(), config.successRateWeight(), config.scoreWeight()
        );

        return Engine.builder(
                        fitnessFunction::evaluate,
                        PromptGenotypeFactory::create
                )
                .populationSize(config.populationSize())
                .optimize(Optimize.MAXIMUM)
                .alterers(
                        new Mutator<>(config.mutationRate()),
                        new SinglePointCrossover<>(config.crossoverRate())
                )
                .selector(new TournamentSelector<>(config.tournamentSize()))
                .survivorsSelector(config.eliteRatio() > 0 ?
                    new EliteSelector<>() : new TournamentSelector<>())
                .build();
    }

    /**
     * Configuration record for advanced evolution engine setup
     */
    public record EvolutionConfig(
            int populationSize,
            double mutationRate,
            double crossoverRate,
            int tournamentSize,
            double eliteRatio,
            double executionTimeWeight,
            double successRateWeight,
            double scoreWeight
    ) {
        public static EvolutionConfig defaultConfig() {
            return new EvolutionConfig(
                    50,    // populationSize
                    0.15,  // mutationRate
                    0.65,  // crossoverRate
                    3,     // tournamentSize
                    0.1,   // eliteRatio
                    0.1,   // executionTimeWeight
                    0.3,   // successRateWeight
                    0.6    // scoreWeight
            );
        }
    }
}
