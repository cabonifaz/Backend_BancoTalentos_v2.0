package com.bdt.bancotalentosbackend.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentRequest {
    @JsonProperty()
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
    @JsonProperty()
    private List<TechAbilityRequest> habilidadesTecnicas;
    @JsonProperty()
    private List<SoftAbilityRequest> habilidadesBlandas;
    @JsonProperty()
    private List<ExperienceRequest> experiencias;
    @JsonProperty()
    private List<EducationRequest> educaciones;
    @JsonProperty()
    private List<LanguageRequest> idiomas;
}
