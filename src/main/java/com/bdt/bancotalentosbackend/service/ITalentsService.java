package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.FileResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ITalentsService {
    TalentsListResponse getTalents(String token, SearchRequest searchRequest);
    TalentResponse getTalentById(String token, Integer talentId);
    FileResponse getTalentFile(String token, Integer filePath);
    BaseResponse addOrUpdateTalent(String token, TalentRequest updateRequest) throws JsonProcessingException;
    BaseResponse addTalentToFavourite(String token, TalentToFavRequest favRequest);
    BaseResponse addTalentTechAbility(String token, TechAbilityRequest techAbilityRequest);
    BaseResponse addTalentSoftAbility(String token, SoftAbilityRequest techAbilityRequest);
    BaseResponse addOrUpdateTalentExperience(String token, ExperienceRequest experienceRequest);
    BaseResponse deleteTalentExperience(String token, Integer experienceId);
    BaseResponse addOrUpdateTalentEducation(String token, EducationRequest educationRequest);
    BaseResponse deleteTalentEducation(String token, Integer educationId);
    BaseResponse addOrUpdateTalentLanguage(String token, LanguageRequest languageRequest);
    BaseResponse deleteTalentLanguage(String token, Integer languageId);
    BaseResponse addOrUpdateTalentFeedback(String token, FeedbackRequest feedbackRequest);
    BaseResponse deleteTalentFeedback(String token, Integer feedbackId);
    BaseResponse uploadTalentFile(String token, UploadTalentFileRequest uploadTalentFileRequest);
    BaseResponse updateCvFile(String token, UpdateTalentFileRequest updateTalentFileRequest);
    BaseResponse updateTalentFile(String token, UpdateTalentFileRequest updateTalentFileRequest);


    void migrateProfilePhoto();
    void migrateCV();
}
