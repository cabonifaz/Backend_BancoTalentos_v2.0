package com.bdt.bancotalentosbackend.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertUpdateResponse extends BaseResponse{
    private Integer idNuevo;

    public InsertUpdateResponse(Integer idTipoMensaje, String mensaje, Integer idNuevo) {
        super(idTipoMensaje, mensaje);
        this.idNuevo = idNuevo;
    }
}
