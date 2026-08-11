package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Alta de un usuario por el SUPERADMIN. La clave viaja en texto plano y el SP la
 * cifra con HASHBYTES SHA2_256. El rol SUPERADMIN nunca es asignable (validado en SP).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAdminCreateRequest {
    private String nombres;
    private String apellidos;
    private String usuario;
    private String clave;
    private String email;
    private String telefono;
    private String cargo;
    private Integer idTipoRol;
}
