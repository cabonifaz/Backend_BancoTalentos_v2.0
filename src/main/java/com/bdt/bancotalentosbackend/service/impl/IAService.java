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
import com.bdt.bancotalentosbackend.model.response.FMIExtractionDTO;
import com.bdt.bancotalentosbackend.model.response.GeneralResponse;
import com.bdt.bancotalentosbackend.model.response.IACVQuickResponse;
import com.bdt.bancotalentosbackend.model.response.IACVResponse;
import com.bdt.bancotalentosbackend.model.response.PromptResponse;
import com.bdt.bancotalentosbackend.model.response.SummarizeResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.util.ClientOpenIA;
import com.bdt.bancotalentosbackend.util.FmiTextParser;
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

    // Instrucciones de sistema del request (o default), y schema opcional para
    // structured outputs estrictos.
    String instructions = (request.getInstructions() != null && !request.getInstructions().isBlank())
        ? request.getInstructions()
        : "Responde únicamente con un JSON válido";

    String response = this.clientOpenIA.sendPrompt(
        request.getPrompt(),
        "gpt-4.1-mini",
        instructions,
        request.getSchema());

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
   * CARGA RÁPIDA: del CV sólo salen nombres, apellidos, celular y correo.
   *
   * Comparte con {@link #analyzeCv(MultipartFile)} la extracción del PDF, pero
   * usa un prompt mínimo ({@link PromptBuilder#buildQuickCVPrompt(String)}): la
   * llamada es corta, y eso es lo que permite dar de alta un talento en
   * segundos en vez de esperar el análisis completo del CV.
   *
   * @param cvFile CV en PDF.
   * @return identidad y contacto detectados; los campos que el CV no tenga vienen
   *         en null, para que el frontend los pida a mano.
   */
  public GeneralResponse<IACVQuickResponse> analyzeCvQuick(MultipartFile cvFile) {
    try {
      String extractedText = extractCvText(cvFile);

      long t2 = System.currentTimeMillis();
      this.logger.info("Starting OpenAI API call (quick)");
      String prompt = PromptBuilder.buildQuickCVPrompt(extractedText);

      String structuredJson = this.clientOpenIA.sendPromptResponses(
          prompt,
          "gpt-4.1-mini");

      logger.info("OpenAI call (quick): {}ms", System.currentTimeMillis() - t2);

      JsonNode structuredNode = objectMapper.readTree(structuredJson);
      IACVQuickResponse response = objectMapper.treeToValue(structuredNode,
          IACVQuickResponse.class);

      return GeneralResponse.ok(response);
    } catch (Exception e) {
      this.logger.error("Error procesando el CV (carga rápida)", e);
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
   * Lee un FMI (FT-GTH-12, Formulario de Ingreso) para la carga de
   * colaboradores desde AutFMI.
   *
   * Primero lo intenta el parser por etiquetas, que acierta con los formularios
   * que genera el propio sistema y no gasta ni una llamada a OpenAI. La IA sólo
   * entra cuando el parser se queda corto, que es el caso de los FMI redactados
   * fuera con el mismo formato.
   *
   * @param fmiFile formulario en PDF.
   * @return lo leído del formulario; si el PDF no es un FMI de ingreso, viene
   *         con `esFormularioIngreso` en false y el motivo, no como error.
   */
  public GeneralResponse<FMIExtractionDTO> analyzeFmi(MultipartFile fmiFile) {
    try {
      // Sin limpiar: el parser necesita las líneas tal como las ordenó PDFBox.
      String extractedText = extractPdfText(fmiFile, false);

      if (extractedText == null || extractedText.isBlank()) {
        return GeneralResponse.ok(descartado(
            "No se pudo extraer texto del PDF. Si es un documento escaneado, no se puede leer."));
      }

      if (!FmiTextParser.pareceFormulario(extractedText)) {
        return GeneralResponse.ok(descartado(
            "El PDF no corresponde al formato FT-GTH-12 (Formulario de Movimiento)."));
      }

      FMIExtractionDTO extraccion = FmiTextParser.parse(extractedText,
          fmiFile.getOriginalFilename());

      // El parser ya decidió que no es un ingreso: no hay nada que la IA pueda
      // aportar, y preguntarle costaría una llamada para el mismo "no".
      if (!extraccion.isEsFormularioIngreso())
        return GeneralResponse.ok(extraccion);

      if (parserSuficiente(extraccion))
        return GeneralResponse.ok(extraccion);

      this.logger.info("Parser incompleto, se consulta a la IA");
      completarConIa(extraccion, TextUtils.cleanCvText(extractedText));

      return GeneralResponse.ok(extraccion);
    } catch (Exception e) {
      this.logger.error("Error procesando el FMI", e);
      return GeneralResponse.error("Hubo un error al leer el formulario: " + e.getMessage());
    }
  }

  /**
   * Con el nombre, el monto base y tres campos más, el formulario ya es
   * utilizable.
   *
   * El monto base se exige aparte porque es el que peor se lee: no tiene rótulo
   * propio en la plantilla, vive en una fila de celdas numéricas bajo los checks
   * de la estructura salarial. Si el parser no lo saca, conviene que lo intente
   * la IA antes de dar el formulario por leído.
   */
  private boolean parserSuficiente(FMIExtractionDTO extraccion) {
    if (extraccion.getNombreCompleto() == null || extraccion.getMontoBase() == null)
      return false;

    int leidos = 0;
    for (String campo : new String[] { extraccion.getEquipoOCliente(), extraccion.getModalidad(),
        extraccion.getMotivoIngreso(), extraccion.getCargo(), extraccion.getHorario(),
        extraccion.getFechaInicioContrato(), extraccion.getObjetoContrato() }) {
      if (campo != null && !campo.isBlank())
        leidos++;
    }
    return leidos >= 3;
  }

  /**
   * Pasa el formulario por la IA y rellena SÓLO los huecos que dejó el parser:
   * lo que se leyó de la tabla es más fiable que lo que deduzca el modelo.
   */
  private void completarConIa(FMIExtractionDTO extraccion, String textoLimpio) throws IOException, InterruptedException {
    long t0 = System.currentTimeMillis();
    String structuredJson = this.clientOpenIA.sendPromptResponses(
        PromptBuilder.buildFmiPrompt(textoLimpio),
        "gpt-4.1-mini");
    logger.info("OpenAI call (FMI): {}ms", System.currentTimeMillis() - t0);

    FMIExtractionDTO deIa = objectMapper.treeToValue(
        objectMapper.readTree(structuredJson), FMIExtractionDTO.class);

    if (!deIa.isEsFormularioIngreso() && deIa.getMotivoDescarte() != null) {
      extraccion.setEsFormularioIngreso(false);
      extraccion.setMotivoDescarte(deIa.getMotivoDescarte());
      extraccion.setOrigen("IA");
      return;
    }

    boolean parserAporto = extraccion.getNombreCompleto() != null;
    extraccion.setOrigen(parserAporto ? "MIXTO" : "IA");
    extraccion.setConfianza(parserAporto ? "MEDIA" : "BAJA");

    if (extraccion.getNombreCompleto() == null) {
      extraccion.setNombreCompleto(deIa.getNombreCompleto());
      extraccion.setNombres(deIa.getNombres());
      extraccion.setApellidoPaterno(deIa.getApellidoPaterno());
      extraccion.setApellidoMaterno(deIa.getApellidoMaterno());
    }
    if (extraccion.getEquipoOCliente() == null) {
      extraccion.setEquipoOCliente(deIa.getEquipoOCliente());
      extraccion.setEtiquetaEquipo(deIa.getEtiquetaEquipo());
      extraccion.setEsOutsourcing(deIa.isEsOutsourcing());
    }
    if (extraccion.getModalidad() == null)
      extraccion.setModalidad(deIa.getModalidad());
    if (extraccion.getMotivoIngreso() == null)
      extraccion.setMotivoIngreso(deIa.getMotivoIngreso());
    if (extraccion.getCargo() == null)
      extraccion.setCargo(deIa.getCargo());
    if (extraccion.getHorario() == null)
      extraccion.setHorario(deIa.getHorario());
    if (extraccion.getMontoBase() == null)
      extraccion.setMontoBase(deIa.getMontoBase());
    if (extraccion.getMontoMovilidad() == null)
      extraccion.setMontoMovilidad(deIa.getMontoMovilidad());
    if (extraccion.getFechaInicioContrato() == null)
      extraccion.setFechaInicioContrato(deIa.getFechaInicioContrato());
    if (extraccion.getFechaFinContrato() == null)
      extraccion.setFechaFinContrato(deIa.getFechaFinContrato());
    if (extraccion.getProyectoServicio() == null)
      extraccion.setProyectoServicio(deIa.getProyectoServicio());
    if (extraccion.getObjetoContrato() == null)
      extraccion.setObjetoContrato(deIa.getObjetoContrato());
    if (extraccion.getDeclaraSunat() == null)
      extraccion.setDeclaraSunat(deIa.getDeclaraSunat());
    if (extraccion.getSedeDeclarar() == null)
      extraccion.setSedeDeclarar(deIa.getSedeDeclarar());
    if (extraccion.getGestor() == null)
      extraccion.setGestor(deIa.getGestor());
    if (extraccion.getFechaEmision() == null)
      extraccion.setFechaEmision(deIa.getFechaEmision());
  }

  /** PDF que no sirve: se responde OK con el motivo, no como error técnico. */
  private FMIExtractionDTO descartado(String motivo) {
    FMIExtractionDTO extraccion = new FMIExtractionDTO();
    extraccion.setEsFormularioIngreso(false);
    extraccion.setMotivoDescarte(motivo);
    extraccion.setConfianza("ALTA");
    extraccion.setOrigen("PARSER");
    return extraccion;
  }

  /**
   * Valida el archivo, extrae el texto del PDF y lo limpia.
   * Lógica compartida entre {@link #analyzeCv(MultipartFile)} y
   * {@link #analyzeCvDiff(MultipartFile, Integer, String)}.
   */
  private String extractCvText(MultipartFile cvFile) throws IOException {
    return extractPdfText(cvFile, true);
  }

  /**
   * Extrae el texto de un PDF.
   *
   * @param limpiar true para los CV, donde {@link TextUtils#cleanCvText} quita
   *                ruido de maquetación; false para el FMI, porque ahí los
   *                saltos de línea y los espacios SON la estructura de la tabla
   *                y es de donde el parser saca cada campo.
   */
  private String extractPdfText(MultipartFile file, boolean limpiar) throws IOException {
    if (file == null || file.isEmpty())
      throw new IllegalArgumentException("El archivo está vacío");

    String contentType = file.getContentType();
    if (contentType == null || !contentType.equalsIgnoreCase("application/pdf"))
      throw new IllegalArgumentException("El archivo debe ser un PDF");

    this.logger.info("Processing file: {}", file.getOriginalFilename());

    String extractedText;
    int rawLength;
    long t0 = System.currentTimeMillis();

    try (PDDocument document = Loader.loadPDF(file.getBytes())) {
      PDFTextStripper pdfStripper = new PDFTextStripper();
      pdfStripper.setSortByPosition(true);
      extractedText = pdfStripper.getText(document);
      rawLength = extractedText.length();
      this.logger.info("Text extracted successfully. Length: {}", rawLength);
    }
    logger.info("PDF extraction: {}ms", System.currentTimeMillis() - t0);

    if (!limpiar)
      return extractedText;

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
