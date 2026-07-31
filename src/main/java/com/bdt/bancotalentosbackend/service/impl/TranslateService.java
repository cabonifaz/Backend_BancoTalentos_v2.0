package com.bdt.bancotalentosbackend.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bdt.bancotalentosbackend.util.ClientTranslateV2;

import software.amazon.awssdk.services.translate.model.TranslateTextRequest;

/**
 * Traducción de textos con Amazon Translate (AWS SDK v2).
 *
 * Recibe una lista de textos y devuelve sus traducciones en el MISMO orden. Las
 * llamadas se hacen en paralelo (Translate en tiempo real traduce un texto por
 * petición). Los textos vacíos se devuelven tal cual (Amazon no acepta vacío) y,
 * si una traducción individual falla, se conserva el texto original (degradación
 * grácil, sin recurrir a otro motor).
 */
@Service
public class TranslateService {

  private final Logger logger = LoggerFactory.getLogger(TranslateService.class);

  private static final int MAX_THREADS = 16;
  // Límite de Amazon Translate en tiempo real por petición (bytes UTF-8).
  private static final int MAX_BYTES = 10000;

  public List<String> translate(List<String> texts, String source, String target) {
    if (texts == null || texts.isEmpty()) {
      return Collections.emptyList();
    }
    if (target == null || target.isBlank()) {
      throw new IllegalArgumentException("El idioma destino es obligatorio");
    }
    String src = (source == null || source.isBlank()) ? "auto" : source;

    int n = texts.size();
    String[] out = new String[n];
    int poolSize = Math.min(Math.max(n, 1), MAX_THREADS);
    ExecutorService pool = Executors.newFixedThreadPool(poolSize);
    try {
      List<CompletableFuture<Void>> futures = new ArrayList<>(n);
      for (int i = 0; i < n; i++) {
        final int idx = i;
        final String text = texts.get(i);
        futures.add(CompletableFuture.runAsync(
            () -> out[idx] = translateOne(text, src, target), pool));
      }
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    } finally {
      pool.shutdown();
    }
    return Arrays.asList(out);
  }

  private String translateOne(String text, String source, String target) {
    if (text == null) {
      return "";
    }
    // Amazon Translate rechaza texto vacío; se conserva la posición sin llamar.
    if (text.isBlank()) {
      return text;
    }
    // Textos que exceden el límite por petición se dejan sin traducir.
    if (text.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > MAX_BYTES) {
      logger.warn("Texto excede el límite de Amazon Translate; se conserva el original");
      return text;
    }
    try {
      TranslateTextRequest request = TranslateTextRequest.builder()
          .text(text)
          .sourceLanguageCode(source)
          .targetLanguageCode(target)
          .build();
      return ClientTranslateV2.getInstance().translateText(request).translatedText();
    } catch (Exception e) {
      logger.error("Error traduciendo texto: {}", e.getMessage());
      return text; // degradación: se conserva el original
    }
  }
}
