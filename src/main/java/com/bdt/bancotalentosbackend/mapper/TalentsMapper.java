package com.bdt.bancotalentosbackend.mapper;

import com.bdt.bancotalentosbackend.model.dto.*;

import com.bdt.bancotalentosbackend.util.FileUtils;

import java.util.Map;

public class TalentsMapper {
    public static FeedbackDTO mapToFeedbackDTO(Map<String, Object> row) {
        return new FeedbackDTO(
                (Integer) row.get("ID_FEEDBACK"),
                (Integer) row.get("ES_EDITABLE"),
                (String) row.get("USUARIO"),
                (String) row.get("DESCRIPCION"),
                (Integer) row.get("ESTRELLAS")
        );
    }

    public static LanguageDTO mapToLanguageDTO(Map<String, Object> row) {
        return new LanguageDTO(
                (Integer) row.get("ID_TALENTO_IDIOMA"),
                (Integer) row.get("ID_IDIOMA"),
                (String) row.get("IDIOMA"),
                (Integer) row.get("ID_NIVEL"),
                (String) row.get("NIVEL"),
                (Integer) row.get("ESTRELLAS")
        );
    }

    public static EducationDTO mapToEducationDTO(Map<String, Object> row) {
        return new EducationDTO(
                (Integer) row.get("ID_TALENTO_EDUCACION"),
                (String) row.get("INSTITUCION_EDUCATIVA"),
                (String) row.get("CARRERA"),
                (String) row.get("GRADO"),
                (String) row.get("FCH_INICIO"),
                (String) row.get("FCH_FIN"),
                (Integer) row.get("FL_ACTUALIDAD")
        );
    }

    public static WorkExperienceDTO mapToWorkExperienceDTO(Map<String, Object> row) {
        return new WorkExperienceDTO(
                (Integer) row.get("ID_EXPERIENCIA"),
                (String) row.get("EMPRESA"),
                (String) row.get("PUESTO"),
                (String) row.get("FUNCIONES"),
                (String) row.get("FCH_INICIO"),
                (String) row.get("FCH_FIN"),
                (String) row.get("TIEMPO"),
                (Integer) row.get("FL_ACTUALIDAD")
        );
    }

    public static SoftAbilityDTO mapToSoftAbilityDTO(Map<String, Object> row) {
        return new SoftAbilityDTO(
                (String) row.get("HABILIDAD")
        );
    }

    public static TechAbilityDTO mapToTechAbilityDTO(Map<String, Object> row) {
        return new TechAbilityDTO(
                (String) row.get("HABILIDAD"),
                (Integer) row.get("ANIOS")
        );
    }

    public static TalentListDTO mapToTalentListDTO(Map<String, Object> talentRow) {
        return new TalentListDTO(
                (Integer) talentRow.get("ID_TALENTO"),
                (String) talentRow.get("NOMBRES"),
                (String) talentRow.get("APELLIDO_PATERNO"),
                (String) talentRow.get("APELLIDO_MATERNO"),
                FileUtils.cargarImagen((String) talentRow.get("RUTA_IMAGEN")),
                (String) talentRow.get("PUESTO"),
                (String) talentRow.get("PAIS"),
                (String) talentRow.get("CIUDAD"),
                (Double) talentRow.get("MONTO_INICIAL_PLANILLA"),
                (Double) talentRow.get("MONTO_FINAL_PLANILLA"),
                (Double) talentRow.get("MONTO_INICIAL_RXH"),
                (Double) talentRow.get("MONTO_FINAL_RXH"),
                (String) talentRow.get("MONEDA"),
                (Integer) talentRow.get("ESTRELLAS"),
                (Integer) talentRow.get("ES_FAVORITO")
        );
    }

}
