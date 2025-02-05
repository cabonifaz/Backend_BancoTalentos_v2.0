package com.bdt.bancotalentosbackend.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentUpdateRequest {
    private Integer idTalento;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String telefono;
    private String imagen;
    private String linkedin;
    private String github;
    private String descripcion;
    private String disponibilidad;
    private String puesto;
    private Integer idPais;
    private Integer idCiudad;
    private Double montoInicialPlanilla;
    private Double montoFinalPlanilla;
    private Double montoInicialRxH;
    private Double montoFinalRxH;
    private Integer idMoneda;
}
