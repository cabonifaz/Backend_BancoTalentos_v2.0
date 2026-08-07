package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.ClientAdminRequest;
import com.bdt.bancotalentosbackend.model.request.ClientGestorRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ClientAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.ClientGestorListResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.service.IClientService;
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
    private final IClientService clientService;

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
            return ResponseEntity.ok(clientService.list(token, filtro, idEstado, pagina));
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
            return ResponseEntity.ok(clientService.create(token, request));
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
            return ResponseEntity.ok(clientService.update(token, request));
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
            return ResponseEntity.ok(clientService.delete(token, idCliente));
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
            return ResponseEntity.ok(clientService.reactivate(token, idCliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /* ==================== Gestores por cliente (SUPERADMIN) ==================== */

    /** Gestores activos (0-2) de un cliente. */
    @GetMapping("/gestores")
    public ResponseEntity<ClientGestorListResponse> listGestores(
            @RequestParam Integer idCliente,
            HttpServletRequest httpServletRequest) {
        ClientGestorListResponse response = new ClientGestorListResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientService.listGestores(token, idCliente));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /** Asigna un gestor a un slot libre del cliente. */
    @PostMapping("/gestor")
    public ResponseEntity<BaseResponse> assignGestor(
            @RequestBody ClientGestorRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientService.assignGestor(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Cambia el usuario asignado a un slot existente. */
    @PutMapping("/gestor")
    public ResponseEntity<BaseResponse> changeGestor(
            @RequestBody ClientGestorRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientService.changeGestor(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Quita (baja lógica) un gestor de un slot. */
    @DeleteMapping("/gestor")
    public ResponseEntity<BaseResponse> removeGestor(
            @RequestParam Integer idClienteGestor,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientService.removeGestor(token, idClienteGestor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Intercambia las prioridades de los dos gestores del cliente. */
    @PutMapping("/gestores/swap")
    public ResponseEntity<BaseResponse> swapGestores(
            @RequestParam Integer idCliente,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(clientService.swapGestores(token, idCliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }
}
