package com.bdt.bancotalentosbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bdt.bancotalentosbackend.model.request.AIPromptRequest;
import com.bdt.bancotalentosbackend.model.request.SummarizeRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.GeneralResponse;
import com.bdt.bancotalentosbackend.model.response.IACVResponse;
import com.bdt.bancotalentosbackend.model.response.PromptResponse;
import com.bdt.bancotalentosbackend.model.response.SummarizeResponse;
import com.bdt.bancotalentosbackend.service.impl.IAService;
import com.bdt.bancotalentosbackend.util.JWTHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
@Tag(name = "IA Services Controller")
public class IAController {

  private final IAService iAService;

  @PostMapping(value = "/analyze-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse> analyzeCv(
      @RequestPart("file") MultipartFile file,
      HttpServletRequest httpServletRequest) {
    var response = iAService.analyzeCv(file);
    return ResponseEntity.ok().body(response);
  }

  /**
   * Analizador de Diferencias: compara un nuevo CV contra la información actual del
   * talento y devuelve ÚNICAMENTE la información nueva, adicional o mejorada.
   * A diferencia de {@code /analyze-cv}, este endpoint NO extrae todo el CV.
   */
  @PostMapping(value = "/analyze-cv-diff", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<BaseResponse> analyzeCvDiff(
      @RequestPart("file") MultipartFile file,
      @RequestParam("idTalento") Integer idTalento,
      HttpServletRequest httpServletRequest) {
    try {
      String token = JWTHelper.extractToken(httpServletRequest);
      GeneralResponse<IACVResponse> response = iAService.analyzeCvDiff(file, idTalento, token);
      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      BaseResponse baseResponse = new BaseResponse(3, "Hubo un error al analizar las diferencias del CV");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
    }
  }

  @PostMapping("/prompt")
  public ResponseEntity<BaseResponse> prompt(
      @RequestBody AIPromptRequest request,
      HttpServletRequest httpServletRequest) {
    try {
      PromptResponse response = (PromptResponse) iAService.prompt(request);
      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      BaseResponse baseResponse = new BaseResponse(3, "Hubo un error al procesar el prompt");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
    }
  }

  @PostMapping("/summarize")
  public GeneralResponse<SummarizeResponse> summarize(@RequestBody SummarizeRequest request,
      HttpServletRequest httpServletRequest) {
    return iAService.summarizeActivities(request.getActivities(), request.getInstructions());
  }

}
