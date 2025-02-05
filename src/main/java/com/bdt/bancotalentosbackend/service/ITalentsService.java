package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.SearchRequest;
import com.bdt.bancotalentosbackend.model.request.TalentUpdateRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;

public interface ITalentsService {
    TalentsListResponse getTalents(String token, SearchRequest searchRequest);
    TalentResponse getTalentById(String token, Integer talentId);
    BaseResponse updateTalent(String token, TalentUpdateRequest updateRequest);
}
