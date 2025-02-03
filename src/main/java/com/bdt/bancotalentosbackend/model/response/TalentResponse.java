package com.bdt.bancotalentosbackend.model.response;

import com.bdt.bancotalentosbackend.model.dto.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentResponse {
    @JsonProperty("result")
    private BaseResponse baseResponse;
    private String email;
    private String celular;
    private String linkedin;
    private String github;
    private String descripcion;
    private String disponibilidad;
    private FileDTO cv;
    private List<TechAbilityDTO> habilidadesTecnicas;
    private List<SoftAbilityDTO> habilidadesBlandas;
    private List<WorkExperienceDTO> experiencias;
    private List<EducationDTO> educaciones;
    private List<LanguageDTO> idiomas;
    private List<FeedbackDTO> feedback;
}
