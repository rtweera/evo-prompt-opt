package com.evopromptopt.core.genome;

public record PromptGenome(
        String systemRole,
        InstructionStyle instructionStyle,
        ToolPolicy toolPolicy,
        double temperature
) {}
