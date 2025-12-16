package com.evopromptopt.core.execution.ollama;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class OllamaClient {
    private static final Logger logger = LoggerFactory.getLogger(OllamaClient.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String baseUrl;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OllamaClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.objectMapper = new ObjectMapper();

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)  // Allow up to 3 minutes for model response
                .build();
    }

    public OllamaClient() {
        this("http://localhost:11434");
    }

    public OllamaResponse generate(OllamaRequest request) throws IOException {
        String jsonRequest = objectMapper.writeValueAsString(request);
        logger.debug("Sending request to Ollama: {}", jsonRequest);

        RequestBody body = RequestBody.create(jsonRequest, JSON);
        Request httpRequest = new Request.Builder()
                .url(baseUrl + "api/generate")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                throw new IOException("Ollama request failed with code " + response.code() + ": " + errorBody);
            }

            String responseBody = response.body().string();
            logger.debug("Received response from Ollama: {}", responseBody);

            return objectMapper.readValue(responseBody, OllamaResponse.class);
        }
    }

    public boolean isAvailable() {
        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "api/tags")
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (Exception e) {
            logger.warn("Ollama availability check failed", e);
            return false;
        }
    }

    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }
}
