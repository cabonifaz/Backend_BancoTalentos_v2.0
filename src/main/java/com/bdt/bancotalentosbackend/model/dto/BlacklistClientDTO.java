package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cliente del que un talento está restringido. idCliente = 0 y
 * cliente = "TODOS LOS CLIENTES" cuando la restricción es global.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistClientDTO {
    private Integer idCliente;
    private String cliente;
}
