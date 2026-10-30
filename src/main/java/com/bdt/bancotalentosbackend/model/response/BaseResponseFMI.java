package com.bdt.bancotalentosbackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta base con el formato de AutFMI ({@code idTipoMensaje}, {@code mensaje},
 * {@code detalleMensaje}). La usan los endpoints migrados desde FMI (Entrevistas)
 * para no romper el contrato que espera el frontend ({@code BaseResponseFMI}).
 * El resto de BDT sigue con {@link BaseResponse} ({@code idMensaje}).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponseFMI {
    private Integer idTipoMensaje;
    private String mensaje;
    private String detalleMensaje;

    public BaseResponseFMI(Integer idTipoMensaje, String mensaje) {
        this.idTipoMensaje = idTipoMensaje;
        this.mensaje = mensaje;
    }

}
