package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.TariffRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TariffListResponse;
import com.bdt.bancotalentosbackend.repository.TariffRepository;
import com.bdt.bancotalentosbackend.service.ITariffService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TariffService implements ITariffService {
    private final TariffRepository tariffRepository;
    private final JWTHelper jwt;

    @Override
    public TariffListResponse list(String token, String filtro, Integer pagina) {
        return tariffRepository.list(buildBaseRequest(token), filtro, pagina);
    }

    @Override
    public BaseResponse create(String token, TariffRequest request) {
        return tariffRepository.create(buildBaseRequest(token), request);
    }

    @Override
    public BaseResponse update(String token, TariffRequest request) {
        return tariffRepository.update(buildBaseRequest(token), request);
    }

    @Override
    public BaseResponse delete(String token, Integer idTarifario) {
        return tariffRepository.delete(buildBaseRequest(token), idTarifario);
    }

    private BaseRequest buildBaseRequest(String token) {
        UserDTO user = jwt.decodeToken(token);
        return Common.createBaseRequest(user, Constante.MANEJO_TARIFARIO);
    }
}
