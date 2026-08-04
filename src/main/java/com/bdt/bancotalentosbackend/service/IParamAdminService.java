package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.ParamAdminRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.model.response.ParamItemListResponse;
import com.bdt.bancotalentosbackend.model.response.ParamMasterListResponse;

public interface IParamAdminService {
    ParamMasterListResponse listMasters(String token, String filtro, Integer pagina);

    ParamItemListResponse listByMaster(String token, Integer idMaestro);

    InsertUpdateResponse create(String token, ParamAdminRequest request);

    BaseResponse update(String token, ParamAdminRequest request);

    BaseResponse delete(String token, Integer idParametro);
}
