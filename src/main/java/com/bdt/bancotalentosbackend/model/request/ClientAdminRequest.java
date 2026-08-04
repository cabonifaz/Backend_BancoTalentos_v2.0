package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alta/edición de un cliente (SUPERADMIN). En el alta {@code idCliente} viaja nulo.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientAdminRequest {
    private Integer idCliente;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String ubicacion;
    private String direccionExacta;
}
