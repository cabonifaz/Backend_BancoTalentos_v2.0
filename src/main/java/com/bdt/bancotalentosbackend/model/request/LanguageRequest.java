package com.bdt.bancotalentosbackend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageRequest {
    @JsonProperty()
    private Integer idTalento;
    @JsonProperty()
    private Integer idTalentoIdioma;
    private Integer idIdioma;
    private Integer idNivel;
    private Integer estrellas;
}
