package com.bdt.bancotalentosbackend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bdt.bancotalentosbackend.model.request.TranslateRequest;
import com.bdt.bancotalentosbackend.model.response.TranslateResponse;
import com.bdt.bancotalentosbackend.service.impl.TranslateService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/translate")
@RequiredArgsConstructor
@Tag(name = "Translate Controller")
public class TranslateController {

  private final TranslateService translateService;

  @PostMapping
  public ResponseEntity<TranslateResponse> translate(@RequestBody TranslateRequest request) {
    try {
      List<String> translations = translateService.translate(
          request.getTexts(), request.getSource(), request.getTarget());
      return ResponseEntity.ok(new TranslateResponse(2, "Traducción exitosa", translations));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new TranslateResponse(3, e.getMessage(), null));
    }
  }
}
