package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.mapper.TalentsMapper;
import com.bdt.bancotalentosbackend.model.dto.*;
import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.TalentsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.bdt.bancotalentosbackend.util.Common.getBaseResponse;
import static com.bdt.bancotalentosbackend.util.Common.simpleSPCall;

@Repository
@RequiredArgsConstructor
public class TalentsRepository {
    private final JdbcTemplate jdbcTemplate;

    public TalentsListResponse getTalents(BaseRequest baseRequest, SearchRequest searchRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_LST");
        TalentsListResponse talentsListResponse = new TalentsListResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
                .addValue("N_PAG", searchRequest.getPage())
                .addValue("BUSQUEDA", searchRequest.getSearch())
                .addValue("HABILIDADES_TECNICAS", searchRequest.getTechAbilities())
                .addValue("ID_NIVEL_INGLES", searchRequest.getIdEnglishLevel())
                .addValue("ID_USUARIO_FAVORITOS", searchRequest.getIdTalentCollection());

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            Map<String, Object> row = resultSet.get(0);
            Integer totalTalents = (Integer) row.get("TOTAL_LISTA");

            talentsListResponse.setBaseResponse(getBaseResponse(resultSet));
            talentsListResponse.setTotal(totalTalents);

            if (talentsListResponse.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> talentsSet = (List<Map<String, Object>>) result.get("#result-set-2");
                if (talentsSet != null && !talentsSet.isEmpty()) {
                    List<TalentListDTO> talents = new ArrayList<>();

                    for (Map<String, Object> talentRow : talentsSet) {
                        talents.add(TalentsMapper.mapToTalentListDTO(talentRow));
                    }
                    talentsListResponse.setTalents(talents);
                }
            }
        }
        return talentsListResponse;
    }

    public TalentResponse getTalentById(BaseRequest baseRequest, Integer talentId) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_SEL");
        TalentResponse talentResponse = new TalentResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("ID_TALENTO", talentId);

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            talentResponse.setBaseResponse(getBaseResponse(resultSet));

            if (talentResponse.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> talentSet = (List<Map<String, Object>>) result.get("#result-set-2");
                if (talentSet != null && !talentSet.isEmpty()) {
                    Map<String, Object> talentRow = talentSet.get(0);

                    // Talent detail
                    talentResponse.setEmail((String) talentRow.get("EMAIL"));
                    talentResponse.setCelular((String) talentRow.get("CELULAR"));
                    talentResponse.setLinkedin((String) talentRow.get("LINK_LINKEDIN"));
                    talentResponse.setGithub((String) talentRow.get("LINK_GITHUB"));
                    talentResponse.setDescripcion((String) talentRow.get("DESCRIPCION"));
                    talentResponse.setDisponibilidad((String) talentRow.get("DISPONIBILIDAD"));
                    talentResponse.setIdColeccion((Integer) talentRow.get("ID_COLECCION"));
                    talentResponse.setCv(TalentsUtils.getTalentCv(result));
                    talentResponse.setHabilidadesTecnicas(TalentsUtils.getTechAbilities(result));
                    talentResponse.setHabilidadesBlandas(TalentsUtils.getSoftAbilities(result));
                    talentResponse.setExperiencias(TalentsUtils.getWorkExperience(result));
                    talentResponse.setEducaciones(TalentsUtils.getEducations(result));
                    talentResponse.setIdiomas(TalentsUtils.getLanguages(result));
                    talentResponse.setFeedback(TalentsUtils.getFeedback(result));
                }
            }
        }
        return talentResponse;
    }

    public BaseResponse updateTalent(BaseRequest baseRequest, TalentUpdateRequest updateRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_UPD");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO", updateRequest.getIdTalento())
                .addValue("NOMBRES", updateRequest.getNombres())
                .addValue("APELLIDO_PATERNO", updateRequest.getApellidoPaterno())
                .addValue("APELLIDO_MATERNO", updateRequest.getApellidoMaterno())
                .addValue("EMAIL", updateRequest.getEmail())
                .addValue("CELULAR", updateRequest.getTelefono())
                .addValue("RUTA_IMAGEN", updateRequest.getImagen())
                .addValue("LINK_LINKEDIN", updateRequest.getLinkedin())
                .addValue("LINK_GITHUB", updateRequest.getGithub())
                .addValue("DESCRIPCION", updateRequest.getDescripcion())
                .addValue("DISPONIBILIDAD", updateRequest.getDisponibilidad())
                .addValue("PUESTO", updateRequest.getPuesto())
                .addValue("ID_PAIS", updateRequest.getIdPais())
                .addValue("ID_CIUDAD", updateRequest.getIdCiudad())
                .addValue("MONTO_INICIAL_PLANILLA", updateRequest.getMontoInicialPlanilla())
                .addValue("MONTO_FINAL_PLANILLA", updateRequest.getMontoFinalPlanilla())
                .addValue("MONTO_INICIAL_RXH", updateRequest.getMontoInicialRxH())
                .addValue("MONTO_FINAL_RXH", updateRequest.getMontoFinalRxH())
                .addValue("ID_MONEDA", updateRequest.getIdMoneda())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse addTalentToFavourite(BaseRequest baseRequest, TalentToFavRequest favRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_USUARIO_FAVORITOS_TALENTO_INS");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_USUARIO_FAVORITOS", favRequest.getIdColeccion())
                .addValue("ID_TALENTO", favRequest.getIdTalento())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse addTalentTechAbility(BaseRequest baseRequest, TechAbilityRequest techAbilityRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_HABILIDAD_TECNICA_INS");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO", techAbilityRequest.getIdTalento())
                .addValue("ID_HABILIDAD", techAbilityRequest.getIdHabilidad())
                .addValue("ANIOS", techAbilityRequest.getAnios())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse addTalentSoftAbility(BaseRequest baseRequest, SoftAbilityRequest softAbilityRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_HABILIDAD_BLANDA_INS");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO", softAbilityRequest.getIdTalento())
                .addValue("ID_HABILIDAD", softAbilityRequest.getIdHabilidad())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse addOrUpdateTalentExperience(BaseRequest baseRequest, ExperienceRequest experienceRequest) {
        boolean isUpdate = experienceRequest.getIdExperiencia() != null;
        String procedureName = isUpdate ? "SP_BT_TALENTO_EXPERIENCIA_UPD" : "SP_BT_TALENTO_EXPERIENCIA_INS";
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName);
        BaseResponse baseResponse = new BaseResponse();

        LocalDate dateInit = Common.formatDate(experienceRequest.getFechaInicio());
        LocalDate dateEnd = Common.formatDate(experienceRequest.getFechaFin());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("EMPRESA", experienceRequest.getEmpresa())
                .addValue("PUESTO", experienceRequest.getPuesto())
                .addValue("FCH_INICIO", dateInit)
                .addValue("FCH_FIN", dateEnd)
                .addValue("FL_ACTUALIDAD", experienceRequest.getFlActualidad())
                .addValue("FUNCIONES", experienceRequest.getFunciones())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        if (isUpdate) {
            params.addValue("ID_EXPERIENCIA", experienceRequest.getIdExperiencia());
        } else {
            params.addValue("ID_TALENTO", experienceRequest.getIdTalento());
        }

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse deleteTalentExperience(BaseRequest baseRequest, Integer idExperiencia) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_EXPERIENCIA_DEL");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_EXPERIENCIA", idExperiencia)
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse addOrUpdateTalentEducation(BaseRequest baseRequest, EducationRequest educationRequest) {
        boolean isUpdate = educationRequest.getIdTalentoEducacion() != null;
        String procedureName = isUpdate ? "SP_BT_TALENTO_EDUCACION_UPD" : "SP_BT_TALENTO_EDUCACION_INS";
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName);
        BaseResponse baseResponse = new BaseResponse();

        LocalDate dateInit = Common.formatDate(educationRequest.getFechaInicio());
        LocalDate dateEnd = Common.formatDate(educationRequest.getFechaFin());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("INSTITUCION_EDUCATIVA", educationRequest.getInstitucion())
                .addValue("CARRERA", educationRequest.getCarrera())
                .addValue("GRADO", educationRequest.getGrado())
                .addValue("FCH_INICIO", dateInit)
                .addValue("FCH_FIN", dateEnd)
                .addValue("FL_ACTUALIDAD", educationRequest.getFlActualidad())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        if (isUpdate) {
            params.addValue("ID_TALENTO_EDUCACION", educationRequest.getIdTalentoEducacion());
        } else {
            params.addValue("ID_TALENTO", educationRequest.getIdTalento());
        }

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse deleteTalentEducation(BaseRequest baseRequest, Integer idEducacion) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_EDUCACION_DEL");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO_EDUCACION", idEducacion)
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse addOrUpdateTalentLanguage(BaseRequest baseRequest, LanguageRequest languageRequest) {
        boolean isUpdate = languageRequest.getIdTalentoIdioma() != null && languageRequest.getIdTalentoIdioma() > 0;
        String procedureName = isUpdate ? "SP_BT_TALENTO_IDIOMA_UPD" : "SP_BT_TALENTO_IDIOMA_INS";
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName);
        BaseResponse baseResponse = new BaseResponse();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_IDIOMA", languageRequest.getIdIdioma())
                .addValue("ID_NIVEL", languageRequest.getIdNivel())
                .addValue("ESTRELLAS", languageRequest.getEstrellas())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        if (isUpdate) {
            params.addValue("ID_TALENTO_IDIOMA", languageRequest.getIdTalentoIdioma());
        } else {
            params.addValue("ID_TALENTO", languageRequest.getIdTalento());
        }

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse deleteTalentLanguage(BaseRequest baseRequest, Integer idLanguage) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_IDIOMA_DEL");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO_IDIOMA", idLanguage)
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse addOrUpdateTalentFeedback(BaseRequest baseRequest, FeedbackRequest feedbackRequest) {
        boolean isUpdate = feedbackRequest.getIdFeedback() != null && feedbackRequest.getIdFeedback() > 0;
        String procedureName = isUpdate ? "SP_BT_TALENTO_FEEDBACK_UPD" : "SP_BT_TALENTO_FEEDBACK_INS";
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName);
        BaseResponse baseResponse = new BaseResponse();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ESTRELLAS", feedbackRequest.getEstrellas())
                .addValue("DESCRIPCION", feedbackRequest.getFeedback())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        if (isUpdate) {
            params.addValue("ID_FEEDBACK", feedbackRequest.getIdFeedback());
        } else {
            params.addValue("ID_TALENTO", feedbackRequest.getIdTalento());
        }

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public BaseResponse deleteTalentFeedback(BaseRequest baseRequest, Integer idFeedback) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_FEEDBACK_DEL");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_FEEDBACK", idFeedback)
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }
}
