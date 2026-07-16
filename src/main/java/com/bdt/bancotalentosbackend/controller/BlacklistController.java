package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.BlacklistCreateRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistRemoveRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistUpdateRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistHistoryResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistListResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistValidateResponse;
import com.bdt.bancotalentosbackend.service.IBlacklistService;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blacklist")
@RequiredArgsConstructor
@Tag(name = "Lista Negra")
public class BlacklistController {
    private final IBlacklistService blacklistService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse> createBlacklist(
            @RequestBody BlacklistCreateRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(blacklistService.createBlacklist(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse> updateBlacklist(
            @RequestBody BlacklistUpdateRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(blacklistService.updateBlacklist(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /**
     * Baja lógica de una restricción concreta. El motivo viaja en el body (y no
     * como query param) porque es texto libre de hasta 1000 caracteres: es el
     * motivo de la baja, que queda en el historial.
     *
     * Para levantar una restricción global conservándola solo para ciertos
     * clientes, el frontend llama aquí y luego a /create por cada cliente que
     * se conserva.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<BaseResponse> removeBlacklist(
            @RequestBody BlacklistRemoveRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(blacklistService.removeBlacklist(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /**
     * Listado paginado por talento. {@code pagina} nulo devuelve todo.
     */
    @GetMapping("/list")
    public ResponseEntity<BlacklistListResponse> listBlacklist(
            @RequestParam @Nullable String nombre,
            @RequestParam @Nullable Integer idCliente,
            @RequestParam @Nullable Integer pagina,
            HttpServletRequest httpServletRequest) {
        BlacklistListResponse response = new BlacklistListResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(blacklistService.listBlacklist(token, nombre, idCliente, pagina));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Historial paginado por movimiento. {@code pagina} nulo devuelve todo.
     */
    @GetMapping("/history")
    public ResponseEntity<BlacklistHistoryResponse> listBlacklistHistory(
            @RequestParam Integer idTalento,
            @RequestParam @Nullable Integer idCliente,
            @RequestParam @Nullable Integer pagina,
            HttpServletRequest httpServletRequest) {
        BlacklistHistoryResponse response = new BlacklistHistoryResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(blacklistService.listBlacklistHistory(token, idTalento, idCliente, pagina));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Valida si un talento está restringido para el cliente del requerimiento,
     * al asignarlo. Devuelve la restricción que aplica (global o del cliente)
     * para que la pantalla pueda mostrar cliente y motivo.
     */
    @GetMapping("/validate")
    public ResponseEntity<BlacklistValidateResponse> validateBlacklist(
            @RequestParam Integer idTalento,
            @RequestParam Integer idRequerimiento,
            HttpServletRequest httpServletRequest) {
        BlacklistValidateResponse response = new BlacklistValidateResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(blacklistService.validateBlacklist(token, idTalento, idRequerimiento));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
