package com.evopromptopt.core.genome;

import io.jenetics.*;

public final class PromptGenotypeFactory {

    // Define the indices for each parameter in the chromosome
    public static final int SYSTEM_PROMPT_INDEX = 0;
    public static final int PROMPT_TEMPLATE_INDEX = 1;
    public static final int INSTRUCTION_STYLE_INDEX = 2;
    public static final int TOOL_POLICY_INDEX = 3;
    public static final int TEMPERATURE_INDEX = 4;
    public static final int MAX_TOKENS_INDEX = 5;
    public static final int TOP_P_INDEX = 6;
    public static final int TOP_K_INDEX = 7;
    public static final int REPEAT_PENALTY_INDEX = 8;
    public static final int RESPONSE_FORMAT_INDEX = 9;

    // System prompts array
    private static final String[] SYSTEM_PROMPTS = {
        "You are a helpful AI assistant",
        "You are a precise and accurate assistant",
        "You are a careful reasoning assistant",
        "You are an expert domain assistant",
        "You are a concise and direct assistant",
        "You are a thorough and analytical assistant"
    };

    // Prompt templates array
    private static final String[] PROMPT_TEMPLATES = {
        "{system_prompt}\n\nTask: {task}\n\nResponse:",
        "{system_prompt}\n\n{task}\n\nPlease provide a detailed response:",
        "System: {system_prompt}\n\nUser: {task}\n\nAssistant:",
        "{system_prompt}\n\nInstruction: {task}\n\nOutput:",
        "Context: {system_prompt}\n\nQuery: {task}\n\nAnswer:"
    };

    // Response formats array
    private static final String[] RESPONSE_FORMATS = {
        "text", "json", "markdown"
    };

    private PromptGenotypeFactory() {}

    @SuppressWarnings("unchecked")
    public static Genotype<IntegerGene> create() {
        return Genotype.of(
                // System prompt index (0-5)
                IntegerChromosome.of(0, SYSTEM_PROMPTS.length - 1, 1),
                // Prompt template index (0-4)
                IntegerChromosome.of(0, PROMPT_TEMPLATES.length - 1, 1),
                // Instruction style index
                IntegerChromosome.of(0, InstructionStyle.values().length - 1, 1),
                // Tool policy index
                IntegerChromosome.of(0, ToolPolicy.values().length - 1, 1),
                // Temperature as integer (100 = 1.0, so 10-150 for 0.1-1.5)
                IntegerChromosome.of(10, 150, 1),
                // Max tokens (64 to 2048)
                IntegerChromosome.of(64, 2048, 1),
                // Top P as integer (10 = 0.1, so 10-100 for 0.1-1.0)
                IntegerChromosome.of(10, 100, 1),
                // Top K (1 to 100)
                IntegerChromosome.of(1, 100, 1),
                // Repeat penalty as integer (50 = 0.5, so 50-200 for 0.5-2.0)
                IntegerChromosome.of(50, 200, 1),
                // Response format index (0-2)
                IntegerChromosome.of(0, RESPONSE_FORMATS.length - 1, 1)
        );
    }

    public static PromptGenome decode(Genotype<IntegerGene> genotype) {
        var chromosomes = genotype;

        String systemPrompt = SYSTEM_PROMPTS[chromosomes.get(SYSTEM_PROMPT_INDEX).gene().intValue()];
        String promptTemplate = PROMPT_TEMPLATES[chromosomes.get(PROMPT_TEMPLATE_INDEX).gene().intValue()];
        InstructionStyle instructionStyle = InstructionStyle.values()[chromosomes.get(INSTRUCTION_STYLE_INDEX).gene().intValue()];
        ToolPolicy toolPolicy = ToolPolicy.values()[chromosomes.get(TOOL_POLICY_INDEX).gene().intValue()];
        double temperature = chromosomes.get(TEMPERATURE_INDEX).gene().intValue() / 100.0;
        int maxTokens = chromosomes.get(MAX_TOKENS_INDEX).gene().intValue();
        double topP = chromosomes.get(TOP_P_INDEX).gene().intValue() / 100.0;
        int topK = chromosomes.get(TOP_K_INDEX).gene().intValue();
        double repeatPenalty = chromosomes.get(REPEAT_PENALTY_INDEX).gene().intValue() / 100.0;
        String responseFormat = RESPONSE_FORMATS[chromosomes.get(RESPONSE_FORMAT_INDEX).gene().intValue()];

        return new PromptGenome(
                systemPrompt, promptTemplate, instructionStyle, toolPolicy,
                temperature, maxTokens, topP, topK, repeatPenalty, responseFormat
        );
    }

    // Helper methods to get the available options
    public static String[] getSystemPrompts() {
        return SYSTEM_PROMPTS.clone();
    }

    public static String[] getPromptTemplates() {
        return PROMPT_TEMPLATES.clone();
    }

    public static String[] getResponseFormats() {
        return RESPONSE_FORMATS.clone();
    }
}
