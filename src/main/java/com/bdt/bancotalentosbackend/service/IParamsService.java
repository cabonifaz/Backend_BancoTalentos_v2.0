package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.response.ParamsListResponse;

public interface IParamsService {
    ParamsListResponse getParamsList(String groupIdMaestros);
}
