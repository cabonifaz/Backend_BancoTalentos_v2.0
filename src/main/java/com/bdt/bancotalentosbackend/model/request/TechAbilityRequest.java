package com.bdt.bancotalentosbackend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TechAbilityRequest {
    @JsonProperty()
    private Integer idTalento;
    private Integer idHabilidad;
    private Integer anios;
}
