package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLanguageRequest {
    private Integer idTalentoIdioma;
    private Integer idIdioma;
    private Integer idNivel;
    private Integer estrellas;
}
