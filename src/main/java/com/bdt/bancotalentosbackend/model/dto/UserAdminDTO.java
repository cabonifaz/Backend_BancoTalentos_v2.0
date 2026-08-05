package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminDTO {
    private Integer idUsuario;
    private Integer idEmpresa;
    private String nombres;
    private String apellidos;
    private String usuario;
    private String email;
    private String cargo;
    private String telefono;
    private String firma;
    private Integer idEstadoRegistro;
    private Integer idRol;
    private String rol;
}
