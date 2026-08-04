package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamItemDTO {
    private Integer idParametro;
    private Integer idMaestro;
    private String descripcion;
    private Integer idSubMaestro;
    private BigDecimal num1;
    private BigDecimal num2;
    private BigDecimal num3;
    private String string1;
    private String string2;
    private String string3;
    private String date1;
    private String date2;
    private String date3;
    private String usucre;
    private String fchcre;
    private String usumod;
    private String fchmod;
    private Integer idEstadoRegistro;
}
