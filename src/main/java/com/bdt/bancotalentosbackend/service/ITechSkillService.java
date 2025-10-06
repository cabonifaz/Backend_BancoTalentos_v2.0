package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.TechSkillRequest;
import com.bdt.bancotalentosbackend.model.response.NewTechSkillReponse;

public interface ITechSkillService {
    NewTechSkillReponse createTechSkill(TechSkillRequest request, String token);
}