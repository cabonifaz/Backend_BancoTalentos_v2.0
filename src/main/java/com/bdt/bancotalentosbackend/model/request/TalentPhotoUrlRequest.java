package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Petición de URL PUT pre-firmada para la foto de perfil de un talento.
 *
 * La foto NO vive en TALENTO_ARCHIVOS (no tiene idArchivo): es la columna
 * TALENTO.RUTA_IMAGEN. Por eso no usa el flujo de confirm-upload de CV y
 * certificados; la ruta se registra en el addOrUpdateTalent que ya existe,
 * igual que la firma de usuario.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentPhotoUrlRequest {
    private Integer idTalento;
    private String fileName;
    private String contentType;
}
