package com.bdt.bancotalentosbackend.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackResponse extends BaseResponse{
    private Integer avgEstrellas;

    public FeedbackResponse(Integer idTipoMensaje, String mensaje, Integer avgEstrellas) {
        super(idTipoMensaje, mensaje);
        this.avgEstrellas = avgEstrellas;
    }
}
