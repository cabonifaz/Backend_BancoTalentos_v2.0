package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {
    private Integer idArchivo;
    private String nombreArchivo;
    private String tipoArchivo;
    private String tipoDocumento;
}
