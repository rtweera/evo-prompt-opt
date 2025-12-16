package com.evopromptopt.core.genome;

public record PromptGenome(
        String systemPrompt,
        String promptTemplate,
        InstructionStyle instructionStyle,
        ToolPolicy toolPolicy,
        double temperature,
        int maxTokens,
        double topP,
        int topK,
        double repeatPenalty,
        String responseFormat
) {
    public static PromptGenome defaultGenome() {
        return new PromptGenome(
                "You are a helpful AI assistant.",
                "{system_prompt}\n\nTask: {task}\n\nPlease provide your response:",
                InstructionStyle.DIRECT,
                ToolPolicy.NONE,
                0.7,
                512,
                0.9,
                40,
                1.1,
                "text"
        );
    }
}
