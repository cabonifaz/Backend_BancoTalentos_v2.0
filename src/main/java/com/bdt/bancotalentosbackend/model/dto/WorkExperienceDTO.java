package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkExperienceDTO {
    private Integer idExperiencia;
    private String nombreEmpresa;
    private String puesto;
    private String funciones;
    private String fechaInicio;
    private String fechaFin;
    private Integer flActualidad;
}
