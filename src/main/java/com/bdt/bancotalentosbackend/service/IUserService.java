package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.FavCollectionRequest;
import com.bdt.bancotalentosbackend.model.request.UpdateUserRequest;
import com.bdt.bancotalentosbackend.model.request.UserAdminRequest;
import com.bdt.bancotalentosbackend.model.request.UserAdminCreateRequest;
import com.bdt.bancotalentosbackend.model.request.UserSignatureUrlRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.model.response.UserAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.UserFavListResponse;
import com.bdt.bancotalentosbackend.model.response.UserInfoResponse;
import com.bdt.bancotalentosbackend.model.response.UserSignatureUrlResponse;

public interface IUserService {
    BaseResponse addFavouriteCollection (String token, FavCollectionRequest favCollectionRequest);
    UserInfoResponse getUserInfo(String token);
    BaseResponse updateUserInfo(String token, UpdateUserRequest updateUserRequest);
    UserFavListResponse getFavourites (String token);

    // ===== Administración de usuarios (SUPERADMIN) =====
    UserAdminListResponse listUsuariosAdmin(String token, String filtro, Integer idEstado, Integer pagina);
    InsertUpdateResponse createUsuarioAdmin(String token, UserAdminCreateRequest request);
    BaseResponse updateUsuarioAdmin(String token, UserAdminRequest request);
    BaseResponse deleteUsuarioAdmin(String token, Integer idUsuario);
    BaseResponse reactivateUsuarioAdmin(String token, Integer idUsuario);
    UserSignatureUrlResponse generateSignatureUploadUrl(String token, UserSignatureUrlRequest request);
}
