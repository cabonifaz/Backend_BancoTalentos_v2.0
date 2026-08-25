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
    /**
     * Ruta (key) S3 del archivo ya subido con URL pre-firmada.
     *
     * Cuando viaja informada, {@code stringB64} va vacío y el backend NO sube
     * nada: sólo registra esta ruta. Es el camino que usa la foto de perfil desde
     * la migración a subida directa a S3.
     */
    private String rutaArchivo;
}



