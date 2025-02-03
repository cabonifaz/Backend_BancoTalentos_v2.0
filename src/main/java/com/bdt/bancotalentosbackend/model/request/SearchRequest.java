package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    private Integer page;
    private String search;
    private String techAbilities;
    private Integer idEnglishLevel;
    private Integer idTalentCollection;
}
