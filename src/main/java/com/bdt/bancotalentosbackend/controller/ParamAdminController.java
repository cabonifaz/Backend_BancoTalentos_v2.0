package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.ParamAdminRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.model.response.ParamItemListResponse;
import com.bdt.bancotalentosbackend.model.response.ParamMasterListResponse;
import com.bdt.bancotalentosbackend.service.IParamAdminService;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/param-admin")
@RequiredArgsConstructor
@Tag(name = "Manejo de Parámetros (SUPERADMIN)")
public class ParamAdminController {
    private final IParamAdminService paramAdminService;

    /** Modo 1: lista de maestros paginada por maestro. {@code pagina} nulo devuelve todo. */
    @GetMapping("/masters")
    public ResponseEntity<ParamMasterListResponse> listMasters(
            @RequestParam @Nullable String filtro,
            @RequestParam @Nullable Integer pagina,
            HttpServletRequest httpServletRequest) {
        ParamMasterListResponse response = new ParamMasterListResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(paramAdminService.listMasters(token, filtro, pagina));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Modo 2: parámetros de un maestro, paginados. {@code pagina} nulo devuelve todo. */
    @GetMapping("/list")
    public ResponseEntity<ParamItemListResponse> listByMaster(
            @RequestParam Integer idMaestro,
            @RequestParam @Nullable Integer pagina,
            HttpServletRequest httpServletRequest) {
        ParamItemListResponse response = new ParamItemListResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(paramAdminService.listByMaster(token, idMaestro, pagina));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<InsertUpdateResponse> create(
            @RequestBody ParamAdminRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(paramAdminService.create(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InsertUpdateResponse(3, e.getMessage(), null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse> update(
            @RequestBody ParamAdminRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(paramAdminService.update(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Baja lógica de un parámetro. */
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
            @RequestParam Integer idParametro,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(paramAdminService.delete(token, idParametro));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }
}
