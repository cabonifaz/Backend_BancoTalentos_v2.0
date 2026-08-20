package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.FileResponse;
import com.bdt.bancotalentosbackend.model.response.TalentPhotoUrlResponse;
import com.bdt.bancotalentosbackend.model.response.TalentPresignedUrlResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.service.impl.TalentsService;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/talent")
@RequiredArgsConstructor
@Tag(name = "Talento")
public class TalentsController {
    private final TalentsService talentsService;

    @GetMapping("/list")
    public ResponseEntity<TalentsListResponse> getTalents(
            @RequestParam @Nullable Integer nPag,
            @RequestParam @Nullable String search,
            @RequestParam @Nullable String techAbilities,
            @RequestParam @Nullable Integer idEnglishLevel,
            @RequestParam @Nullable Integer idTalentCollection,
            @RequestParam @Nullable String jobPosition,
            @RequestParam @Nullable Integer yearsExperience,
            @RequestParam @Nullable String educationName,
            @RequestParam @Nullable Integer idAcademicGrade,
            HttpServletRequest httpServletRequest) {
        TalentsListResponse response = new TalentsListResponse();
        SearchRequest searchRequest = new SearchRequest(nPag, search, techAbilities, idEnglishLevel, jobPosition, yearsExperience, educationName, idAcademicGrade, idTalentCollection);

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.getTalents(token, searchRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/data")
    public ResponseEntity<TalentResponse> getTalent(
            @RequestParam Integer talentId,
            @RequestParam boolean loadExtraInfo,
            HttpServletRequest httpServletRequest) {
        TalentResponse response = new TalentResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.getTalentById(token, talentId, loadExtraInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/file")
    public ResponseEntity<FileResponse> getTalentFile(
            @RequestParam Integer fileId,
            HttpServletRequest httpServletRequest) {
        FileResponse response = new FileResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.getTalentFile(token, fileId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(1, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addOrUpdateTalent")
    public ResponseEntity<BaseResponse> addOrUpdateTalent(
            @jakarta.validation.Valid @RequestBody TalentRequest updateRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addOrUpdateTalent(token, updateRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addToFavourite")
    public ResponseEntity<BaseResponse> addToFavourite(
            @RequestBody TalentToFavRequest talentToFavRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addTalentToFavourite(token, talentToFavRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addTechAbility")
    public ResponseEntity<BaseResponse> addTechAbility(
            @RequestBody TechAbilityRequest techAbilityRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addTalentTechAbility(token, techAbilityRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addSoftAbility")
    public ResponseEntity<BaseResponse> addSoftAbility(
            @RequestBody SoftAbilityRequest softAbilityRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addTalentSoftAbility(token, softAbilityRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addOrUpdateExperience")
    public ResponseEntity<BaseResponse> addOrUpdateExperience(
            @RequestBody ExperienceRequest experienceRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addOrUpdateTalentExperience(token, experienceRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/deleteExperience")
    public ResponseEntity<BaseResponse> deleteExperience(
            @RequestBody DeleteRequest<Integer> experienceRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.deleteTalentExperience(token, experienceRequest.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addOrUpdateEducation")
    public ResponseEntity<BaseResponse> addOrUpdateEducation(
            @RequestBody EducationRequest educationRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addOrUpdateTalentEducation(token, educationRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/deleteEducation")
    public ResponseEntity<BaseResponse> deleteEducation(
            @RequestBody DeleteRequest<Integer> educationRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.deleteTalentEducation(token, educationRequest.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addOrUpdateLanguage")
    public ResponseEntity<BaseResponse> addOrUpdateLanguage(
            @RequestBody LanguageRequest languageRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addOrUpdateTalentLanguage(token, languageRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/deleteLanguage")
    public ResponseEntity<BaseResponse> deleteLanguage(
            @RequestBody DeleteRequest<Integer> languageRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.deleteTalentLanguage(token, languageRequest.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("deleteTechskill")
    public ResponseEntity<BaseResponse> deleteTechSkill(
            @RequestParam Integer targetId,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            BaseResponse bs = this.talentsService.removeTechnicalSkill(token, targetId);
            return ResponseEntity.ok(bs);
        } catch (Exception e) {
            BaseResponse bs = new BaseResponse(3, e.getMessage());
            return new ResponseEntity<>(bs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("deleteSoftskill")
    public ResponseEntity<BaseResponse> deleteSoftSkill(
            @RequestParam Integer targetId,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            BaseResponse bs = this.talentsService.removeSoftSkill(token, targetId);
            return ResponseEntity.ok(bs);
        } catch (Exception e) {
            BaseResponse bs = new BaseResponse(3, e.getMessage());
            return new ResponseEntity<>(bs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addOrUpdateFeedback")
    public ResponseEntity<BaseResponse> addFeedback(
            @jakarta.validation.Valid @RequestBody FeedbackRequest feedbackRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addOrUpdateTalentFeedback(token, feedbackRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/deleteFeedback")
    public ResponseEntity<BaseResponse> deleteFeedback(
            @RequestBody DeleteRequest<Integer> feedbackRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.deleteTalentFeedback(token, feedbackRequest.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/uploadTalentFile")
    public ResponseEntity<BaseResponse> uploadTalentFile(
            @RequestBody UploadTalentFileRequest uploadTalentFileRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.uploadTalentFile(token, uploadTalentFileRequest);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/updateTalentFile")
    public ResponseEntity<BaseResponse> updateTalentFile(
            @RequestBody UpdateTalentFileRequest updateTalentFileRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.updateTalentFile(token, updateTalentFileRequest);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/updateCvFile")
    public ResponseEntity<BaseResponse> updateCvFile(
            @RequestBody UpdateTalentFileRequest updateTalentFileRequest,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.updateCvFile(token, updateTalentFileRequest);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ─── Subida directa a S3 mediante URL pre-firmada ──────────────────────────

    @PostMapping("/file/upload-url")
    public ResponseEntity<TalentPresignedUrlResponse> generateUploadUrl(
            @RequestBody TalentUploadUrlRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            TalentPresignedUrlResponse response = talentsService.generateTalentUploadUrl(token, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TalentPresignedUrlResponse(new BaseResponse(3, e.getMessage()), null, null, null, false));
        }
    }

    @PostMapping("/file/confirm-upload")
    public ResponseEntity<BaseResponse> confirmUpload(
            @RequestBody TalentConfirmUploadRequest request,
            HttpServletRequest httpServletRequest) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.confirmTalentUpload(token, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/file/download-url")
    public ResponseEntity<TalentPresignedUrlResponse> generateDownloadUrl(
            @RequestBody TalentDownloadUrlRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            TalentPresignedUrlResponse response = talentsService.generateTalentDownloadUrl(token, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TalentPresignedUrlResponse(new BaseResponse(3, e.getMessage()), null, null, null, false));
        }
    }

    /**
     * URL PUT pre-firmada para la foto de perfil.
     *
     * No tiene confirm-upload: la foto es la columna TALENTO.RUTA_IMAGEN, no una
     * fila de TALENTO_ARCHIVOS. El frontend sube a S3 y luego manda la ruta en el
     * addOrUpdateTalent de siempre, igual que la firma de usuario.
     */
    @PostMapping("/photo/upload-url")
    public ResponseEntity<TalentPhotoUrlResponse> generatePhotoUploadUrl(
            @RequestBody TalentPhotoUrlRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(talentsService.generateTalentPhotoUploadUrl(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TalentPhotoUrlResponse(new BaseResponse(3, e.getMessage()), null, null, null));
        }
    }

    // Espacio solo para migración de archivos
    @GetMapping("/migration/profile")
    public void migrateProfilePhoto() {
        try {
            talentsService.migrateProfilePhoto();
        } catch (Exception e) {
            System.out.println("Error de migración: " + e.getMessage());
        }
    }

    @GetMapping("/migration/cv")
    public void migrateCV() {
        try {
            talentsService.migrateCV();
        } catch (Exception e) {
            System.out.println("Error de migración: " + e.getMessage());
        }
    }

}
