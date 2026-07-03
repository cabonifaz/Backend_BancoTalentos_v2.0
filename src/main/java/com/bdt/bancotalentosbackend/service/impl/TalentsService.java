package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.FileResponse;
import com.bdt.bancotalentosbackend.model.response.TalentPresignedUrlResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.repository.TalentsRepository;
import com.bdt.bancotalentosbackend.service.ITalentsService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.FileUtils;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import com.bdt.bancotalentosbackend.util.S3Utils;
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
        var talentDetails = talentsRepository.getTalentById(baseRequest, talentId, loadExtraInfo);

        // Load Image from AWS S3
        var photoUrl = S3Utils.getSignedUrl(talentDetails.getPhotoUrl());
        talentDetails.setPhotoUrl(photoUrl);

        return talentDetails;
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
        return talentsRepository.updateTalentFile(baseRequest, updateTalentFileRequest,
                Constante.RUTA_REPOSITORIO_CV_TALENTO);
    }

    @Override
    public BaseResponse updateTalentFile(String token, UpdateTalentFileRequest updateTalentFileRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.updateTalentFile(baseRequest, updateTalentFileRequest,
                Constante.RUTA_REPOSITORIO_TALENTO_ARCHIVOS);
    }

    // ─── Subida directa a S3 mediante URL pre-firmada ──────────────────────────

    @Override
    public TalentPresignedUrlResponse generateTalentUploadUrl(String token, TalentUploadUrlRequest request) {
        UserDTO user = jwt.decodeToken(token);
        // Se decodifica el token para validar la sesión (autorización consistente).
        Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);

        if (request.getIdTalento() == null) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Talento inválido"), null, null, null);
        }
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Nombre de archivo inválido"), null, null, null);
        }

        String originalFilename = request.getFileName();
        String extension = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        String cleanName = originalFilename;
        if (cleanName.length() > 100) {
            cleanName = cleanName.substring(0, 95) + extension;
        }

        // Carpeta destino según el tipo de documento (1 = CV, resto = archivos).
        String folder = (request.getIdTipoDocumento() != null && request.getIdTipoDocumento() == 1)
                ? Constante.RUTA_REPOSITORIO_CV_TALENTO
                : Constante.RUTA_REPOSITORIO_TALENTO_ARCHIVOS;
        folder = folder.replace("[ID]", request.getIdTalento().toString());

        // Nombre único en S3 para evitar colisiones.
        String generatedFileName = System.currentTimeMillis() + "_" + cleanName.replaceAll("\\s+", "_");
        String s3Path = folder + generatedFileName;

        String uploadUrl = S3Utils.getUploadSignedUrl(s3Path, request.getContentType(), 5);
        if (uploadUrl == null || uploadUrl.isEmpty()) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Error generando URL"), null, null, null);
        }

        return new TalentPresignedUrlResponse(
                new BaseResponse(2, "URL generada correctamente"), uploadUrl, s3Path, cleanName);
    }

    @Override
    public BaseResponse confirmTalentUpload(String token, TalentConfirmUploadRequest request) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);

        if (request.getPath() == null || request.getPath().trim().isEmpty()) {
            return new BaseResponse(3, "Ruta de archivo inválida");
        }

        // Se valida que el archivo exista físicamente en S3 antes de registrarlo en BD.
        if (!S3Utils.exists(request.getPath())) {
            return new BaseResponse(3, "El archivo no existe en S3");
        }

        return talentsRepository.confirmTalentFile(baseRequest, request);
    }

    @Override
    public TalentPresignedUrlResponse generateTalentDownloadUrl(String token, TalentDownloadUrlRequest request) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTAR_TALENTOS);

        // El repositorio devuelve la RUTA_ARCHIVO (path S3) en el campo 'archivo'.
        FileResponse fileResponse = talentsRepository.getTalentFile(baseRequest, request.getIdFile());

        if (fileResponse == null || fileResponse.getBaseResponse() == null
                || fileResponse.getBaseResponse().getIdMensaje() != 2
                || fileResponse.getArchivo() == null || fileResponse.getArchivo().isEmpty()) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Archivo no encontrado"), null, null, null);
        }

        String path = fileResponse.getArchivo();
        String url = S3Utils.getSignedUrl(path, 5);
        if (url == null || url.isEmpty()) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Error generando URL de descarga"), null, null, null);
        }

        String fileName = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
        return new TalentPresignedUrlResponse(new BaseResponse(2, "URL generada correctamente"), url, null, fileName);
    }

    // Espacio solo para migración de archivos
    @Override
    public void migrateProfilePhoto() {
        talentsRepository.migrateProfilePhoto();
    }

    @Override
    public void migrateCV() {
        talentsRepository.migrateCV();
    }

    @Override
    public BaseResponse uploadCVLang(String token, UploadTalentFileRequest uploadRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.uploadTalentCVLang(baseRequest, uploadRequest);
    }

    @Override
    public BaseResponse updateCVLang(String token, UpdateTalentFileRequest request) {

        String basePath = null;

        switch (request.getIdTipoDocumento()) {
            case 5: // CV ES
                basePath = Constante.RUTA_REPOSITORIO_CV_ES_TALENTO;
                break;

            case 6: // CV EN
                basePath = Constante.RUTA_REPOSITORIO_CV_EN_TALENTO;
                break;

            default:
                return new BaseResponse(3, "Tipo de documento desconocido");
        }

        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return talentsRepository.updateTalentFile(baseRequest, request,
                basePath);
    }

    @Override
    public BaseResponse removeTechnicalSkill(String token, Integer targetId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return this.talentsRepository.removeTechnicalSkill(baseRequest, targetId);
    }

    @Override
    public BaseResponse removeSoftSkill(String token, Integer targetId) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);
        return this.talentsRepository.removeSoftSkill(baseRequest, targetId);
    }

}
