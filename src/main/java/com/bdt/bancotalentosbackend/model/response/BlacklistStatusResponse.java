package com.bdt.bancotalentosbackend.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Estado de un talento en la lista negra: bloqueado = true si tiene alguna
 * restricción activa para cualquier cliente (global o específico).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistStatusResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    private boolean bloqueado;
}
