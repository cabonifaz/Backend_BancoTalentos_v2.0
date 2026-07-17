package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Una restricción activa de lista negra (fila de SP_BT_LISTA_NEGRA_LST).
 * Cuando idCliente = 0, la restricción es global y "cliente" viene como
 * "TODOS LOS CLIENTES".
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistItemDTO {
    private Integer idListaNegra;
    private Integer idTalento;
    private String nombreTalento;
    private Integer idCliente;
    private String cliente;
    private String motivo;
    private String usucre;
    private String fchcre;
}
