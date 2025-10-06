package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.TechSkillRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.NewTechSkillReponse;
import com.bdt.bancotalentosbackend.service.impl.TechSkillService;
import com.bdt.bancotalentosbackend.util.JWTHelper;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "Skills")
@RequestMapping("/skills")
public class SkillsController {

    @Autowired
    private TechSkillService techSkillService;

    @PostMapping("/techskills/create")
    public ResponseEntity<NewTechSkillReponse> createTechSkill(@RequestBody TechSkillRequest request,
            HttpServletRequest httpServletRequest) {
        NewTechSkillReponse response = new NewTechSkillReponse();
        BaseResponse baseResponse = new BaseResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            response = techSkillService.createTechSkill(request, token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            baseResponse.setIdMensaje(3);
            baseResponse.setMensaje("Error al crear la habilidad técnica");
            response.setBaseResponse(baseResponse);
            return ResponseEntity.badRequest().body(response);
        }
    }
}