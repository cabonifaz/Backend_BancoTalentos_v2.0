package com.bdt.bancotalentosbackend.model.response;

import com.bdt.bancotalentosbackend.model.dto.BlacklistClientDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Estado de un talento en la lista negra: bloqueado = true si tiene alguna
 * restricción activa, y la lista de clientes de los que está restringido
 * (un solo "TODOS LOS CLIENTES" cuando la restricción es global).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistStatusResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    private boolean bloqueado;
    private List<BlacklistClientDTO> clientes = new ArrayList<>();
}
