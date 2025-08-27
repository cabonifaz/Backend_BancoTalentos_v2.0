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
    @JsonProperty()
    private String dni;
    @JsonProperty()
    private String nombres;
    @JsonProperty()
    private String apellidoPaterno;
    @JsonProperty()
    private String apellidoMaterno;
    @JsonProperty()
    private String email;
    @JsonProperty()
    private String telefono;
    @JsonProperty()
    private FileRequest cvArchivo;
    @JsonProperty()
    private FileRequest fotoArchivo;
    @JsonProperty()
    private String linkedin;
    @JsonProperty()
    private String github;
    @JsonProperty()
    private String descripcion;
    @JsonProperty()
    private String disponibilidad;
    @JsonProperty()
    private String puesto;
    @JsonProperty()
    private Integer idPais;
    @JsonProperty()
    private Integer idCiudad;
    @JsonProperty()
    private Integer idModalidadFacturacion;
    @JsonProperty()
    private Double montoInicialPlanilla;
    @JsonProperty()
    private Double montoFinalPlanilla;
    @JsonProperty()
    private Double montoInicialRxH;
    @JsonProperty()
    private Double montoFinalRxH;
    @JsonProperty()
    private Integer idMoneda;
    @JsonProperty()
    private Boolean tieneEquipo;
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
