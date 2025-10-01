package com.bdt.bancotalentosbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bdt.bancotalentosbackend.model.request.AnalyzeCVRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.IACVResponse;
import com.bdt.bancotalentosbackend.service.impl.IAService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ia")
@RequiredArgsConstructor
@Tag(name = "IA Services Controller")
public class IAController {

    private final IAService iAService;

    @PostMapping("/analyze/cv")
    public ResponseEntity<IACVResponse> analyzeCv(@RequestBody AnalyzeCVRequest request,
            HttpServletRequest httpServletRequest) {
        BaseResponse baseResponse = new BaseResponse();
        IACVResponse iacvResponse = new IACVResponse();
        try {
            iacvResponse = iAService.analyzeText(request.getExtractedText());
            baseResponse.setIdMensaje(2);
            baseResponse.setMensaje("CV analizado con éxito");
            iacvResponse.setResult(baseResponse);
            return ResponseEntity.ok().body(iacvResponse);
        } catch (Exception e) {
            baseResponse.setIdMensaje(3);
            baseResponse.setMensaje("Hubo un error al extraer la información del CV");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(iacvResponse);
        }
    }

}
