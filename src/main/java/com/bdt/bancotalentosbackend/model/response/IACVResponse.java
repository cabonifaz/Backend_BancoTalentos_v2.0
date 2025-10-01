package com.bdt.bancotalentosbackend.model.response;

import java.util.List;

import com.bdt.bancotalentosbackend.model.dto.ContactDTO;
import com.bdt.bancotalentosbackend.model.dto.EducationDTO;
import com.bdt.bancotalentosbackend.model.dto.LanguageDTO;
import com.bdt.bancotalentosbackend.model.dto.LocationDTO;
import com.bdt.bancotalentosbackend.model.dto.SocialLinkDTO;
import com.bdt.bancotalentosbackend.model.dto.SoftAbilityDTO;
import com.bdt.bancotalentosbackend.model.dto.TechAbilityDTO;
import com.bdt.bancotalentosbackend.model.dto.WorkExperienceDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IACVResponse {
    private BaseResponse result;
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String docIdentidad;
    private ContactDTO contacto;
    private LocationDTO location;
    private List<TechAbilityDTO> tecSkills;
    private SocialLinkDTO social;
    private String presentacion;
    private List<SoftAbilityDTO> softSkills;
    private List<WorkExperienceDTO> workExps;
    private List<EducationDTO> edExps;
    private List<LanguageDTO> langs;
}
