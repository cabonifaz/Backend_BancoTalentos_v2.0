package com.bdt.bancotalentosbackend.controller;

import com.bdt.bancotalentosbackend.model.request.UserAdminRequest;
import com.bdt.bancotalentosbackend.model.request.UserSignatureUrlRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.UserAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.UserSignatureUrlResponse;
import com.bdt.bancotalentosbackend.service.IUserService;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-admin")
@RequiredArgsConstructor
@Tag(name = "Manejo de Usuarios (SUPERADMIN)")
public class UserAdminController {
    private final IUserService userService;

    /** Listado paginado con rol y estado. {@code pagina} nulo devuelve todo. */
    @GetMapping("/list")
    public ResponseEntity<UserAdminListResponse> list(
            @RequestParam @Nullable String filtro,
            @RequestParam @Nullable Integer idEstado,
            @RequestParam @Nullable Integer pagina,
            HttpServletRequest httpServletRequest) {
        UserAdminListResponse response = new UserAdminListResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(userService.listUsuariosAdmin(token, filtro, idEstado, pagina));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse> update(
            @RequestBody UserAdminRequest request,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(userService.updateUsuarioAdmin(token, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Baja lógica de un usuario. */
    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse> delete(
            @RequestParam Integer idUsuario,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(userService.deleteUsuarioAdmin(token, idUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** Reactivación de un usuario desactivado. */
    @PutMapping("/reactivate")
    public ResponseEntity<BaseResponse> reactivate(
            @RequestParam Integer idUsuario,
            HttpServletRequest httpServletRequest) {
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(userService.reactivateUsuarioAdmin(token, idUsuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse(3, e.getMessage()));
        }
    }

    /** URL PUT pre-firmada para subir la firma directamente a S3. */
    @PostMapping("/signature/upload-url")
    public ResponseEntity<UserSignatureUrlResponse> signatureUploadUrl(
            @RequestBody UserSignatureUrlRequest request,
            HttpServletRequest httpServletRequest) {
        UserSignatureUrlResponse response = new UserSignatureUrlResponse();
        try {
            String token = JWTHelper.extractToken(httpServletRequest);
            return ResponseEntity.ok(userService.generateSignatureUploadUrl(token, request));
        } catch (Exception e) {
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
