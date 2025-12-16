package com.evopromptopt.core.genome;

import io.jenetics.*;

public final class PromptGenotypeFactory {

    private PromptGenotypeFactory() {}

    public static Genotype<EnumGene<String>> create() {
        var chromosome = PermutationChromosome.of(
                "You are a precise assistant",
                "You are a careful reasoning assistant",
                "You are an expert domain assistant"
        );
        return Genotype.of(chromosome);
    }
}
