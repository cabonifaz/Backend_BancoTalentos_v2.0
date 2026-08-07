package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Asignación/cambio de un gestor. En el alta viajan {@code idCliente}, {@code idUsuario} y
 * {@code prioridad}; en el cambio de slot viajan {@code idClienteGestor} y {@code idUsuario}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientGestorRequest {
    private Integer idClienteGestor;
    private Integer idCliente;
    private Integer idUsuario;
    private Integer prioridad;
}
