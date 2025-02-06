package com.bdt.bancotalentosbackend.model.request;

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
    private String funciones;
}
