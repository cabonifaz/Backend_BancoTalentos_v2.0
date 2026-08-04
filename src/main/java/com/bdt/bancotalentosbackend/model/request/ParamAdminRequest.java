package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Alta/edición genérica de un parámetro. Se usa para INS y UPD; en el alta
 * {@code idParametro} viaja nulo. El significado de cada campo genérico lo define
 * el maestro, por lo que aquí solo se transportan tal cual.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamAdminRequest {
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
}
