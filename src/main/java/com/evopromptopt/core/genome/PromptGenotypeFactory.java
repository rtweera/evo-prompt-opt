package com.evopromptopt.core.genome;

import io.jenetics.*;

public final class PromptGenotypeFactory {

    private PromptGenotypeFactory() {}

    public static Genotype<Gene<?, ?>> create() {
        return Genotype.of(
                EnumGene.of(
                        "You are a precise assistant",
                        "You are a careful reasoning assistant",
                        "You are an expert domain assistant"
                ),
                EnumGene.of(InstructionStyle.values()),
                EnumGene.of(ToolPolicy.values()),
                DoubleGene.of(0.1, 1.0)
        );
    }
}
