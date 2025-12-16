package com.evopromptopt.core.execution.ollama;

import com.evopromptopt.core.execution.ExecutionResult;
import com.evopromptopt.core.execution.PromptExecutor;
import com.evopromptopt.core.genome.PromptGenome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OllamaPromptExecutor implements PromptExecutor {
    private static final Logger logger = LoggerFactory.getLogger(OllamaPromptExecutor.class);

    private final OllamaClient ollamaClient;
    private final String modelName;

    public OllamaPromptExecutor(OllamaClient ollamaClient, String modelName) {
        this.ollamaClient = ollamaClient;
        this.modelName = modelName;
    }

    public OllamaPromptExecutor(String modelName) {
        this(new OllamaClient(), modelName);
    }

    @Override
    public ExecutionResult execute(PromptGenome genome, String task) {
        long startTime = System.currentTimeMillis();

        try {
            // Construct the prompt from template
            String prompt = genome.promptTemplate()
                    .replace("{system_prompt}", genome.systemPrompt())
                    .replace("{task}", task);

            // Create Ollama request with genome parameters
            var options = new OllamaRequest.Options(
                    genome.temperature(),
                    genome.maxTokens(),
                    genome.topP(),
                    genome.topK(),
                    genome.repeatPenalty()
            );

            var request = new OllamaRequest(modelName, prompt, genome.systemPrompt(), options);

            // Execute the request
            var response = ollamaClient.generate(request);
            long executionTime = System.currentTimeMillis() - startTime;

            return new ExecutionResult(
                    response.getResponse().trim(),
                    true,
                    null,
                    executionTime,
                    response.getEvalCount(),
                    response.getPromptEvalCount()
            );

        } catch (IOException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Failed to execute prompt with Ollama", e);

            return new ExecutionResult(
                    "",
                    false,
                    "Ollama execution failed: " + e.getMessage(),
                    executionTime,
                    0,
                    0
            );
        }
    }

    public boolean isAvailable() {
        return ollamaClient.isAvailable();
    }

    public void close() {
        ollamaClient.close();
    }
}
