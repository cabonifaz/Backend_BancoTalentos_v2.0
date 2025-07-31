package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.FileResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.repository.TalentsRepository;
import com.bdt.bancotalentosbackend.service.ITalentsService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.FileUtils;
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
    public TalentResponse getTalentById(String token, Integer talentId, boolean loadExtraInfo) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTAR_TALENTOS);
        return talentsRepository.getTalentById(baseRequest, talentId, loadExtraInfo);
    }

    @Override
    public FileResponse getTalentFile(String token, Integer fileId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTAR_TALENTOS);
        FileResponse fileResponse = talentsRepository.getTalentFile(baseRequest, fileId);

        if (fileResponse != null && fileResponse.getBaseResponse().getIdMensaje() == 2) {
            String fileB64 = FileUtils.cargarArchivoAws(fileResponse.getArchivo()); // file path
            fileResponse.setArchivo(fileB64);

            if (fileB64.isEmpty()) {
                fileResponse.setBaseResponse(new BaseResponse(1, "Archivo no encontrado"));
            }
        }

        return fileResponse;
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

    @Override
    public BaseResponse uploadTalentFile(String token, UploadTalentFileRequest uploadTalentFileRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.uploadTalentFile(baseRequest, uploadTalentFileRequest);
    }

    @Override
    public BaseResponse updateCvFile(String token, UpdateTalentFileRequest updateTalentFileRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.updateTalentFile(baseRequest, updateTalentFileRequest, Constante.RUTA_REPOSITORIO_CV_TALENTO);
    }

    @Override
    public BaseResponse updateTalentFile(String token, UpdateTalentFileRequest updateTalentFileRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.updateTalentFile(baseRequest, updateTalentFileRequest, Constante.RUTA_REPOSITORIO_TALENTO_ARCHIVOS);
    }



    //    Espacio solo para migración de archivos
    @Override
    public void migrateProfilePhoto() {
        talentsRepository.migrateProfilePhoto();
    }

    @Override
    public void migrateCV() {
        talentsRepository.migrateCV();
    }

}
