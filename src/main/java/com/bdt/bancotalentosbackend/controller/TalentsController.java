package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.FileResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.service.impl.TalentsService;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("bdt/talent")
@RequiredArgsConstructor
public class TalentsController {
    private final TalentsService talentsService;

    @GetMapping("/list")
    public ResponseEntity<TalentsListResponse> getTalents(
            @RequestParam @Nullable Integer nPag,
            @RequestParam @Nullable String search,
            @RequestParam @Nullable String techAbilities,
            @RequestParam @Nullable Integer idEnglishLevel,
            @RequestParam @Nullable Integer idTalentCollection,
            HttpServletRequest httpServletRequest
    ) {
        TalentsListResponse response = new TalentsListResponse();
        SearchRequest searchRequest = new SearchRequest(nPag, search, techAbilities, idEnglishLevel, idTalentCollection);

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
            HttpServletRequest httpServletRequest
    ) {
        TalentResponse response = new TalentResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.getTalentById(token, talentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(1, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/file")
    public ResponseEntity<FileResponse> getTalentFile(
            @RequestParam String filePath,
            HttpServletRequest httpServletRequest
    ) {
        FileResponse response = new FileResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.getTalentFile(token, filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(1, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addOrUpdateTalent")
    public ResponseEntity<BaseResponse> addOrUpdateTalent(
            @RequestBody TalentRequest updateRequest,
            HttpServletRequest httpServletRequest
    ) {
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
            HttpServletRequest httpServletRequest
    ) {
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
            HttpServletRequest httpServletRequest
    ) {
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
            HttpServletRequest httpServletRequest
    ) {
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
            HttpServletRequest httpServletRequest
    ) {
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
            @RequestBody Integer idExperiencia,
            HttpServletRequest httpServletRequest
    ) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.deleteTalentExperience(token, idExperiencia);
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
            HttpServletRequest httpServletRequest
    ) {
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
            @RequestBody Integer idEducation,
            HttpServletRequest httpServletRequest
    ) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.deleteTalentEducation(token, idEducation);
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
            HttpServletRequest httpServletRequest
    ) {
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
            @RequestBody Integer idLanguage,
            HttpServletRequest httpServletRequest
    ) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.deleteTalentLanguage(token, idLanguage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addOrUpdateFeedback")
    public ResponseEntity<BaseResponse> addFeedback(
            @RequestBody FeedbackRequest feedbackRequest,
            HttpServletRequest httpServletRequest
    ) {
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
            @RequestBody Integer idFeedback,
            HttpServletRequest httpServletRequest
    ) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.deleteTalentFeedback(token, idFeedback);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    //    Espacio solo para migración de archivos
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
