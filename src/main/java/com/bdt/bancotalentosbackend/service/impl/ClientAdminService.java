package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.ClientAdminRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ClientAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.repository.ClientAdminRepository;
import com.bdt.bancotalentosbackend.service.IClientAdminService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientAdminService implements IClientAdminService {
    private final ClientAdminRepository clientAdminRepository;
    private final JWTHelper jwt;

    @Override
    public ClientAdminListResponse list(String token, String filtro, Integer idEstado, Integer pagina) {
        return clientAdminRepository.list(buildBaseRequest(token), filtro, idEstado, pagina);
    }

    @Override
    public InsertUpdateResponse create(String token, ClientAdminRequest request) {
        return clientAdminRepository.create(buildBaseRequest(token), request);
    }

    @Override
    public BaseResponse update(String token, ClientAdminRequest request) {
        return clientAdminRepository.update(buildBaseRequest(token), request);
    }

    @Override
    public BaseResponse delete(String token, Integer idCliente) {
        return clientAdminRepository.delete(buildBaseRequest(token), idCliente);
    }

    @Override
    public BaseResponse reactivate(String token, Integer idCliente) {
        return clientAdminRepository.reactivate(buildBaseRequest(token), idCliente);
    }

    private BaseRequest buildBaseRequest(String token) {
        UserDTO user = jwt.decodeToken(token);
        return Common.createBaseRequest(user, Constante.MANEJO_CLIENTES);
    }
}
