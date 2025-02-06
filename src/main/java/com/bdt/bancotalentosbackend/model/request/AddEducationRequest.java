package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddEducationRequest {
    private Integer idTalento;
    private String institucion;
    private String carrera;
    private String grado;
    private String fechaInicio;
    private String fechaFin;
    private Integer flActualidad;
}
