package com.bdt.bancotalentosbackend.model.response;

import lombok.*;

@Getter
@Setter
public class GenerateLinkResponse extends BaseResponse{
    private String linkToken;

    public GenerateLinkResponse(Integer idTipoMensaje, String mensaje, String linkToken) {
        super(idTipoMensaje, mensaje);
        this.linkToken = linkToken;
    }
}
