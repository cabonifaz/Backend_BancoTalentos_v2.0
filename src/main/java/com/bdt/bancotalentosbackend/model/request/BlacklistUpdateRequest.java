package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Actualización del motivo de una restricción existente en la lista negra.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistUpdateRequest {
    private Integer idListaNegra;
    private String motivo;
}
