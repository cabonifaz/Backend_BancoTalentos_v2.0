package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.ClientAdminRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ClientAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;

public interface IClientAdminService {
    ClientAdminListResponse list(String token, String filtro, Integer idEstado, Integer pagina);

    InsertUpdateResponse create(String token, ClientAdminRequest request);

    BaseResponse update(String token, ClientAdminRequest request);

    BaseResponse delete(String token, Integer idCliente);

    BaseResponse reactivate(String token, Integer idCliente);
}
