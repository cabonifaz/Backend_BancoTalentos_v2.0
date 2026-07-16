package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Baja lógica de una restricción de la lista negra. El motivo es propio de la
 * eliminación (por qué se levanta) y no el que tenía la restricción: es el que
 * queda registrado en el historial.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistRemoveRequest {
    private Integer idListaNegra;
    private String motivo;
}
