package com.bdt.bancotalentosbackend.model.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewTechSkillReponse {
    private BaseResponse baseResponse;
    private Integer idSkill;
}
