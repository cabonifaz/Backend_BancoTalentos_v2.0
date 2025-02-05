package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.AddFavCollectionRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.UserFavListResponse;

public interface IUserService {
    BaseResponse addFavouriteCollection (String token, AddFavCollectionRequest addFavCollectionRequest);
    UserFavListResponse getFavourites (String token);
}
