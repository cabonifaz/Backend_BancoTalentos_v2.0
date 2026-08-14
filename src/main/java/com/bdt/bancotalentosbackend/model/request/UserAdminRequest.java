package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Edición de un usuario por el SUPERADMIN. No incluye USUARIO (no editable).
 * {@code firma} es la ruta S3 ya subida; si viaja nula, la firma actual se conserva.
 * {@code clave} opcional en texto plano (el SP la cifra SHA2_256); nula = no cambia.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminRequest {
    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String email;
    private String cargo;
    private String telefono;
    private String firma;
    private String clave;
    private Integer idTipoRol;
}
