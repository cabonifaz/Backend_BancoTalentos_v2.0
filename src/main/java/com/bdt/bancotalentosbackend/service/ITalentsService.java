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
    BaseResponse addTalentExperience(String token, AddExperienceRequest experienceRequest);
    BaseResponse updateTalentExperience(String token, UpdateExperienceRequest experienceRequest);
    BaseResponse deleteTalentExperience(String token, Integer experienceId);
    BaseResponse addTalentEducation(String token, AddEducationRequest educationRequest);
    BaseResponse updateTalentEducation(String token, UpdateEducationRequest educationRequest);
    BaseResponse deleteTalentEducation(String token, Integer educationId);
    BaseResponse addTalentLanguage(String token, AddLanguageRequest languageRequest);
    BaseResponse updateTalentLanguage(String token, UpdateLanguageRequest languageRequest);
    BaseResponse deleteTalentLanguage(String token, Integer languageId);
    BaseResponse addTalentFeedback(String token, AddFeedbackRequest feedbackRequest);
    BaseResponse updateTalentFeedback(String token, UpdateFeedbackRequest feedbackRequest);
    BaseResponse deleteTalentFeedback(String token, Integer feedbackId);
}
