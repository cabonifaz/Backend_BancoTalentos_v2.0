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
    public BaseResponse updateTalent(String token, TalentUpdateRequest updateRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.updateTalent(baseRequest, updateRequest);
    }

    @Override
    public BaseResponse addTalentToFavourite(String token, AddTalentToFavRequest favRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addTalentToFavourite(baseRequest, favRequest);
    }

    @Override
    public BaseResponse addTalentTechAbility(String token, AddTechAbilityRequest techAbilityRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addTalentTechAbility(baseRequest, techAbilityRequest);
    }

    @Override
    public BaseResponse addTalentSoftAbility(String token, AddSoftAbilityRequest techAbilityRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addTalentSoftAbility(baseRequest, techAbilityRequest);
    }

    @Override
    public BaseResponse addTalentExperience(String token, AddExperienceRequest experienceRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.addTalentExperience(baseRequest, experienceRequest);
    }

    @Override
    public BaseResponse updateTalentExperience(String token, UpdateExperienceRequest experienceRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.updateTalentExperience(baseRequest, experienceRequest);
    }

    @Override
    public BaseResponse deleteTalentExperience(String token, Integer experienceId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.deleteTalentExperience(baseRequest, experienceId);
    }
}
