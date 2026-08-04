package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.ClientAdminRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ClientAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.service.IClientAdminService;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client-admin")
@RequiredArgsConstructor
@Tag(name = "Manejo de Clientes (SUPERADMIN)")
public class ClientAdminController {
    private final IClientAdminService clientAdminService;

    /** Listado paginado. {@code pagina} nulo devuelve todo; {@code idEstado} nulo trae activos e inactivos. */
    @GetMapping("/list")
    public ResponseEntity<ClientAdminListResponse> list(
            @RequestParam @Nullable String filtro,
            @RequestParam @Nullable Integer idEstado,
            @RequestParam @Nullable Integer pagina,
            HttpServletRequest httpServletRequest) {
        ClientAdminListResponse response = new ClientAdminListResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientAdminService.list(token, filtro, idEstado, pagina));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<InsertUpdateResponse> create(
            @RequestBody ClientAdminRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientAdminService.create(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InsertUpdateResponse(3, e.getMessage(), null));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse> update(
            @RequestBody ClientAdminRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientAdminService.update(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Baja lógica de un cliente. */
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
            @RequestParam Integer idCliente,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientAdminService.delete(token, idCliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Reactivación de un cliente dado de baja. */
    @PutMapping("/reactivate")
    public ResponseEntity<BaseResponse> reactivate(
            @RequestParam Integer idCliente,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientAdminService.reactivate(token, idCliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }
}
