package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MigrationCVDTO {
    private Integer idTalento;
    private String nuevaRutaCV;
    private String fileExtension;
    private String fileBase64;
}
