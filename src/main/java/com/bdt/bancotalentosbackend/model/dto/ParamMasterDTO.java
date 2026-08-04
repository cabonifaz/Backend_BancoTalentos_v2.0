package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamMasterDTO {
    private Integer idMaestro;
    private String descripcion;
    private Integer totalRegistros;
    private Integer registrosActivos;
    private Integer registrosInactivos;
}
