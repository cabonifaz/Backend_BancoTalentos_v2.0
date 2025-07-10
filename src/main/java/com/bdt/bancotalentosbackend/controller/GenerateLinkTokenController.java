package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.LinkTokenRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.GenerateLinkResponse;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/link")
@RequiredArgsConstructor
@Tag(name = "Generar link token")
public class GenerateLinkTokenController {
    private final JWTHelper jwt;

    @PostMapping("/generate")
    public ResponseEntity<BaseResponse> generateLinkToken(
            HttpServletRequest httpServletRequest,
            @RequestBody LinkTokenRequest request
    ) {
        GenerateLinkResponse respuesta;

        try {
            String token = JWTHelper.extractToken(httpServletRequest);

            respuesta = new GenerateLinkResponse(
                    2,
                    "Se generó el enlace, correctamente",
                    jwt.generateLinkToken(request, token)
            );

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            respuesta = new GenerateLinkResponse(3, e.getMessage(), null);
            return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
