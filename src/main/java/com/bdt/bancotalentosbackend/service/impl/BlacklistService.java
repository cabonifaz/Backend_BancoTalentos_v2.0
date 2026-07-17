package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistCreateRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistRemoveRequest;
import com.bdt.bancotalentosbackend.model.request.BlacklistUpdateRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistHistoryResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistListResponse;
import com.bdt.bancotalentosbackend.model.response.BlacklistValidateResponse;
import com.bdt.bancotalentosbackend.repository.BlacklistRepository;
import com.bdt.bancotalentosbackend.service.IBlacklistService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlacklistService implements IBlacklistService {
    private final BlacklistRepository blacklistRepository;
    private final JWTHelper jwt;

    @Override
    public BaseResponse createBlacklist(String token, BlacklistCreateRequest request) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTA_NEGRA);
        return blacklistRepository.createBlacklist(baseRequest, request);
    }

    @Override
    public BaseResponse updateBlacklist(String token, BlacklistUpdateRequest request) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTA_NEGRA);
        return blacklistRepository.updateBlacklist(baseRequest, request);
    }

    @Override
    public BaseResponse removeBlacklist(String token, BlacklistRemoveRequest request) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTA_NEGRA);
        return blacklistRepository.removeBlacklist(baseRequest, request);
    }

    @Override
    public BlacklistListResponse listBlacklist(String token, String nombreTalento, Integer idCliente, Integer pagina) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTA_NEGRA);
        return blacklistRepository.listBlacklist(baseRequest, nombreTalento, idCliente, pagina);
    }

    @Override
    public BlacklistHistoryResponse listBlacklistHistory(String token, Integer idTalento, Integer idCliente,
                                                         Integer pagina) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTA_NEGRA);
        return blacklistRepository.listBlacklistHistory(baseRequest, idTalento, idCliente, pagina);
    }

    @Override
    public BlacklistValidateResponse validateBlacklist(String token, Integer idTalento, Integer idRequerimiento) {
        UserDTO user = jwt.decodeToken(token);
        BaseRequest baseRequest = Common.createBaseRequest(user, Constante.LISTA_NEGRA);
        return blacklistRepository.validateBlacklist(baseRequest, idTalento, idRequerimiento);
    }
}
