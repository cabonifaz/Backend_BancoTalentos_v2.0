package com.bdt.bancotalentosbackend.service.impl;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.ClientAdminRequest;
import com.bdt.bancotalentosbackend.model.request.ClientGestorRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ClientAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.ClientGestorListResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.repository.ClientRepository;
import com.bdt.bancotalentosbackend.service.IClientService;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService implements IClientService {
    private final ClientRepository clientRepository;
    private final JWTHelper jwt;

    @Override
    public ClientAdminListResponse list(String token, String filtro, Integer idEstado, Integer pagina) {
        return clientRepository.list(buildBaseRequest(token), filtro, idEstado, pagina);
    }

    @Override
    public InsertUpdateResponse create(String token, ClientAdminRequest request) {
        return clientRepository.create(buildBaseRequest(token), request);
    }

    @Override
    public BaseResponse update(String token, ClientAdminRequest request) {
        return clientRepository.update(buildBaseRequest(token), request);
    }

    @Override
    public BaseResponse delete(String token, Integer idCliente) {
        return clientRepository.delete(buildBaseRequest(token), idCliente);
    }

    @Override
    public BaseResponse reactivate(String token, Integer idCliente) {
        return clientRepository.reactivate(buildBaseRequest(token), idCliente);
    }

    /* ==================== Gestores por cliente (funcionalidad 2045) ==================== */

    @Override
    public ClientGestorListResponse listGestores(String token, Integer idCliente) {
        return clientRepository.listGestores(buildGestorBaseRequest(token), idCliente);
    }

    @Override
    public BaseResponse assignGestor(String token, ClientGestorRequest request) {
        return clientRepository.assignGestor(buildGestorBaseRequest(token), request);
    }

    @Override
    public BaseResponse changeGestor(String token, ClientGestorRequest request) {
        return clientRepository.changeGestor(buildGestorBaseRequest(token), request);
    }

    @Override
    public BaseResponse removeGestor(String token, Integer idClienteGestor) {
        return clientRepository.removeGestor(buildGestorBaseRequest(token), idClienteGestor);
    }

    @Override
    public BaseResponse swapGestores(String token, Integer idCliente) {
        return clientRepository.swapGestores(buildGestorBaseRequest(token), idCliente);
    }

    private BaseRequest buildBaseRequest(String token) {
        UserDTO user = jwt.decodeToken(token);
        return Common.createBaseRequest(user, Constante.MANEJO_CLIENTES);
    }

    private BaseRequest buildGestorBaseRequest(String token) {
        UserDTO user = jwt.decodeToken(token);
        return Common.createBaseRequest(user, Constante.MANEJO_GESTORES);
    }
}
