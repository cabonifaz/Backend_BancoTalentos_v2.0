package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.TariffRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TariffListResponse;
import com.bdt.bancotalentosbackend.service.ITariffService;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tariff-admin")
@RequiredArgsConstructor
@Tag(name = "Manejo de Tarifario (SUPERADMIN)")
public class TariffAdminController {
    private final ITariffService tariffService;

    /** Listado paginado con nombres amigables. {@code pagina} nulo devuelve todo. */
    @GetMapping("/list")
    public ResponseEntity<TariffListResponse> list(
            @RequestParam @Nullable String filtro,
            @RequestParam @Nullable Integer pagina,
            HttpServletRequest httpServletRequest) {
        TariffListResponse response = new TariffListResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(tariffService.list(token, filtro, pagina));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> create(
            @RequestBody TariffRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(tariffService.create(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse> update(
            @RequestBody TariffRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(tariffService.update(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Baja lógica de una tarifa (sin reactivación). */
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
            @RequestParam Integer idTarifario,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(tariffService.delete(token, idTarifario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }
}
