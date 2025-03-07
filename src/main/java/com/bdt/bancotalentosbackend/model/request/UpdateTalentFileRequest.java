package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTalentFileRequest {
    private Integer idTalento;
    private Integer idArchivo;
    private String nombreArchivo;
    private String extensionArchivo;
    private Integer idTipoArchivo;
    private Integer idTipoDocumento;
    private String string64;
}
