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

  public String buildCVPrompt(String extractedText) {
    return """
        Eres un asistente especializado en análisis de currículums (CV).
        Tu tarea es extraer la información relevante del siguiente texto extraído de un PDF.
        El texto puede provenir de OCR, por lo que algunas palabras pueden estar incompletas o tener errores.
        Debes reconstruir y corregir lo mínimo necesario para dar coherencia a la información,
        pero sin inventar datos que no estén presentes.

        Responde ÚNICAMENTE en formato JSON con la siguiente estructura exacta
        correspondiente al modelo `IACVResponse` en Java.
        Si no encuentras algún dato, usa `null`.
        No omitas ninguna propiedad.

        # Reglas para la extracción de experiencia laboral:

        Debes retornar las experiencias laborales en orden: Las más recientes primero (Si es actual primero, leugo ordena por fecha fin y finalmente por fecha inicio).

        Para las funciones laborales, trata de no resumirlas, sino de mantener la mayor similitud posible con el texto original, corrigiendo solo errores evidentes de ortografía y gramática. Puedes resumir apartir de la 10ma experiencia laboral, pero siempre manteniendo la esencia de las funciones descritas.

        Para las funciones laborales, incluye TANTO el párrafo descriptivo inicial
        COMO todos los puntos o bullets que aparezcan bajo la experiencia.
        No omitas ningún bullet point. Concaténalos en un solo string separados por salto de línea (\n).

        No elimines las habilidades técnicas que se mencionen en las funcionaes laborales.

        ### Formato de salida esperado (ejemplo con datos ficticios):

        {
          "result": {
            "code": 0,
            "message": "OK"
          },
          "nombres": "Juan Carlos",
          "apellidoPaterno": "Pérez",
          "apellidoMaterno": "García",
          "docIdentidad": "12345678",
          "contacto": {
            "celularNum": "987654321",
            "celularCod": "51",
            "email": "juan.perez@email.com"
          },
          "location": {
            "pais": "Perú",
            "ciudad": "Lima"
          },
          "tecSkills": [
            { "nombreHabilidad": "JAVA", "aniosExperiencia": 5 },
            { "nombreHabilidad": "SPRING BOOT", "aniosExperiencia": 3 }
          ],
          "social": {
            "linkedin": "https://linkedin.com/in/juanperez",
            "github": "https://github.com/juanperez"
          },
          "presentacion": "Profesional con experiencia en desarrollo backend...",
          "softSkills": [
            { "nombreHabilidad": "Trabajo en equipo" },
            { "nombreHabilidad": "Comunicación efectiva" }
          ],
          "workExps": [
            {
              "idExperiencia": null,
              "nombreEmpresa": "TechCorp",
              "puesto": "Desarrollador Backend",
              "funciones": "Desarrollo de microservicios y mantenimiento de APIs.",
              "fechaInicio": "2020-01-15",
              "fechaFin": null,
              "flActualidad": 1
            }
          ],
          "edExps": [
            {
              "idEducacion": null,
              "nombreInstitucion": "Universidad Nacional",
              "carrera": "Ingeniería de Sistemas",
              "grado": "1",
              "fechaInicio": "2015-03-01",
              "fechaFin": "2019-12-15",
              "flActualidad": 0
            }
          ],
          "langs": [
            {
              "idTalentoIdioma": null,
              "idIdioma": null,
              "nombreIdioma": "Inglés",
              "idNivel": 3,
              "nivelIdioma": "AVANZADO",
              "estrellas": 3
            },
            {
              "idTalentoIdioma": null,
              "idIdioma": null,
              "nombreIdioma": "Español",
              "idNivel": 4,
              "nivelIdioma": "NATIVO",
              "estrellas": 4
            }
          ]
        }

        ### Reglas adicionales:
        - Los campos `idExperiencia`, `idEducacion`, `idTalentoIdioma`, `idIdioma` deben ser siempre `null`.
        - `fechaInicio` y `fechaFin` deben estar en formato `yyyy-MM-dd`.
          Si `flActualidad = 1`, entonces `fechaFin = null`.
        - Para idiomas:
          - BASICO = estrellas 1
          - INTERMEDIO = estrellas 2
          - AVANZADO = estrellas 3
          - NATIVO = estrellas 4
        - En langs.idNivel retorna el número 1, 2, 3, o 4 para BASICO, INTERMEDIO, AVANZADO y NATIVO respectivamente
        - En langs.idIdioma retorna el número 1, 2, 3, 4, 5 para ESPAÑOL, INGLES FRANCES, ALEMAN, CHINO respectivamente
        - Para educación: devolver únicamente `grado` como string un número(ej. Bachiller es 1, Título es 2, Curso es 3, Técnico es 4, Egresado es 5, Estudiante es 6).
          Si no es posible identificarlo, poner `null`.
        - El campo `presentacion` debe ser exactamente como aparece en el CV, sin traducir ni reescribir.
        - Si algún dato no puede determinarse, asignar `null`.
        - No devuelvas texto adicional fuera del JSON.
        - En el codigo del celular no debes incluir +
          correcto: 51, incorrecto: +51
          ---

        ### Texto del CV a procesar:
        """
        + extractedText;
  }

}