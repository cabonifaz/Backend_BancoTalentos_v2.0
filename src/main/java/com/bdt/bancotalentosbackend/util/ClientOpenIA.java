package com.bdt.bancotalentosbackend.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientOpenIA {

  private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
  private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
  private static final String OPENAI_RESPONSES_URL = "https://api.openai.com/v1/responses";

  // HttpClient estático para reutilizar conexiones
  private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(20))
      .build();

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public ClientOpenIA() {
    if (OPENAI_API_KEY == null || OPENAI_API_KEY.isBlank()) {
      throw new IllegalStateException("La variable de entorno OPENAI_API_KEY no está configurada");
    }
  }

  public String sendPrompt(String prompt) throws IOException, InterruptedException {
    // Construir el cuerpo con Map en lugar de concatenación de strings
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("model", "gpt-4o-mini");
    requestMap.put("temperature", 0);

    Map<String, String> systemMessage = new HashMap<>();
    systemMessage.put("role", "system");
    systemMessage.put("content", "Eres un asistente que responde solo con JSON válido.");

    Map<String, String> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", prompt);

    requestMap.put("messages", List.of(systemMessage, userMessage));

    // Convertir a JSON en una sola operación
    String requestBody = OBJECT_MAPPER.writeValueAsString(requestMap);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(OPENAI_API_URL))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + OPENAI_API_KEY)
        .timeout(Duration.ofSeconds(180))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new IOException("Error en OpenAI API: " + response.statusCode() + " - " + response.body());
    }

    // Parsear JSON para obtener solo el texto
    JsonNode root = OBJECT_MAPPER.readTree(response.body());
    String text = root.path("choices").get(0).path("message").path("content").asText();

    return text;
  }

  public String sendPromptResponses(String prompt, String model) throws IOException, InterruptedException {
    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("model", model);
    requestMap.put("temperature", 0);
    requestMap.put("input", prompt);
    requestMap.put("instructions",
        "Eres un asistente especializado en análisis de currículums. Responde ÚNICAMENTE con JSON válido.");

    // Formato correcto para /v1/responses
    Map<String, Object> format = new HashMap<>();
    format.put("type", "json_object");

    Map<String, Object> text = new HashMap<>();
    text.put("format", format);

    requestMap.put("text", text);

    String requestBody = OBJECT_MAPPER.writeValueAsString(requestMap);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(OPENAI_RESPONSES_URL))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + OPENAI_API_KEY)
        .timeout(Duration.ofSeconds(180))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new IOException("Error en OpenAI API: " + response.statusCode() + " - " + response.body());
    }

    JsonNode root = OBJECT_MAPPER.readTree(response.body());
    JsonNode output = root.path("output");

    if (!output.isArray() || output.isEmpty()) {
      throw new IOException("Respuesta inesperada de OpenAI: " + response.body());
    }

    JsonNode content = output.get(0).path("content");

    if (!content.isArray() || content.isEmpty()) {
      throw new IOException("Contenido inválido en respuesta OpenAI: " + response.body());
    }

    return content.get(0).path("text").asText();
  }
}