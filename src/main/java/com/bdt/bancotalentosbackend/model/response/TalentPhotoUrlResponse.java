package com.bdt.bancotalentosbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Respuesta del presign de la foto de perfil. Mismo contrato que la firma de usuario. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentPhotoUrlResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    /** URL PUT pre-firmada para subir la foto directamente a S3. */
    private String url;
    /** Ruta (key) S3 destino; se envía como rutaArchivo al actualizar el talento. */
    private String path;
    /** Nombre limpio del archivo, para guardarlo junto a la ruta. */
    private String fileName;
}
