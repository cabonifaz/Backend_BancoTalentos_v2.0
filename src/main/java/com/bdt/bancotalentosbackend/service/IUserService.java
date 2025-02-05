package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.AddFavCollectionRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;

public interface IUserService {
    BaseResponse addFavouriteCollection (String token, AddFavCollectionRequest addFavCollectionRequest);
}
