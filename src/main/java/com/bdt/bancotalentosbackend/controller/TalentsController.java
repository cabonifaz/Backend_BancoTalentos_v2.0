package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
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

        try{
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.getTalents(token, searchRequest);
            return ResponseEntity.ok(response);
        }catch (Exception e){
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

    @PostMapping("/update")
    public ResponseEntity<BaseResponse> updateTalent(
            @RequestBody TalentUpdateRequest updateRequest,
            HttpServletRequest httpServletRequest
    ) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.updateTalent(token, updateRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addToFavourite")
    public ResponseEntity<BaseResponse> addToFavourite(
            @RequestBody AddTalentToFavRequest addTalentToFavRequest,
            HttpServletRequest httpServletRequest
    ) {
        BaseResponse response = new BaseResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.addTalentToFavourite(token, addTalentToFavRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setIdMensaje(3);
            response.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/addTechAbility")
    public ResponseEntity<BaseResponse> addTechAbility(
            @RequestBody AddTalentTechAbilityRequest techAbilityRequest,
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
            @RequestBody AddTalentSoftAbilityRequest softAbilityRequest,
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
}
