package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MigrationProfilePhotoDTO {
    private Integer idTalento;
    private String nuevaRutaImagen;
    private String fileExtension;
    private String fileB64;
}
