package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.repository.TalentsRepository;
import com.bdt.bancotalentosbackend.service.ITalentsService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TalentsService implements ITalentsService {
    private final TalentsRepository talentsRepository;
    private final JWTHelper jwt;

    @Override
    public TalentsListResponse getTalents(String token, SearchRequest searchRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTAR_TALENTOS);
        return talentsRepository.getTalents(baseRequest, searchRequest);
    }

    @Override
    public TalentResponse getTalentById(String token, Integer talentId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTAR_TALENTOS);
        return talentsRepository.getTalentById(baseRequest, talentId);
    }

    @Override
    public BaseResponse addOrUpdateTalent(String token, TalentRequest talentRequest) throws JsonProcessingException {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addOrUpdateTalent(baseRequest, talentRequest);
    }

    @Override
    public BaseResponse addTalentToFavourite(String token, TalentToFavRequest favRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addTalentToFavourite(baseRequest, favRequest);
    }

    @Override
    public BaseResponse addTalentTechAbility(String token, TechAbilityRequest techAbilityRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addTalentTechAbility(baseRequest, techAbilityRequest);
    }

    @Override
    public BaseResponse addTalentSoftAbility(String token, SoftAbilityRequest techAbilityRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addTalentSoftAbility(baseRequest, techAbilityRequest);
    }

    @Override
    public BaseResponse addOrUpdateTalentExperience(String token, ExperienceRequest experienceRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addOrUpdateTalentExperience(baseRequest, experienceRequest);
    }

    @Override
    public BaseResponse deleteTalentExperience(String token, Integer experienceId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.deleteTalentExperience(baseRequest, experienceId);
    }

    @Override
    public BaseResponse addOrUpdateTalentEducation(String token, EducationRequest educationRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addOrUpdateTalentEducation(baseRequest, educationRequest);
    }

    @Override
    public BaseResponse deleteTalentEducation(String token, Integer educationId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.deleteTalentEducation(baseRequest, educationId);
    }

    @Override
    public BaseResponse addOrUpdateTalentLanguage(String token, LanguageRequest languageRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addOrUpdateTalentLanguage(baseRequest, languageRequest);
    }

    @Override
    public BaseResponse deleteTalentLanguage(String token, Integer languageId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.deleteTalentLanguage(baseRequest, languageId);
    }

    @Override
    public BaseResponse addOrUpdateTalentFeedback(String token, FeedbackRequest feedbackRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addOrUpdateTalentFeedback(baseRequest, feedbackRequest);
    }

    @Override
    public BaseResponse deleteTalentFeedback(String token, Integer feedbackId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.deleteTalentFeedback(baseRequest, feedbackId);
    }

    //    Espacio solo para migración de archivos
    @Override
    public void migrateProfilePhoto() {
        talentsRepository.migrateProfilePhoto();
    }
}
