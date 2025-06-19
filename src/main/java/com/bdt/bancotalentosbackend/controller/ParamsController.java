package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ParamsListResponse;
import com.bdt.bancotalentosbackend.service.impl.ParamsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("bdt/params")
@RequiredArgsConstructor
@Tag(name = "Parametro")
public class ParamsController {
    private final ParamsService paramsService;

    @GetMapping
    public ResponseEntity<ParamsListResponse> getParams(@RequestParam String groupIdMaestros) {
        ParamsListResponse response = new ParamsListResponse();
        try {
            response = paramsService.getParamsList(groupIdMaestros);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            response.setResponse(new BaseResponse(3, e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
