package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {
    private Integer idFeedback;
    private Integer editable;
    private String usuario;
    @Size(max = 5000, message = "La descripción no puede exceder los 5000 caracteres")
    private String descripcion;
    private Integer estrellas;
}
