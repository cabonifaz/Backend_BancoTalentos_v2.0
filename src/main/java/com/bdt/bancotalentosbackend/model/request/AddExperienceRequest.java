package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddExperienceRequest {
    private Integer idTalento;
    private String empresa;
    private String puesto;
    private String fechaInicio;
    private String fechaFin;
    private Integer flActualidad;
    private String funciones;
}
