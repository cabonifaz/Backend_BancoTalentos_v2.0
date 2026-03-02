package com.bdt.bancotalentosbackend.service.impl;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bdt.bancotalentosbackend.model.request.AIPromptRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.GeneralResponse;
import com.bdt.bancotalentosbackend.model.response.IACVResponse;
import com.bdt.bancotalentosbackend.model.response.PromptResponse;
import com.bdt.bancotalentosbackend.util.ClientOpenIA;
import com.bdt.bancotalentosbackend.util.TextUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IAService {

  private final Logger logger = LoggerFactory.getLogger(IAService.class);
  private final ObjectMapper objectMapper;

  public BaseResponse prompt(AIPromptRequest request) throws IOException, InterruptedException {
    this.logger.info("Service IA started ");
    ClientOpenIA client = new ClientOpenIA();
    this.logger.info("Client was created");
    this.logger.info("Prompt generated");
    String response = client.sendPrompt(request.getPrompt());
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

      if (cvFile == null || cvFile.isEmpty())
        throw new IllegalArgumentException("El archivo está vacío");

      String contentType = cvFile.getContentType();
      if (contentType == null || !contentType.equalsIgnoreCase("application/pdf"))
        throw new IllegalArgumentException("El archivo debe ser un PDF");

      this.logger.info("Processing file: {}", cvFile.getOriginalFilename());

      String extractedText;
      Integer rawLength, cleanedLength;
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
      cleanedLength = extractedText.length();
      this.logger.info("Cleaned text length: {}", cleanedLength);
      this.logger.info("Diffeerence in length after cleaning: {}", rawLength - cleanedLength);
      this.logger.info("Text cleaning: {}ms", System.currentTimeMillis() - t1);

      long t2 = System.currentTimeMillis();
      this.logger.info("Starting OpenAI API call");
      ClientOpenIA client = new ClientOpenIA();
      String prompt = client.buildCVPrompt(extractedText);

      String structuredJson = client.sendPromptResponses(
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
}
