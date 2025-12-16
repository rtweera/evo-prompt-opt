package com.evopromptopt.core.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.evopromptopt.core.evaluation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Loads task definitions from JSON configuration files
 */
public class TaskLoader {
    private static final Logger logger = LoggerFactory.getLogger(TaskLoader.class);
    private final ObjectMapper objectMapper;

    public TaskLoader() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Load a task definition from a JSON file
     */
    public TaskDefinition loadFromResource(String resourcePath) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            JsonNode rootNode = objectMapper.readTree(inputStream);
            return parseTaskDefinition(rootNode);
        }
    }

    /**
     * Load a task definition from a JSON string
     */
    public TaskDefinition loadFromJson(String json) throws IOException {
        JsonNode rootNode = objectMapper.readTree(json);
        return parseTaskDefinition(rootNode);
    }

    private TaskDefinition parseTaskDefinition(JsonNode rootNode) {
        String name = rootNode.get("name").asText();
        String description = rootNode.get("description").asText();

        // Parse test cases
        List<TaskDefinition.TestCase> testCases = new ArrayList<>();
        if (rootNode.has("testCases")) {
            for (JsonNode testCaseNode : rootNode.get("testCases")) {
                String input = testCaseNode.get("input").asText();
                String expectedOutput = testCaseNode.has("expectedOutput") ?
                    testCaseNode.get("expectedOutput").asText() : "";

                Map<String, Object> metadata = new HashMap<>();
                if (testCaseNode.has("metadata")) {
                    JsonNode metadataNode = testCaseNode.get("metadata");
                    metadataNode.fieldNames().forEachRemaining(fieldName ->
                        metadata.put(fieldName, metadataNode.get(fieldName).asText()));
                }

                testCases.add(new TaskDefinition.TestCase(input, expectedOutput, metadata));
            }
        }

        // Parse evaluation metrics
        List<EvaluationMetric> evaluationMetrics = new ArrayList<>();
        if (rootNode.has("evaluation")) {
            JsonNode evalNode = rootNode.get("evaluation");

            if (evalNode.has("metrics")) {
                for (JsonNode metricNode : evalNode.get("metrics")) {
                    EvaluationMetric metric = parseEvaluationMetric(metricNode);
                    if (metric != null) {
                        evaluationMetrics.add(metric);
                    }
                }
            } else {
                // Default evaluation based on type
                String evalType = evalNode.has("type") ? evalNode.get("type").asText() : "accuracy";
                EvaluationMetric defaultMetric = createDefaultMetric(evalType, evalNode);
                if (defaultMetric != null) {
                    evaluationMetrics.add(defaultMetric);
                }
            }
        }

        // Add default accuracy evaluator if none specified
        if (evaluationMetrics.isEmpty()) {
            evaluationMetrics.add(new AccuracyEvaluator());
        }

        // Parse configuration
        Map<String, Object> configuration = new HashMap<>();
        if (rootNode.has("configuration")) {
            JsonNode configNode = rootNode.get("configuration");
            configNode.fieldNames().forEachRemaining(fieldName -> {
                JsonNode valueNode = configNode.get(fieldName);
                if (valueNode.isTextual()) {
                    configuration.put(fieldName, valueNode.asText());
                } else if (valueNode.isNumber()) {
                    configuration.put(fieldName, valueNode.asDouble());
                } else if (valueNode.isBoolean()) {
                    configuration.put(fieldName, valueNode.asBoolean());
                }
            });
        }

        return new TaskDefinition(name, description, testCases, evaluationMetrics, configuration);
    }

    private EvaluationMetric parseEvaluationMetric(JsonNode metricNode) {
        String type = metricNode.get("type").asText();

        switch (type.toLowerCase()) {
            case "accuracy":
                boolean caseSensitive = metricNode.has("caseSensitive") &&
                                       metricNode.get("caseSensitive").asBoolean();
                boolean trimWhitespace = !metricNode.has("trimWhitespace") ||
                                        metricNode.get("trimWhitespace").asBoolean();
                return new AccuracyEvaluator(caseSensitive, trimWhitespace);

            case "length":
                int minLength = metricNode.has("minLength") ?
                               metricNode.get("minLength").asInt() : 50;
                int maxLength = metricNode.has("maxLength") ?
                               metricNode.get("maxLength").asInt() : 200;
                return new LengthEvaluator(minLength, maxLength);

            case "content":
                Set<String> requiredKeywords = new HashSet<>();
                Set<String> bonusKeywords = new HashSet<>();

                if (metricNode.has("requiredKeywords")) {
                    metricNode.get("requiredKeywords").forEach(node ->
                        requiredKeywords.add(node.asText()));
                }

                if (metricNode.has("bonusKeywords")) {
                    metricNode.get("bonusKeywords").forEach(node ->
                        bonusKeywords.add(node.asText()));
                }

                return new ContentQualityEvaluator(requiredKeywords, bonusKeywords, 0.4, 0.3, 0.3);

            default:
                logger.warn("Unknown evaluation metric type: {}", type);
                return null;
        }
    }

    private EvaluationMetric createDefaultMetric(String type, JsonNode evalNode) {
        return switch (type.toLowerCase()) {
            case "accuracy" -> new AccuracyEvaluator();
            case "length" -> {
                int minLength = evalNode.has("minLength") ? evalNode.get("minLength").asInt() : 50;
                int maxLength = evalNode.has("maxLength") ? evalNode.get("maxLength").asInt() : 200;
                yield new LengthEvaluator(minLength, maxLength);
            }
            case "content" -> new ContentQualityEvaluator(Set.of());
            default -> new AccuracyEvaluator();
        };
    }
}
