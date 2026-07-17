package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de validar si un talento está restringido para el cliente de un
 * requerimiento. Si {@code bloqueado} es false el resto de campos viene nulo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistValidationDTO {
    private Integer idListaNegra;
    private Integer idTalento;
    /** 0 = la restricción es global (todos los clientes). */
    private Integer idCliente;
    private String cliente;
    private String motivo;
    private Boolean bloqueado;
}
