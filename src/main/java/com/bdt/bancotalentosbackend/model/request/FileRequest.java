package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileRequest {
    private Integer idTipoArchivo;
    private String nombreArchivo;
    private String extensionArchivo;
    private String stringB64;
    private Integer idTipoDocumento;
}



