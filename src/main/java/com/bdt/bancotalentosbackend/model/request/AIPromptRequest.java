package com.bdt.bancotalentosbackend.model.request;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPromptRequest {
    private String prompt;
    /** Instrucciones de sistema opcionales (si se omite, se usa un default). */
    private String instructions;
    /**
     * JSON Schema opcional. Si viene, la respuesta se fuerza con structured outputs
     * estrictos (json_schema); si no, se usa json_object (JSON válido genérico).
     */
    private JsonNode schema;
}
