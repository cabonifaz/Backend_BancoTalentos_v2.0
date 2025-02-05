package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;

public interface ITalentsService {
    TalentsListResponse getTalents(String token, SearchRequest searchRequest);
    TalentResponse getTalentById(String token, Integer talentId);
    BaseResponse updateTalent(String token, TalentUpdateRequest updateRequest);
    BaseResponse addTalentToFavourite(String token, AddTalentToFavRequest favRequest);
    BaseResponse addTalentTechAbility(String token, AddTalentTechAbilityRequest techAbilityRequest);
    BaseResponse addTalentSoftAbility(String token, AddTalentSoftAbilityRequest techAbilityRequest);
}
