package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.FileResponse;
import com.bdt.bancotalentosbackend.model.response.TalentPhotoUrlResponse;
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
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);

        if (request.getIdTalento() == null) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Talento inválido"), null, null, null, false);
        }
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Nombre de archivo inválido"), null, null, null, false);
        }

        String originalFilename = request.getFileName();
        String extension = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        String s3Path;
        String cleanName;
        // requiresConfirm indica si el frontend debe registrar/actualizar la ruta en
        // BD tras subir. En un reemplazo in-place (misma key) NO hace falta confirm:
        // basta con que el PUT a la URL pre-firmada devuelva 200.
        boolean requiresConfirm;

        // Content-type con el que se firma la URL. En el reemplazo del CV se fuerza a
        // PDF para que la URL pre-firmada SOLO admita PDF (ya no se aceptan Word).
        String signContentType = request.getContentType();

        Integer idArchivo = request.getIdArchivo();
        if (idArchivo != null && idArchivo > 0) {
            // Reemplazo del CV: SOLO PDF. Se sobrescribe la MISMA key (in-place), por
            // lo que la ruta en BD no cambia y basta el 200 del PUT (sin confirm).
            boolean isPdf = "application/pdf".equalsIgnoreCase(request.getContentType())
                    || ".pdf".equalsIgnoreCase(extension);
            if (!isPdf) {
                return new TalentPresignedUrlResponse(
                        new BaseResponse(3, "Solo se permiten archivos PDF"), null, null, null, false);
            }
            FileResponse existing = talentsRepository.getTalentFile(baseRequest, idArchivo);
            if (existing == null || existing.getArchivo() == null || existing.getArchivo().isEmpty()) {
                return new TalentPresignedUrlResponse(
                        new BaseResponse(3, "Archivo a reemplazar no encontrado"), null, null, null, false);
            }
            s3Path = existing.getArchivo();
            cleanName = s3Path.contains("/") ? s3Path.substring(s3Path.lastIndexOf("/") + 1) : s3Path;
            requiresConfirm = false;
            signContentType = "application/pdf";
        } else {
            // Archivo nuevo: carpeta según el tipo de documento y key única.
            // 1 = CV, 5 = CV Fractal ES, 6 = CV Fractal EN, resto = archivos.
            Integer idTipoDocumento = request.getIdTipoDocumento();
            String folder;
            if (idTipoDocumento != null && idTipoDocumento == 1) {
                folder = Constante.RUTA_REPOSITORIO_CV_TALENTO;
            } else if (idTipoDocumento != null && idTipoDocumento == 5) {
                folder = Constante.RUTA_REPOSITORIO_CV_ES_TALENTO;
            } else if (idTipoDocumento != null && idTipoDocumento == 6) {
                folder = Constante.RUTA_REPOSITORIO_CV_EN_TALENTO;
            } else {
                folder = Constante.RUTA_REPOSITORIO_TALENTO_ARCHIVOS;
            }
            folder = folder.replace("[ID]", request.getIdTalento().toString());

            cleanName = originalFilename;
            if (cleanName.length() > 100) {
                cleanName = cleanName.substring(0, 95) + extension;
            }
            // Nombre único en S3 para evitar colisiones.
            String generatedFileName = System.currentTimeMillis() + "_" + cleanName.replaceAll("\\s+", "_");
            s3Path = folder + generatedFileName;
            requiresConfirm = true;
        }

        String uploadUrl = S3Utils.getUploadSignedUrl(s3Path, signContentType, 5);
        if (uploadUrl == null || uploadUrl.isEmpty()) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Error generando URL"), null, null, null, false);
        }

        return new TalentPresignedUrlResponse(
                new BaseResponse(2, "URL generada correctamente"), uploadUrl, s3Path, cleanName, requiresConfirm);
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
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Archivo no encontrado"), null, null, null, false);
        }

        String path = fileResponse.getArchivo();
        String url = request.isInline()
                ? S3Utils.getSignedUrlInline(path, 5)
                : S3Utils.getSignedUrl(path, 5);
        if (url == null || url.isEmpty()) {
            return new TalentPresignedUrlResponse(new BaseResponse(3, "Error generando URL de descarga"), null, null, null, false);
        }

        String fileName = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
        return new TalentPresignedUrlResponse(new BaseResponse(2, "URL generada correctamente"), url, null, fileName, false);
    }

    /**
     * URL PUT pre-firmada para la foto de perfil.
     *
     * A diferencia del CV y los certificados, la foto NO se registra con
     * confirm-upload: la ruta viaja como {@code rutaArchivo} dentro del
     * addOrUpdateTalent que el frontend ya hace después de subir. Aquí sólo se
     * genera la key y se firma, igual que en la firma de usuario.
     *
     * La URL se firma con el content-type recibido, así que S3 rechaza el PUT si
     * el navegador manda otra cosa. Se admite únicamente PNG y JPEG, que es lo
     * que valida el frontend.
     */
    @Override
    public TalentPhotoUrlResponse generateTalentPhotoUploadUrl(String token, TalentPhotoUrlRequest request) {
        UserDTO user = jwt.decodeToken(token);
        // Se decodifica el token para exigir sesión válida y dejar trazabilidad,
        // con la misma funcionalidad que usa la actualización del talento.
        Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);

        TalentPhotoUrlResponse response = new TalentPhotoUrlResponse();

        if (request.getIdTalento() == null || request.getIdTalento() <= 0) {
            response.setBaseResponse(new BaseResponse(3, "Talento inválido"));
            return response;
        }
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            response.setBaseResponse(new BaseResponse(3, "Nombre de archivo inválido"));
            return response;
        }

        String contentType = request.getContentType() == null ? "" : request.getContentType().trim();
        if (!"image/png".equalsIgnoreCase(contentType) && !"image/jpeg".equalsIgnoreCase(contentType)) {
            response.setBaseResponse(new BaseResponse(3, "Solo se permiten imágenes PNG o JPEG"));
            return response;
        }

        String originalFilename = request.getFileName().trim();
        String extension = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        String cleanName = originalFilename;
        if (cleanName.length() > 100) {
            cleanName = cleanName.substring(0, 95) + extension;
        }
        cleanName = cleanName.replaceAll("\\s+", "_");

        // OJO: la constante trae el marcador [ID], hay que sustituirlo.
        String folder = Constante.RUTA_REPOSITORIO_FOTO_TALENTO
                .replace("[ID]", request.getIdTalento().toString());
        // Key única: si el usuario cancela tras pedir la URL, la foto anterior
        // sigue intacta porque no se sobrescribe nada.
        String s3Path = folder + System.currentTimeMillis() + "_" + cleanName;

        String uploadUrl = S3Utils.getUploadSignedUrl(s3Path, contentType, 5);
        if (uploadUrl == null || uploadUrl.isEmpty()) {
            response.setBaseResponse(new BaseResponse(3, "No se pudo generar la URL de carga"));
            return response;
        }

        response.setBaseResponse(new BaseResponse(2, "URL generada correctamente"));
        response.setUrl(uploadUrl);
        response.setPath(s3Path);
        response.setFileName(cleanName);
        return response;
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
