package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Un movimiento del historial de lista negra (fila de
 * SP_BT_HISTORIAL_LISTA_NEGRA_LST). MOVIMIENTO ∈ {CREACION, ACTUALIZACION, ELIMINACION}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistHistoryDTO {
    private Integer idHistorialListaNegra;
    private Integer idListaNegra;
    private Integer idTalento;
    private Integer idCliente;
    private String cliente;
    private String motivo;
    private String movimiento;
    private String usucre;
    private String fchcre;
}
