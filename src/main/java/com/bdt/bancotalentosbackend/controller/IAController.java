package com.bdt.bancotalentosbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bdt.bancotalentosbackend.model.request.AIPromptRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.PromptResponse;
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

    @PostMapping(value = "/analyze-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse> analyzeCv(
            @RequestPart("file") MultipartFile file) {
        var response = iAService.analyzeCv(file);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/prompt")
    public ResponseEntity<BaseResponse> prompt(
            @RequestBody AIPromptRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            PromptResponse response = (PromptResponse) iAService.prompt(request);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            BaseResponse baseResponse = new BaseResponse(3, "Hubo un error al procesar el prompt");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(baseResponse);
        }
    }

}
