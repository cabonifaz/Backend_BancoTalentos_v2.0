package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientAdminDTO {
    private Integer idCliente;
    private Integer idEmpresa;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String ubicacion;
    private String direccionExacta;
    private String usucre;
    private String fchcre;
    private String usumod;
    private String fchmod;
    private Integer idEstadoRegistro;
}
