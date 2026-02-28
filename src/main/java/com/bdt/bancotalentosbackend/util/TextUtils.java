package com.bdt.bancotalentosbackend.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TextUtils {
  public static String cleanCvText(String text) {
    if (text == null)
      return "";

    return text
        // Normalizar saltos de línea Windows (\r\n) a Unix (\n)
        .replaceAll("\\r\\n|\\r", "\n")
        // Eliminar espacios y tabs al inicio y fin de cada línea
        .replaceAll("(?m)^[ \\t]+|[ \\t]+$", "")
        // Eliminar líneas que solo tienen espacios/guiones/puntos/bullets
        .replaceAll("(?m)^[\\s\\-\\.•·\\|]+$", "")
        // Eliminar líneas vacías de menos de 3 caracteres (ruido del PDF)
        .replaceAll("(?m)^.{0,2}$\\n", "")
        // Reducir múltiples saltos de línea a máximo 2
        .replaceAll("\\n{3,}", "\n\n")
        // Eliminar espacios múltiples dentro de una línea
        .replaceAll("[ \\t]{2,}", " ")
        // Eliminar espacios antes de puntuación
        .replaceAll(" ([,\\.;:])", "$1")
        .strip();
  }
}