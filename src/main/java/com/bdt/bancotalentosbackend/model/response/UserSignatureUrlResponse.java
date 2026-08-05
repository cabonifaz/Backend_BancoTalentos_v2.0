package com.bdt.bancotalentosbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignatureUrlResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    /** URL PUT pre-firmada para subir la firma directamente a S3. */
    private String url;
    /** Ruta (key) S3 destino; se envía como FIRMA al actualizar el usuario. */
    private String path;
}
