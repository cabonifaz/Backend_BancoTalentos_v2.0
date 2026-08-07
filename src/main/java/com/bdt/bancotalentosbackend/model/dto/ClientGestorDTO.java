package com.bdt.bancotalentosbackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Gestor asignado a un cliente (una fila de CLIENTE_GESTOR + datos del usuario). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientGestorDTO {
    private Integer idClienteGestor;
    private Integer idCliente;
    private Integer idUsuario;
    private Integer prioridad;
    private String usuario;
    private String nombres;
    private String apellidos;
}
