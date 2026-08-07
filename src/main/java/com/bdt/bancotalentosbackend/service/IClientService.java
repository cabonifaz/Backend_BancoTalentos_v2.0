package com.bdt.bancotalentosbackend.service;

import com.bdt.bancotalentosbackend.model.request.ClientAdminRequest;
import com.bdt.bancotalentosbackend.model.request.ClientGestorRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ClientAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.ClientGestorListResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;

public interface IClientService {
    ClientAdminListResponse list(String token, String filtro, Integer idEstado, Integer pagina);

    InsertUpdateResponse create(String token, ClientAdminRequest request);

    BaseResponse update(String token, ClientAdminRequest request);

    BaseResponse delete(String token, Integer idCliente);

    BaseResponse reactivate(String token, Integer idCliente);

    // ===== Gestores por cliente (funcionalidad 2045) =====
    ClientGestorListResponse listGestores(String token, Integer idCliente);
    BaseResponse assignGestor(String token, ClientGestorRequest request);
    BaseResponse changeGestor(String token, ClientGestorRequest request);
    BaseResponse removeGestor(String token, Integer idClienteGestor);
    BaseResponse swapGestores(String token, Integer idCliente);
}
