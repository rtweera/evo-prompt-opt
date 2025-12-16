package com.evopromptopt.core.execution.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OllamaRequest {
    @JsonProperty("model")
    private String model;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("system")
    private String system;

    @JsonProperty("stream")
    private boolean stream = false;

    @JsonProperty("options")
    private Options options;

    public OllamaRequest(String model, String prompt, String system, Options options) {
        this.model = model;
        this.prompt = prompt;
        this.system = system;
        this.options = options;
    }

    // Getters and setters
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }

    public String getSystem() { return system; }
    public void setSystem(String system) { this.system = system; }

    public boolean isStream() { return stream; }
    public void setStream(boolean stream) { this.stream = stream; }

    public Options getOptions() { return options; }
    public void setOptions(Options options) { this.options = options; }

    public static class Options {
        @JsonProperty("temperature")
        private double temperature;

        @JsonProperty("num_predict")
        private int maxTokens;

        @JsonProperty("top_p")
        private double topP;

        @JsonProperty("top_k")
        private int topK;

        @JsonProperty("repeat_penalty")
        private double repeatPenalty;

        public Options(double temperature, int maxTokens, double topP, int topK, double repeatPenalty) {
            this.temperature = temperature;
            this.maxTokens = maxTokens;
            this.topP = topP;
            this.topK = topK;
            this.repeatPenalty = repeatPenalty;
        }

        // Getters and setters
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public int getMaxTokens() { return maxTokens; }
        public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

        public double getTopP() { return topP; }
        public void setTopP(double topP) { this.topP = topP; }

        public int getTopK() { return topK; }
        public void setTopK(int topK) { this.topK = topK; }

        public double getRepeatPenalty() { return repeatPenalty; }
        public void setRepeatPenalty(double repeatPenalty) { this.repeatPenalty = repeatPenalty; }
    }
}
