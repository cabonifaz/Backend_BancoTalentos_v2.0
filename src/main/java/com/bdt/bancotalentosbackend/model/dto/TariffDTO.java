package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Una tarifa (fila de TARIFARIO) con los nombres amigables ya resueltos. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffDTO {
    private Integer idTarifario;
    private Integer idCliente;
    private String razonSocial;
    private Integer idPerfil;
    private String perfil;
    private Integer idMoneda;
    private String moneda;
    private Integer idTipoTarifa;
    private String tipoTarifa;
    private BigDecimal tarifa;
    private BigDecimal tipoCambio;
}
