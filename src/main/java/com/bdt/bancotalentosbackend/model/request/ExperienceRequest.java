package com.bdt.bancotalentosbackend.model.request;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceRequest {
    @JsonProperty()
    private Integer idTalento;
    @JsonProperty()
    private Integer idExperiencia;
    private String empresa;
    private String puesto;
    private String fechaInicio;
    private String fechaFin;
    private Integer flActualidad;
    @Length(max = 5000, message = "Las funciones no pueden exceder los 5000 caracteres")
    private String funciones;
}
