package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.FavCollectionRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.UserFavListResponse;

public interface IUserService {
    BaseResponse addFavouriteCollection (String token, FavCollectionRequest favCollectionRequest);
    UserFavListResponse getFavourites (String token);
}
