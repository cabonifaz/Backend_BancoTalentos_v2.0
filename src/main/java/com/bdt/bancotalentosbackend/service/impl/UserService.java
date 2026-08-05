package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.FavCollectionRequest;
import com.bdt.bancotalentosbackend.model.request.UpdateUserRequest;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.UserAdminRequest;
import com.bdt.bancotalentosbackend.model.request.UserSignatureUrlRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.UserAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.UserFavListResponse;
import com.bdt.bancotalentosbackend.model.response.UserInfoResponse;
import com.bdt.bancotalentosbackend.model.response.UserSignatureUrlResponse;
import com.bdt.bancotalentosbackend.repository.UserRepository;
import com.bdt.bancotalentosbackend.service.IUserService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import com.bdt.bancotalentosbackend.util.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final JWTHelper jwt;

    @Override
    public BaseResponse addFavouriteCollection(String token, FavCollectionRequest favCollectionRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);

        return userRepository.addFavouriteCollection(baseRequest, favCollectionRequest);
    }

    @Override
    public UserFavListResponse getFavourites(String token) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_TALENTO);

        return userRepository.getFavourites(baseRequest);
    }

    @Override
    public UserInfoResponse getUserInfo(String token) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_USUARIO);

        return userRepository.getUserInfo(baseRequest);
    }

    @Override
    public BaseResponse updateUserInfo(String token, UpdateUserRequest updateUserRequest) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.ACTUALIZAR_USUARIO);

        return userRepository.updateUserInfo(baseRequest, updateUserRequest);
    }

    /* ==================== Administración de usuarios (SUPERADMIN) ==================== */

    @Override
    public UserAdminListResponse listUsuariosAdmin(String token, String filtro, Integer idEstado, Integer pagina) {
        return userRepository.listUsuariosAdmin(buildAdminBaseRequest(token), filtro, idEstado, pagina);
    }

    @Override
    public BaseResponse updateUsuarioAdmin(String token, UserAdminRequest request) {
        return userRepository.updateUsuarioAdmin(buildAdminBaseRequest(token), request);
    }

    @Override
    public BaseResponse deleteUsuarioAdmin(String token, Integer idUsuario) {
        return userRepository.deleteUsuarioAdmin(buildAdminBaseRequest(token), idUsuario);
    }

    @Override
    public BaseResponse reactivateUsuarioAdmin(String token, Integer idUsuario) {
        return userRepository.reactivateUsuarioAdmin(buildAdminBaseRequest(token), idUsuario);
    }

    /**
     * Genera la URL PUT pre-firmada para la firma. El resultado de la subida lo
     * determina SOLO el status del PUT a S3; luego el frontend envía {@code path}
     * como FIRMA al actualizar. No usa confirm-upload.
     */
    @Override
    public UserSignatureUrlResponse generateSignatureUploadUrl(String token, UserSignatureUrlRequest request) {
        UserSignatureUrlResponse response = new UserSignatureUrlResponse();

        if (request.getIdUsuario() == null) {
            response.setBaseResponse(new BaseResponse(3, "Usuario inválido"));
            return response;
        }
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            response.setBaseResponse(new BaseResponse(3, "Nombre de archivo inválido"));
            return response;
        }

        String cleanName = request.getFileName().trim().replaceAll("\\s+", "_");
        String folder = Constante.RUTA_REPOSITORIO_FIRMA_USUARIO.replace("[ID]", request.getIdUsuario().toString());
        String s3Path = folder + System.currentTimeMillis() + "_" + cleanName;

        String contentType = request.getContentType() != null && !request.getContentType().isEmpty()
                ? request.getContentType()
                : "application/octet-stream";

        String url = S3Utils.getUploadSignedUrl(s3Path, contentType, 15);
        if (url == null || url.isEmpty()) {
            response.setBaseResponse(new BaseResponse(3, "No se pudo generar la URL de carga"));
            return response;
        }

        response.setBaseResponse(new BaseResponse(2, "URL generada"));
        response.setUrl(url);
        response.setPath(s3Path);
        return response;
    }

    private BaseRequest buildAdminBaseRequest(String token) {
        UserDTO user = jwt.decodeToken(token);
        return Common.createBaseRequest(user, Constante.MANEJO_USUARIOS);
    }
}
