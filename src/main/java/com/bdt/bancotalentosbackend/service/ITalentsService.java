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
    BaseResponse addTalentTechAbility(String token, AddTechAbilityRequest techAbilityRequest);
    BaseResponse addTalentSoftAbility(String token, AddSoftAbilityRequest techAbilityRequest);
    BaseResponse addOrUpdateTalentExperience(String token, ExperienceRequest experienceRequest);
    BaseResponse deleteTalentExperience(String token, Integer experienceId);
    BaseResponse addOrUpdateTalentEducation(String token, EducationRequest educationRequest);
    BaseResponse deleteTalentEducation(String token, Integer educationId);
    BaseResponse addOrUpdateTalentLanguage(String token, LanguageRequest languageRequest);
    BaseResponse deleteTalentLanguage(String token, Integer languageId);
    BaseResponse addOrUpdateTalentFeedback(String token, FeedbackRequest feedbackRequest);
    BaseResponse deleteTalentFeedback(String token, Integer feedbackId);
}
