package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alta de una restricción en la lista negra. ID_CLIENTE = 0 significa
 * restricción global (todos los clientes).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistCreateRequest {
    private Integer idTalento;
    private Integer idCliente;
    private String motivo;
}
