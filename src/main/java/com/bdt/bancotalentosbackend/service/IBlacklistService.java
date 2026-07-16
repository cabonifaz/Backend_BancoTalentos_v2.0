package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.BlacklistCreateRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistUpdateRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.request.BlacklistRemoveRequest;
import com.bdt.bancotalentosbackend.model.response.BlacklistHistoryResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistListResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistValidateResponse;

public interface IBlacklistService {
    BaseResponse createBlacklist(String token, BlacklistCreateRequest request);

    BaseResponse updateBlacklist(String token, BlacklistUpdateRequest request);

    BaseResponse removeBlacklist(String token, BlacklistRemoveRequest request);

    BlacklistListResponse listBlacklist(String token, String nombreTalento, Integer idCliente, Integer pagina);

    BlacklistHistoryResponse listBlacklistHistory(String token, Integer idTalento, Integer idCliente, Integer pagina);

    BlacklistValidateResponse validateBlacklist(String token, Integer idTalento, Integer idRequerimiento);
}
