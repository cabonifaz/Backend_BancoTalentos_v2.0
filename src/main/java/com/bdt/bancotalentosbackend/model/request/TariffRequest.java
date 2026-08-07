package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Alta/edición de una tarifa (SUPERADMIN). En el alta {@code idTarifario} viaja nulo.
 * {@code tipoCambio} es opcional (puede ser nulo).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffRequest {
    private Integer idTarifario;
    private Integer idCliente;
    private Integer idPerfil;
    private Integer idMoneda;
    private BigDecimal tarifa;
    private BigDecimal tipoCambio;
    private Integer idTipoTarifa;
}
