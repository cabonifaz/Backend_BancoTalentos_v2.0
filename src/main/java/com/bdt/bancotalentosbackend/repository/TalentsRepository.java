package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.mapper.TalentsMapper;
import com.bdt.bancotalentosbackend.model.dto.*;
import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.FileResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.TalentsUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static com.bdt.bancotalentosbackend.util.FileUtils.*;

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
                    talentResponse.setIdMoneda((Integer) talentRow.get("ID_MONEDA"));
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

    public FileResponse getTalentFile(BaseRequest baseRequest, Integer fileId) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_ARCHIVOS_SEL");
        FileResponse fileResponse = new FileResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_ARCHIVO", fileId)
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario());

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            fileResponse.setBaseResponse(getBaseResponse(resultSet));

            if (fileResponse.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> fileSet = (List<Map<String, Object>>) result.get("#result-set-2");

                if (fileSet != null && !fileSet.isEmpty()) {
                    Map<String, Object> fileRow = fileSet.get(0);
                    fileResponse.setArchivo((String) fileRow.get("RUTA_ARCHIVO"));
                }
            }
        }

        return fileResponse;
    }

    public BaseResponse addOrUpdateTalent(BaseRequest baseRequest, TalentRequest talentRequest) throws JsonProcessingException {
        boolean isUpdate = talentRequest.getIdTalento() != null && talentRequest.getIdTalento() > 0;
        String procedureName = isUpdate ? "SP_BT_TALENTO_UPD" : "SP_BT_TALENTO_INS";
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName);
        BaseResponse baseResponse = new BaseResponse();

        FileRequest fotoRequest = talentRequest.getFotoArchivo();
        String rutaFoto = fotoRequest != null
                ? Constante.RUTA_REPOSITORIO_FOTO_TALENTO + fotoRequest.getNombreArchivo() + "." + fotoRequest.getExtensionArchivo()
                : null;

        FileRequest cvRequest = talentRequest.getCvArchivo();
        String rutaCV = cvRequest != null
                ? Constante.RUTA_REPOSITORIO_CV_TALENTO + cvRequest.getNombreArchivo() + "." + cvRequest.getExtensionArchivo()
                : null;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("NOMBRES", talentRequest.getNombres())
                .addValue("APELLIDO_PATERNO", talentRequest.getApellidoPaterno())
                .addValue("APELLIDO_MATERNO", talentRequest.getApellidoMaterno())
                .addValue("EMAIL", talentRequest.getEmail())
                .addValue("CELULAR", talentRequest.getTelefono())
                .addValue("RUTA_IMAGEN", rutaFoto)
                .addValue("LINK_LINKEDIN", talentRequest.getLinkedin())
                .addValue("LINK_GITHUB", talentRequest.getGithub())
                .addValue("DESCRIPCION", talentRequest.getDescripcion())
                .addValue("DISPONIBILIDAD", talentRequest.getDisponibilidad())
                .addValue("PUESTO", talentRequest.getPuesto())
                .addValue("ID_PAIS", talentRequest.getIdPais())
                .addValue("ID_CIUDAD", talentRequest.getIdCiudad())
                .addValue("MONTO_INICIAL_PLANILLA", talentRequest.getMontoInicialPlanilla())
                .addValue("MONTO_FINAL_PLANILLA", talentRequest.getMontoFinalPlanilla())
                .addValue("MONTO_INICIAL_RXH", talentRequest.getMontoInicialRxH())
                .addValue("MONTO_FINAL_RXH", talentRequest.getMontoFinalRxH())
                .addValue("ID_MONEDA", talentRequest.getIdMoneda())

                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        if (isUpdate) {
            params.addValue("ID_TALENTO", talentRequest.getIdTalento());
        } else {

            ObjectMapper objectMapper = new ObjectMapper();

            String habilidadesTecnicasJson = objectMapper.writeValueAsString(talentRequest.getHabilidadesTecnicas());
            String habilidadesBlandasJson = objectMapper.writeValueAsString(talentRequest.getHabilidadesBlandas());
            String experienciasJson = objectMapper.writeValueAsString(talentRequest.getExperiencias());
            String educacionesJson = objectMapper.writeValueAsString(talentRequest.getEducaciones());
            String idiomasJson = objectMapper.writeValueAsString(talentRequest.getIdiomas());

            params.addValue("HABILIDADES_TECNICAS_JSON", habilidadesTecnicasJson)
                    .addValue("HABILIDADES_BLANDAS_JSON", habilidadesBlandasJson)
                    .addValue("EXPERIENCIAS_JSON", experienciasJson)
                    .addValue("EDUCACIONES_JSON", educacionesJson)
                    .addValue("IDIOMAS_JSON", idiomasJson)
                    .addValue("CV_NOMBRE_ARCHIVO", cvRequest != null ? cvRequest.getNombreArchivo() : null)
                    .addValue("CV_ID_TIPO_ARCHIVO", cvRequest != null ? cvRequest.getIdTipoArchivo() : null)
                    .addValue("CV_ID_TIPO_DOCUMENTO", cvRequest != null ? cvRequest.getIdTipoDocumento() : null)
                    .addValue("CV_RUTA_ARCHIVO", rutaCV);
        }

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            baseResponse = getBaseResponse(resultSet);
            Map<String, Object> row = resultSet.get(0);

            rutaFoto = (String) row.get("NUEVA_RUTA_IMAGEN");

            if (cvRequest != null) {
                rutaCV = (String) row.get("NUEVA_RUTA_CV");
            }
        }

        if (baseResponse.getIdMensaje() == 2 && fotoRequest != null) {
            boolean imagenGuardada = isUpdate
                    ? reemplazarImagen(fotoRequest.getStringB64(), fotoRequest.getExtensionArchivo(), rutaFoto)
                    : guardarImagen(fotoRequest.getStringB64(), fotoRequest.getExtensionArchivo(), rutaFoto);

            if (!imagenGuardada) {
                baseResponse.setIdMensaje(1);
                baseResponse.setMensaje("Los datos han sido registrados correctamente, pero no se pudo guardar la foto");
            }
        }

        if (baseResponse.getIdMensaje() == 2 && cvRequest != null) {
            boolean cvGuardado = guardarArchivo(cvRequest.getStringB64(), rutaCV);
            if (!cvGuardado) {
                baseResponse.setIdMensaje(1);
                baseResponse.setMensaje("Los datos han sido registrados correctamente, pero no se pudo guardar el CV");
            }
        }

        return baseResponse;
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
        boolean isUpdate = experienceRequest.getIdExperiencia() != null && experienceRequest.getIdTalento() > 0;
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
            if (experienceRequest.getIdTalento() == null || experienceRequest.getIdTalento() <= 0) {
                baseResponse.setIdMensaje(1);
                baseResponse.setMensaje("El campo idTalento es obligatorio para agregar experiencias");
                return baseResponse;
            }
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
        boolean isUpdate = educationRequest.getIdTalentoEducacion() != null && educationRequest.getIdTalento() > 0;
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
            if (educationRequest.getIdTalento() == null || educationRequest.getIdTalento() <= 0) {
                baseResponse.setIdMensaje(1);
                baseResponse.setMensaje("El campo idTalento es obligatorio para agregar educación");
                return baseResponse;
            }
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
            if (languageRequest.getIdTalento() == null || languageRequest.getIdTalento() <= 0) {
                baseResponse.setIdMensaje(1);
                baseResponse.setMensaje("El campo idTalento es obligatorio para agregar idiomas");
                return baseResponse;
            }
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
            if (feedbackRequest.getIdTalento() == null || feedbackRequest.getIdTalento() <= 0) {
                baseResponse.setIdMensaje(1);
                baseResponse.setMensaje("El campo idTalento es obligatorio para agregar feedback");
                return baseResponse;
            }
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

    public BaseResponse uploadTalentFile(BaseRequest baseRequest, UploadTalentFileRequest uploadTalentFileRequest) {
        String ruta = Constante.RUTA_REPOSITORIO_TALENTO_ARCHIVOS + uploadTalentFileRequest.getNombreArchivo() + "." + uploadTalentFileRequest.getExtensionArchivo();

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_ARCHIVOS_INS");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO", uploadTalentFileRequest.getIdTalento())
                .addValue("NOMBRE_ARCHIVO", uploadTalentFileRequest.getNombreArchivo())
                .addValue("ID_TIPO_ARCHIVO", uploadTalentFileRequest.getIdTipoArchivo())
                .addValue("ID_TIPO_DOCUMENTO", uploadTalentFileRequest.getIdTipoDocumento())
                .addValue("RUTA_ARCHIVO", ruta)
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            baseResponse = getBaseResponse(resultSet);
            Map<String, Object> row = resultSet.get(0);

            String rutaArchivo = (String) row.get("NUEVA_RUTA_ARCHIVO");
            boolean archivoGuardado = guardarArchivo(uploadTalentFileRequest.getString64(), rutaArchivo);

            if (!archivoGuardado) {
                baseResponse.setIdMensaje(1);
                baseResponse.setMensaje("Los datos han sido registrados correctamente, pero no se pudo guardar el CV");
            }
        }

        return baseResponse;
    }

    public BaseResponse updateCvFile(BaseRequest baseRequest, UpdateCvFileRequest updateCvFileRequest) {
        String ruta = Constante.RUTA_REPOSITORIO_CV_TALENTO + updateCvFileRequest.getNombreArchivo() + "." + updateCvFileRequest.getExtensionArchivo();

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_ARCHIVOS_UPD");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO", updateCvFileRequest.getIdTalento())
                .addValue("ID_ARCHIVO", updateCvFileRequest.getIdArchivo())
                .addValue("NOMBRE_ARCHIVO", updateCvFileRequest.getNombreArchivo())
                .addValue("ID_TIPO_ARCHIVO", updateCvFileRequest.getIdTipoArchivo())
                .addValue("ID_TIPO_DOCUMENTO", updateCvFileRequest.getIdTipoDocumento())
                .addValue("RUTA_ARCHIVO", ruta)
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            baseResponse = getBaseResponse(resultSet);
            Map<String, Object> row = resultSet.get(0);

            String rutaCV = (String) row.get("NUEVA_RUTA_ARCHIVO");
            boolean cvGuardado = reemplazarArchivo(updateCvFileRequest.getString64(), rutaCV);

            if (!cvGuardado) {
                baseResponse.setIdMensaje(1);
                baseResponse.setMensaje("Los datos han sido registrados correctamente, pero no se pudo guardar el CV");
            }
        }

        return baseResponse;
    }


    //    Espacio solo para migración de archivos
    public void migrateProfilePhoto() {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_TALENTO_MIGRAR_FOTO_LST");
        Map<String, Object> result = simpleJdbcCall.execute();
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            List<MigrationProfilePhotoDTO> lstMigration = new ArrayList<>();

            for (Map<String, Object> talentRow : resultSet) {
                MigrationProfilePhotoDTO migration = new MigrationProfilePhotoDTO(
                        (Integer) talentRow.get("ID_TALENTO"),
                        (String) talentRow.get("NUEVA_RUTA_IMAGEN"),
                        (String) talentRow.get("FILE_EXTENSION"),
                        (String) talentRow.get("IM_IMAGE_VARCHAR")
                );
                lstMigration.add(migration);
            }


            for (MigrationProfilePhotoDTO objMigration : lstMigration) {
                if (objMigration.getNuevaRutaImagen() != null && !objMigration.getNuevaRutaImagen().equals("")) {
                    System.out.println("Cargando objeto de id: " + objMigration.getIdTalento());
                    guardarImagenMigracion(objMigration.getFileB64(), objMigration.getFileExtension(), objMigration.getNuevaRutaImagen());
                    migrateProfilePhotoUpdate(objMigration.getIdTalento(), objMigration.getNuevaRutaImagen());
                }

            }

        }
    }

    public void migrateProfilePhotoUpdate(Integer idTalento, String rutaImagen) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_TALENTO_MIGRAR_FOTO_UPD");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO", idTalento)
                .addValue("URLFOTO", rutaImagen);
        simpleJdbcCall.execute(params);
    }

    public void migrateCV() {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_TALENTO_MIGRAR_CV_LST");
        Map<String, Object> result = simpleJdbcCall.execute();
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            List<MigrationCVDTO> lstMigration = new ArrayList<>();

            for (Map<String, Object> talentRow : resultSet) {
                MigrationCVDTO migration = new MigrationCVDTO(
                        (Integer) talentRow.get("ID_TALENTO"),
                        (String) talentRow.get("NUEVA_RUTA_CV"),
                        (String) talentRow.get("FILE_EXTENSION"),
                        (String) talentRow.get("ARCHIVO_B64")
                );
                lstMigration.add(migration);
            }


            for (MigrationCVDTO objMigration : lstMigration) {
                if (objMigration.getNuevaRutaCV() != null && !objMigration.getNuevaRutaCV().equals("")) {
                    System.out.println("Cargando objeto de id: " + objMigration.getIdTalento());
                    guardarArchivoMigracion(objMigration.getFileBase64(), objMigration.getNuevaRutaCV());
                    migrateCVUpdate(objMigration.getIdTalento(), objMigration.getNuevaRutaCV());
                }

            }

        }
    }

    public void migrateCVUpdate(Integer idTalento, String rutaCV) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_TALENTO_MIGRAR_CV_UPD");
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_TALENTO", idTalento)
                .addValue("URL_CV", rutaCV);
        simpleJdbcCall.execute(params);
    }
}
