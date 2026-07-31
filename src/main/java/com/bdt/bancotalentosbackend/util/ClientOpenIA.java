package com.bdt.bancotalentosbackend.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ClientOpenIA {

  private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
  private static final String OPENAI_RESPONSES_URL = "https://api.openai.com/v1/responses";

  // HttpClient estático para reutilizar conexiones
  private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(20))
      .build();

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public String sendPrompt(String prompt, String model, String instructions)
      throws IOException, InterruptedException {
    return sendPrompt(prompt, model, instructions, null);
  }

  /**
   * Envía un prompt a OpenAI (Responses API). Si se provee {@code schema}, la salida
   * se fuerza con structured outputs estrictos (json_schema), garantizando la forma
   * exacta del JSON; en caso contrario se usa json_object (JSON válido genérico).
   */
  public String sendPrompt(String prompt, String model, String instructions, JsonNode schema)
      throws IOException, InterruptedException {

    Map<String, Object> requestMap = new HashMap<>();
    requestMap.put("model", model);
    requestMap.put("temperature", 0);
    requestMap.put("input", prompt);
    requestMap.put("instructions", instructions);

    // Forzar respuesta JSON: schema estricto si se proporciona, si no json_object.
    Map<String, Object> format = new HashMap<>();
    if (schema != null && !schema.isNull() && !schema.isMissingNode()) {
      format.put("type", "json_schema");
      format.put("name", "structured_response");
      format.put("strict", true);
      format.put("schema", schema);
    } else {
      format.put("type", "json_object");
    }
    Map<String, Object> textFormat = new HashMap<>();
    textFormat.put("format", format);
    requestMap.put("text", textFormat);

    String requestBody = OBJECT_MAPPER.writeValueAsString(requestMap);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(OPENAI_RESPONSES_URL))
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + OPENAI_API_KEY)
        .timeout(Duration.ofSeconds(180))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    HttpResponse<String> response = HTTP_CLIENT.send(request,
        HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() != 200) {
      throw new IOException("Error en OpenAI API: " +
          response.statusCode() + " - " + response.body());
    }

    // Parseo con formato /v1/responses (igual que sendPromptResponses)
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