package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.TariffRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TariffListResponse;

public interface ITariffService {
    TariffListResponse list(String token, String filtro, Integer pagina);

    BaseResponse create(String token, TariffRequest request);

    BaseResponse update(String token, TariffRequest request);

    BaseResponse delete(String token, Integer idTarifario);
}
