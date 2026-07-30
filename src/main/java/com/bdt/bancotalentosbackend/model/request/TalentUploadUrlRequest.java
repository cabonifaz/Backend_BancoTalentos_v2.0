package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentUploadUrlRequest {
    private Integer idTalento;
    private Integer idTipoDocumento;
    private String fileName;
    private String contentType;
    /**
     * Si viene informado (> 0), se genera la URL para SOBRESCRIBIR el archivo
     * existente in-place (se reutiliza su key en S3), de modo que la ruta en BD no
     * cambia y no hace falta confirmar la subida.
     */
    private Integer idArchivo;
}
