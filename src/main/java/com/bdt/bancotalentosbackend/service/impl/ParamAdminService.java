package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.ParamAdminRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.model.response.ParamItemListResponse;
import com.bdt.bancotalentosbackend.model.response.ParamMasterListResponse;
import com.bdt.bancotalentosbackend.repository.ParamAdminRepository;
import com.bdt.bancotalentosbackend.service.IParamAdminService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParamAdminService implements IParamAdminService {
    private final ParamAdminRepository paramAdminRepository;
    private final JWTHelper jwt;

    @Override
    public ParamMasterListResponse listMasters(String token, String filtro, Integer pagina) {
        BaseRequest baseRequest = buildBaseRequest(token);
        return paramAdminRepository.listMasters(baseRequest, filtro, pagina);
    }

    @Override
    public ParamItemListResponse listByMaster(String token, Integer idMaestro, Integer pagina) {
        BaseRequest baseRequest = buildBaseRequest(token);
        return paramAdminRepository.listByMaster(baseRequest, idMaestro, pagina);
    }

    @Override
    public InsertUpdateResponse create(String token, ParamAdminRequest request) {
        BaseRequest baseRequest = buildBaseRequest(token);
        return paramAdminRepository.create(baseRequest, request);
    }

    @Override
    public BaseResponse update(String token, ParamAdminRequest request) {
        BaseRequest baseRequest = buildBaseRequest(token);
        return paramAdminRepository.update(baseRequest, request);
    }

    @Override
    public BaseResponse delete(String token, Integer idParametro) {
        BaseRequest baseRequest = buildBaseRequest(token);
        return paramAdminRepository.delete(baseRequest, idParametro);
    }

    private BaseRequest buildBaseRequest(String token) {
        UserDTO user = jwt.decodeToken(token);
        return Common.createBaseRequest(user, Constante.MANEJO_PARAMETROS);
    }
}
