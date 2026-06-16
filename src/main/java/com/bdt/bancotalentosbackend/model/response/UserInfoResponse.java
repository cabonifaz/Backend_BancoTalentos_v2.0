package com.bdt.bancotalentosbackend.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private String usuario;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
}
