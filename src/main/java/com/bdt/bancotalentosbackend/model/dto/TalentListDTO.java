package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentListDTO {
    private Integer idTalento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String rutaImagen;
    private String puesto;
    private String pais;
    private String ciudad;
    private Double montoInicialPlanilla;
    private Double montoFinalPlanilla;
    private Double montoInicialRxH;
    private Double montoFinalRxH;
    private String moneda;
    private Integer estrellas;
    private Integer esFavorito;
}
