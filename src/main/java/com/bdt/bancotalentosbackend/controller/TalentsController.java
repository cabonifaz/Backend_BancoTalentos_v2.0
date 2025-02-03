package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.SearchRequest;
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
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/data")
    public ResponseEntity<TalentResponse> getTalent(@RequestParam Integer talentId, HttpServletRequest httpServletRequest) {
        TalentResponse response = new TalentResponse();

        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = talentsService.getTalentById(token, talentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(1, e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
