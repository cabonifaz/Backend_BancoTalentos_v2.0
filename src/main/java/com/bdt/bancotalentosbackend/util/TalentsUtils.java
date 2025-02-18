package com.bdt.bancotalentosbackend.util;


import com.bdt.bancotalentosbackend.mapper.TalentsMapper;
import com.bdt.bancotalentosbackend.model.dto.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TalentsUtils {
    public static List<FeedbackDTO> getFeedback(Map<String, Object> dataSets) {
        List<FeedbackDTO> feedbacks = new ArrayList<>();
        List<Map<String, Object>> dataSet = (List<Map<String, Object>>) dataSets.get("#result-set-9");

        if (dataSet != null && !dataSet.isEmpty()) {
            for (Map<String, Object> row : dataSet) {
                feedbacks.add(TalentsMapper.mapToFeedbackDTO(row));
            }
        }

        return feedbacks;
    }

    public static List<LanguageDTO> getLanguages(Map<String, Object> dataSets) {
        List<LanguageDTO> languages = new ArrayList<>();
        List<Map<String, Object>> dataSet = (List<Map<String, Object>>) dataSets.get("#result-set-8");

        if (dataSet != null && !dataSet.isEmpty()) {
            for (Map<String, Object> row : dataSet) {
                languages.add(TalentsMapper.mapToLanguageDTO(row));
            }
        }

        return languages;
    }

    public static List<EducationDTO> getEducations(Map<String, Object> dataSets) {
        List<EducationDTO> educations = new ArrayList<>();
        List<Map<String, Object>> dataSet = (List<Map<String, Object>>) dataSets.get("#result-set-7");

        if (dataSet != null && !dataSet.isEmpty()) {
            for (Map<String, Object> row : dataSet) {
                educations.add(TalentsMapper.mapToEducationDTO(row));
            }
        }

        return educations;
    }

    public static List<WorkExperienceDTO> getWorkExperience(Map<String, Object> dataSets) {
        List<WorkExperienceDTO> workExperiences = new ArrayList<>();
        List<Map<String, Object>> dataSet = (List<Map<String, Object>>) dataSets.get("#result-set-6");

        if (dataSet != null && !dataSet.isEmpty()) {
            for (Map<String, Object> row : dataSet) {
                workExperiences.add(TalentsMapper.mapToWorkExperienceDTO(row));
            }
        }

        return workExperiences;
    }

    public static List<SoftAbilityDTO> getSoftAbilities(Map<String, Object> dataSets) {
        List<SoftAbilityDTO> softAbilities = new ArrayList<>();
        List<Map<String, Object>> dataSet = (List<Map<String, Object>>) dataSets.get("#result-set-5");

        if (dataSet != null && !dataSet.isEmpty()) {
            for (Map<String, Object> row : dataSet) {
                softAbilities.add(TalentsMapper.mapToSoftAbilityDTO(row));
            }
        }

        return softAbilities;
    }

    public static List<TechAbilityDTO> getTechAbilities(Map<String, Object> dataSets) {
        List<TechAbilityDTO> techAbilities = new ArrayList<>();
        List<Map<String, Object>> dataSet = (List<Map<String, Object>>) dataSets.get("#result-set-4");

        if (dataSet != null && !dataSet.isEmpty()) {
            for (Map<String, Object> row : dataSet) {
                techAbilities.add(TalentsMapper.mapToTechAbilityDTO(row));
            }
        }

        return techAbilities;
    }

    public static FileDTO getTalentCv(Map<String, Object> dataSets) {
        FileDTO fileDTO = new FileDTO();
        List<Map<String, Object>> dataSet = (List<Map<String, Object>>) dataSets.get("#result-set-3");

        if (dataSet != null && !dataSet.isEmpty()) {
            Map<String, Object> row = dataSet.get(0);
            fileDTO.setIdArchivo((Integer) row.get("ID_ARCHIVO"));
            fileDTO.setNombreArchivo((String) row.get("NOMBRE_ARCHIVO"));
            fileDTO.setRutaArchivo((String) row.get("RUTA_ARCHIVO"));
            fileDTO.setTipoArchivo((String) row.get("TIPO_ARCHIVO"));
            fileDTO.setTipoDocumento((String) row.get("TIPO_DOCUMENTO"));
        }

        return fileDTO;
    }
}
