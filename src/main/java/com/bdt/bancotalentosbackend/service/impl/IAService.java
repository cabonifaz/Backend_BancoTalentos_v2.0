package com.bdt.bancotalentosbackend.service.impl;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bdt.bancotalentosbackend.model.dto.ContactDTO;
import com.bdt.bancotalentosbackend.model.dto.SocialLinkDTO;
import com.bdt.bancotalentosbackend.model.request.AIPromptRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.GeneralResponse;
import com.bdt.bancotalentosbackend.model.response.IACVResponse;
import com.bdt.bancotalentosbackend.model.response.PromptResponse;
import com.bdt.bancotalentosbackend.model.response.SummarizeResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.util.ClientOpenIA;
import com.bdt.bancotalentosbackend.util.PromptBuilder;
import com.bdt.bancotalentosbackend.util.TextUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IAService {

  private final Logger logger = LoggerFactory.getLogger(IAService.class);
  private final ObjectMapper objectMapper;
  private final ClientOpenIA clientOpenIA;
  private final TalentsService talentsService;

  public BaseResponse prompt(AIPromptRequest request) throws IOException, InterruptedException {
    this.logger.info("Service IA started ");
    this.logger.info("Client was created");
    this.logger.info("Prompt generated");
    String response = this.clientOpenIA.sendPrompt(
        request.getPrompt(),
        "gpt-4.1-mini",
        "Response solo con un JSON válido");

    this.logger.info("Response received");

    // Mapear la respuesta JsonNode
    JsonNode promptResponse = objectMapper.readTree(response);
    this.logger.info("Response mapped");
    return new PromptResponse(2, "Respuesta exitosa", promptResponse);
  }

  /**
   * Analiza un CV en formato PDF, extrae el texto, lo limpia y lo envía a OpenAI
   * para obtener una estructura JSON con la información relevante.
   * 
   * @param cvFile
   * @return
   */
  public GeneralResponse<IACVResponse> analyzeCv(MultipartFile cvFile) {
    try {

      String extractedText = extractCvText(cvFile);

      long t2 = System.currentTimeMillis();
      this.logger.info("Starting OpenAI API call");
      String prompt = PromptBuilder.buildCVPrompt(extractedText);

      String structuredJson = this.clientOpenIA.sendPromptResponses(
          prompt,
          "gpt-4.1-mini");

      logger.info("OpenAI call: {}ms", System.currentTimeMillis() - t2);

      JsonNode structuredNode = objectMapper.readTree(structuredJson);
      IACVResponse iacvResponse = objectMapper.treeToValue(structuredNode,
          IACVResponse.class);

      return GeneralResponse.ok(iacvResponse);
    } catch (Exception e) {
      this.logger.error("Error procesando el CV", e);
      return GeneralResponse.error("Hubo un error al analizar el CV: " + e.getMessage());
    }
  }

  /**
   * Analizador de Diferencias.
   *
   * En lugar de extraer toda la información del CV, compara el nuevo CV contra la
   * información ya almacenada del talento (identificado por {@code idTalento}) y
   * devuelve ÚNICAMENTE la información nueva, adicional o mejorada, con la misma
   * estructura de {@link IACVResponse} para que el frontend reutilice la lógica de
   * confirmación existente.
   *
   * @param cvFile   nuevo CV en PDF.
   * @param idTalento identificador del talento a actualizar.
   * @param token    token JWT del usuario (necesario para recuperar el talento).
   * @return diferencias detectadas envueltas en un {@link GeneralResponse}.
   */
  public GeneralResponse<IACVResponse> analyzeCvDiff(MultipartFile cvFile, Integer idTalento, String token) {
    try {
      if (idTalento == null)
        throw new IllegalArgumentException("El idTalento es obligatorio");

      // 1. Extraer y limpiar el texto del nuevo CV (misma lógica que analyzeCv)
      String extractedText = extractCvText(cvFile);

      // 2. Recuperar la información completa del talento almacenada
      this.logger.info("Loading current talent info for id: {}", idTalento);
      TalentResponse currentTalent = this.talentsService.getTalentById(token, idTalento, true);
      if (currentTalent == null || currentTalent.getBaseResponse() == null
          || currentTalent.getBaseResponse().getIdMensaje() == null
          || currentTalent.getBaseResponse().getIdMensaje() != 2) {
        return GeneralResponse.error("No se pudo recuperar la información del talento");
      }

      // 3. Construir el objeto que representa el estado actual del talento
      IACVResponse currentSnapshot = buildCurrentTalentSnapshot(currentTalent);
      String currentTalentJson = objectMapper.writeValueAsString(currentSnapshot);

      // 4. Enviar AMBOS a la IA (CV nuevo + información actual) usando el prompt de diferencias
      long t2 = System.currentTimeMillis();
      this.logger.info("Starting OpenAI diff call");
      String prompt = PromptBuilder.buildCVDiffPrompt(extractedText, currentTalentJson);

      String structuredJson = this.clientOpenIA.sendPromptResponses(prompt, "gpt-4.1-mini");
      logger.info("OpenAI diff call: {}ms", System.currentTimeMillis() - t2);

      // 5. Parsear la respuesta (misma lógica de parseo que analyzeCv)
      JsonNode structuredNode = objectMapper.readTree(structuredJson);
      IACVResponse diffResponse = objectMapper.treeToValue(structuredNode, IACVResponse.class);

      return GeneralResponse.ok(diffResponse);
    } catch (Exception e) {
      this.logger.error("Error analizando diferencias del CV", e);
      return GeneralResponse.error("Hubo un error al analizar las diferencias del CV: " + e.getMessage());
    }
  }

  /**
   * Valida el archivo, extrae el texto del PDF y lo limpia.
   * Lógica compartida entre {@link #analyzeCv(MultipartFile)} y
   * {@link #analyzeCvDiff(MultipartFile, Integer, String)}.
   */
  private String extractCvText(MultipartFile cvFile) throws IOException {
    if (cvFile == null || cvFile.isEmpty())
      throw new IllegalArgumentException("El archivo está vacío");

    String contentType = cvFile.getContentType();
    if (contentType == null || !contentType.equalsIgnoreCase("application/pdf"))
      throw new IllegalArgumentException("El archivo debe ser un PDF");

    this.logger.info("Processing file: {}", cvFile.getOriginalFilename());

    String extractedText;
    int rawLength;
    long t0 = System.currentTimeMillis();

    try (PDDocument document = Loader.loadPDF(cvFile.getBytes())) {
      PDFTextStripper pdfStripper = new PDFTextStripper();
      pdfStripper.setSortByPosition(true);
      extractedText = pdfStripper.getText(document);
      rawLength = extractedText.length();
      this.logger.info("Text extracted successfully. Length: {}", rawLength);
    }
    logger.info("PDF extraction: {}ms", System.currentTimeMillis() - t0);

    long t1 = System.currentTimeMillis();
    this.logger.info("Starting text cleaning");
    extractedText = TextUtils.cleanCvText(extractedText);
    this.logger.info("Cleaned text length: {}", extractedText.length());
    this.logger.info("Difference in length after cleaning: {}", rawLength - extractedText.length());
    this.logger.info("Text cleaning: {}ms", System.currentTimeMillis() - t1);

    return extractedText;
  }

  /**
   * Construye una "foto" del estado actual del talento con la misma estructura de
   * {@link IACVResponse}, para enviarla a la IA como base de comparación.
   * Se conservan los ids de experiencias, educaciones e idiomas para que la IA
   * pueda referenciarlos al mejorar información existente en lugar de duplicarla.
   */
  private IACVResponse buildCurrentTalentSnapshot(TalentResponse t) {
    IACVResponse snapshot = new IACVResponse();
    snapshot.setNombres(t.getNombres());
    snapshot.setApellidoPaterno(t.getApellidos());
    snapshot.setDocIdentidad(t.getDni());
    snapshot.setPresentacion(t.getDescripcion());
    snapshot.setContacto(new ContactDTO(t.getCelular(), null, t.getEmail()));
    snapshot.setSocial(new SocialLinkDTO(t.getLinkedin(), t.getGithub()));
    snapshot.setTecSkills(t.getHabilidadesTecnicas());
    // Las habilidades blandas se excluyen a propósito del análisis de diferencias:
    // son subjetivas y suelen inferirse de texto descriptivo, generando ruido.
    snapshot.setSoftSkills(java.util.Collections.emptyList());
    snapshot.setWorkExps(t.getExperiencias());
    snapshot.setEdExps(t.getEducaciones());
    snapshot.setLangs(t.getIdiomas());
    return snapshot;
  }

  /**
   * Summarizes and improves the job function descriptions of a CV based on the
   * original text and additional user instructions.
   * 
   * @param activities
   * @param instructions
   * @return
   */
  public GeneralResponse<SummarizeResponse> summarizeActivities(String activities, String instructions) {
    try {
      if (activities == null || activities.trim().isEmpty()) {
        this.logger.warn("No se proporcionaron actividades para resumir");
        return GeneralResponse.error("El texto de funciones no puede estar vacío");
      }

      // Build the prompt with the original activities and user instructions
      this.logger.info("Starting summary process for activities");
      String prompt = PromptBuilder.buildSummaryPrompt(activities, instructions);

      // Call the OpenAI API to get the summary
      String aiResponse = this.clientOpenIA.sendPrompt(prompt, "gpt-4.1-mini",
          "Eres un asistente especializado en resumir y mejorar descripciones de funciones laborales. Responde ÚNICAMENTE con JSON válido que contenga un campo 'summary' con el resumen mejorado.");

      this.logger.info("AI response received for summary");

      // Parse the AI response to extract the summary from the JSON
      JsonNode rootNode = objectMapper.readTree(aiResponse);
      String resumen = rootNode.path("summary").asText();

      SummarizeResponse response = SummarizeResponse.builder()
          .summary(resumen)
          .build();

      return GeneralResponse.ok(response);

    } catch (Exception e) {
      this.logger.error("Error al resumir actividades con IA", e);
      return GeneralResponse.error("No se pudo generar el resumen: " + e.getMessage());
    }
  }
}
