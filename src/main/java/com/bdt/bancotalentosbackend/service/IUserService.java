package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.FavCollectionRequest;
import com.bdt.bancotalentosbackend.model.request.UpdateUserRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.UserFavListResponse;
import com.bdt.bancotalentosbackend.model.response.UserInfoResponse;

public interface IUserService {
    BaseResponse addFavouriteCollection (String token, FavCollectionRequest favCollectionRequest);
    UserInfoResponse getUserInfo(String token);
    BaseResponse updateUserInfo(String token, UpdateUserRequest updateUserRequest);
    UserFavListResponse getFavourites (String token);
}
