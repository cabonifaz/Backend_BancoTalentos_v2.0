package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {
    private Integer idEducacion;
    private String nombreInstitucion;
    private String carrera;
    private String grado;
    private String fechaInicio;
    private String fechaFin;
    private Integer flActualidad;
}
