package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTalentTechAbilityRequest {
    private Integer idTalento;
    private Integer idHabilidad;
    private Integer anios;
}
