package com.bdt.bancotalentosbackend.model.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslateRequest {
    /** Textos a traducir (se conserva el orden en la respuesta). */
    private List<String> texts;
    /** Idioma origen (ISO, p. ej. "es"/"en") o "auto" para autodetección. */
    private String source;
    /** Idioma destino (ISO, p. ej. "es"/"en"). */
    private String target;
}
