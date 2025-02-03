package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageDTO {
    private Integer idTalentoIdioma;
    private Integer idIdioma;
    private String nombreIdioma;
    private Integer idNivel;
    private String nivelIdioma;
    private Integer estrellas;
}
