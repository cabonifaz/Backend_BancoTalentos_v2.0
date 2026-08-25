package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.response.*;
import com.bdt.bancotalentosbackend.mapper.TalentsMapper;
import com.bdt.bancotalentosbackend.model.dto.*;
import com.bdt.bancotalentosbackend.model.request.*;
import com.bdt.bancotalentosbackend.util.Common;
import com.bdt.bancotalentosbackend.util.Constante;
import com.bdt.bancotalentosbackend.util.TalentsUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static com.bdt.bancotalentosbackend.util.Common.getBaseResponse;
import static com.bdt.bancotalentosbackend.util.Common.simpleSPCall;
import static com.bdt.bancotalentosbackend.util.Common.getInsertUpdateResponse;
import static com.bdt.bancotalentosbackend.util.FileUtils.*;

@Repository
@RequiredArgsConstructor
public class TalentsRepository {
  private final JdbcTemplate jdbcTemplate;

  public TalentsListResponse getTalents(BaseRequest baseRequest, SearchRequest searchRequest) {
    try {
      TalentsListResponse talentsListResponse = new TalentsListResponse();

      SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("SP_BT_TALENTO_LST");
      SqlParameterSource params = new MapSqlParameterSource()
          .addValue("ID_ROL", baseRequest.getIdRol())
          .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
          .addValue("ID_USUARIO", baseRequest.getIdUsuario())
          .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
          .addValue("N_PAG", searchRequest.getPage())
          .addValue("BUSQUEDA", searchRequest.getSearch())
          .addValue("HABILIDADES_TECNICAS", searchRequest.getTechAbilities())
          .addValue("ID_NIVEL_INGLES", searchRequest.getIdEnglishLevel())
          .addValue("ID_USUARIO_FAVORITOS", searchRequest.getIdTalentCollection())
          .addValue("PUESTO", searchRequest.getJobPosition())
          .addValue("ANIOS_EXPERIENCIA", searchRequest.getYearsExperience())
          .addValue("EDUCACION", searchRequest.getEducationName())
          .addValue("ID_GRADO", searchRequest.getIdAcademicGrade());

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
    } catch (Exception e) {
      System.err.println("Error en REPOSITORY getTalents");
      System.err.println(e.getMessage());

      return new TalentsListResponse(new BaseResponse(3, e.getMessage()), null, null);
    }

  }

  /**
   * Get talent by ID
   * 
   * @param baseRequest
   * @param talentId
   * @param loadExtraInfo
   * @return TalentResponse
   */
  public TalentResponse getTalentById(BaseRequest baseRequest, Integer talentId, boolean loadExtraInfo) {
    var simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_TALENTO_SEL");
    var talentResponse = new TalentResponse();

    var params = new MapSqlParameterSource()
        .addValue("ID_ROL", baseRequest.getIdRol())
        .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
        .addValue("ID_USUARIO", baseRequest.getIdUsuario())
        .addValue("ID_TALENTO", talentId)
        .addValue("LOAD_EXTRA_INFO", loadExtraInfo);

    var result = simpleJdbcCall.execute(params);
    var resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

    if (resultSet == null || resultSet.isEmpty()) {
      talentResponse.setBaseResponse(new BaseResponse(3, "No hubo respuesta de la base de datos"));
      return talentResponse;
    }

    talentResponse.setBaseResponse(getBaseResponse(resultSet));

    if (talentResponse.getBaseResponse().getIdMensaje() != 2) {
      return talentResponse;
    }

    var talentSet2 = (List<Map<String, Object>>) result.get("#result-set-2");

    var talentRow = talentSet2.get(0);

    // Talent detail
    talentResponse.setIdTalento((Integer) talentRow.get("ID_TALENTO"));
    talentResponse.setNombres((String) talentRow.get("NOMBRES"));
    talentResponse.setApellidos((String) talentRow.get("APELLIDOS"));
    talentResponse.setDni((String) talentRow.get("DNI"));
    talentResponse.setEmail((String) talentRow.get("EMAIL"));
    talentResponse.setCelular((String) talentRow.get("CELULAR"));
    talentResponse.setProcedencia((String) talentRow.get("PROCEDENCIA"));
    talentResponse.setLinkedin((String) talentRow.get("LINK_LINKEDIN"));
    talentResponse.setGithub((String) talentRow.get("LINK_GITHUB"));
    talentResponse.setDescripcion((String) talentRow.get("DESCRIPCION"));
    talentResponse.setDisponibilidad((String) talentRow.get("DISPONIBILIDAD"));
    talentResponse.setIdMoneda((Integer) talentRow.get("ID_MONEDA"));
    talentResponse.setPhotoUrl((String) talentRow.get("RUTA_IMAGEN"));

    talentResponse.setIdPais((Integer) talentRow.get("ID_PAIS"));
    talentResponse.setIdCiudad((Integer) talentRow.get("ID_CIUDAD"));

    talentResponse.setSituacion((Integer) talentRow.get("ID_SITUACION"));
    talentResponse.setEstado((Integer) talentRow.get("ID_ESTADO"));

    talentResponse.setIdColeccion(TalentsUtils.getTalentCollection(result));
    talentResponse.setFiles(TalentsUtils.getTalentFiles(result));
    talentResponse.setHabilidadesTecnicas(TalentsUtils.getTechAbilities(result));
    talentResponse.setHabilidadesBlandas(TalentsUtils.getSoftAbilities(result));
    talentResponse.setExperiencias(TalentsUtils.getWorkExperience(result));
    talentResponse.setEducaciones(TalentsUtils.getEducations(result));
    talentResponse.setIdiomas(TalentsUtils.getLanguages(result));
    talentResponse.setFeedback(TalentsUtils.getFeedback(result));

    return talentResponse;
  }

  public FileResponse getTalentFile(BaseRequest baseRequest, Integer fileId) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_TALENTO_ARCHIVOS_SEL");
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

  /**
   * Sustituye el marcador {@code [ID]} de las constantes de carpeta por el id del
   * talento.
   *
   * <p>
   * Las constantes {@code RUTA_REPOSITORIO_*} llevan el marcador y aquí se
   * concatenaban SIN reemplazarlo, así que al SP le llegaba una ruta literal
   * {@code repositorio/talento/[ID]/foto.png}. Hoy no se nota porque el SP
   * devuelve {@code NUEVA_RUTA_IMAGEN} y esa es la que se guarda, pero deja una
   * ruta rota lista para colarse en cuanto el SP respete la que recibe.
   *
   * <p>
   * En el alta el id todavía no existe, así que ahí el marcador se mantiene y es
   * el SP quien debe construir la ruta.
   */
  private String sustituirMarcadorId(String ruta, Integer idTalento) {
    if (ruta == null || idTalento == null || idTalento <= 0) {
      return ruta;
    }
    return ruta.replace("[ID]", idTalento.toString());
  }

  public BaseResponse addOrUpdateTalent(BaseRequest baseRequest, TalentRequest talentRequest)
      throws JsonProcessingException {
    try {
      boolean isUpdate = talentRequest.getIdTalento() != null && talentRequest.getIdTalento() > 0;
      String procedureName = isUpdate ? "SP_BT_TALENTO_UPD" : "SP_BT_TALENTO_INS";
      SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName);
      BaseResponse baseResponse = new BaseResponse();

      FileRequest fotoRequest = talentRequest.getFotoArchivo();
      // La foto ya subida a S3 con URL pre-firmada llega con rutaArchivo. En ese
      // caso la key es la definitiva: no se recalcula, no se sube nada y el SP
      // debe guardarla tal cual.
      boolean fotoYaEnS3 = fotoRequest != null
          && fotoRequest.getRutaArchivo() != null
          && !fotoRequest.getRutaArchivo().trim().isEmpty();
      String rutaFoto;
      if (fotoYaEnS3) {
        rutaFoto = fotoRequest.getRutaArchivo().trim();
      } else if (fotoRequest != null) {
        rutaFoto = sustituirMarcadorId(
            Constante.RUTA_REPOSITORIO_FOTO_TALENTO + fotoRequest.getNombreArchivo() + "."
                + fotoRequest.getExtensionArchivo(),
            talentRequest.getIdTalento());
      } else {
        rutaFoto = null;
      }

      FileRequest cvRequest = talentRequest.getCvArchivo();
      String rutaCV = cvRequest != null
          ? sustituirMarcadorId(
              Constante.RUTA_REPOSITORIO_CV_TALENTO + cvRequest.getNombreArchivo() + "."
                  + cvRequest.getExtensionArchivo(),
              talentRequest.getIdTalento())
          : null;

      MapSqlParameterSource params = new MapSqlParameterSource()
          .addValue("DNI", talentRequest.getDni())
          .addValue("NOMBRES", talentRequest.getNombres())
          .addValue("APELLIDO_PATERNO", talentRequest.getApellidoPaterno())
          .addValue("APELLIDO_MATERNO", talentRequest.getApellidoMaterno())
          .addValue("EMAIL", talentRequest.getEmail())
          .addValue("CELULAR", talentRequest.getTelefono())
          .addValue("PROCEDENCIA", talentRequest.getProcedencia())
          .addValue("RUTA_IMAGEN", rutaFoto)
          .addValue("LINK_LINKEDIN", talentRequest.getLinkedin())
          .addValue("LINK_GITHUB", talentRequest.getGithub())
          .addValue("DESCRIPCION", talentRequest.getDescripcion())
          .addValue("DISPONIBILIDAD", talentRequest.getDisponibilidad())
          .addValue("PUESTO", "")
          .addValue("ID_PAIS", talentRequest.getIdPais())
          .addValue("ID_CIUDAD", talentRequest.getIdCiudad())
          .addValue("ID_MODALIDAD_FACTURACION", talentRequest.getIdModalidadFacturacion())

          .addValue("MONTO_INICIAL_PLANILLA", talentRequest.getMontoInicialPlanilla())
          .addValue("MONTO_FINAL_PLANILLA", talentRequest.getMontoFinalPlanilla())
          .addValue("MONTO_INICIAL_RXH", talentRequest.getMontoInicialRxH())
          .addValue("MONTO_FINAL_RXH", talentRequest.getMontoFinalRxH())
          .addValue("ID_MONEDA", talentRequest.getIdMoneda())

          // Salary expectations
          .addValue("ID_MONEDA_PLAN", talentRequest.getIdMonedaPlan())
          .addValue("ID_MONEDA_RXH", talentRequest.getIdMonedaRxh())

          .addValue("ID_ROL", baseRequest.getIdRol())
          .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
          .addValue("ID_USUARIO", baseRequest.getIdUsuario())
          .addValue("USERNAME", baseRequest.getUsername());

      if (isUpdate) {
        params.addValue("ID_TALENTO", talentRequest.getIdTalento());
      } else {
        params.addValue("TIENE_EQUIPO", talentRequest.getTieneEquipo() ? 1 : 0);
        SQLServerDataTable tvpHabilidadesTecnicas = loadTvpHabilidadesTec(
            talentRequest.getHabilidadesTecnicas());
        SQLServerDataTable tvpHabilidadesBlandas = loadTvpHabilidadesBlan(
            talentRequest.getHabilidadesBlandas());
        SQLServerDataTable tvpExperiencia = loadTvpExperiencia(talentRequest.getExperiencias());
        SQLServerDataTable tvpEducacion = loadTvpEducacion(talentRequest.getEducaciones());
        SQLServerDataTable tvpIdioma = loadTvpIdioma(talentRequest.getIdiomas());

        params.addValue("LST_HABILIDAD_TECNICA", tvpHabilidadesTecnicas)
            .addValue("LST_HABILIDAD_BLANDA", tvpHabilidadesBlandas)
            .addValue("LST_EXPERIENCIA", tvpExperiencia)
            .addValue("LST_EDUCACION", tvpEducacion)
            .addValue("LST_IDIOMA", tvpIdioma)
            .addValue("CV_NOMBRE_ARCHIVO", cvRequest != null ? cvRequest.getNombreArchivo() : null)
            .addValue("CV_ID_TIPO_ARCHIVO", cvRequest != null ? cvRequest.getIdTipoArchivo() : null)
            .addValue("CV_ID_TIPO_DOCUMENTO", cvRequest != null ? cvRequest.getIdTipoDocumento() : null)
            .addValue("CV_RUTA_ARCHIVO", rutaCV);
      }

      Map<String, Object> result = simpleJdbcCall.execute(params);
      List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

      if (resultSet != null && !resultSet.isEmpty()) {
        baseResponse = getInsertUpdateResponse(resultSet);
        Map<String, Object> row = resultSet.get(0);

        // Con la foto ya en S3 la key la manda el frontend, así que NO se pisa con
        // la que devuelve el SP: hacerlo apuntaría la BD a un objeto inexistente.
        if (!fotoYaEnS3) {
          rutaFoto = (String) row.get("NUEVA_RUTA_IMAGEN");
        }

        if (cvRequest != null) {
          rutaCV = (String) row.get("NUEVA_RUTA_CV");
        }
      }

      if (baseResponse.getIdMensaje() == 2 && fotoRequest != null && !fotoYaEnS3) {
        boolean imagenGuardada = guardarArchivoAws(fotoRequest.getStringB64(),
            fotoRequest.getExtensionArchivo(), rutaFoto, true);

        if (!imagenGuardada) {
          baseResponse.setIdMensaje(1);
          baseResponse.setMensaje(
              "Los datos han sido registrados correctamente, pero no se pudo guardar la foto");
        }
      }

      if (baseResponse.getIdMensaje() == 2 && cvRequest != null) {
        boolean cvGuardado = guardarArchivoAws(cvRequest.getStringB64(), cvRequest.getExtensionArchivo(),
            rutaCV, false);
        if (!cvGuardado) {
          baseResponse.setIdMensaje(1);
          baseResponse
              .setMensaje("Los datos han sido registrados correctamente, pero no se pudo guardar el CV");
        }
      }

      return baseResponse;
    } catch (Exception e) {
      System.err.println("ERROR EN addOrUpdateTalent:::");
      System.err.println(e.getMessage());
      return new BaseResponse(3, "Error interno::" + e.getMessage());
    }
  }

  private SQLServerDataTable loadTvpHabilidadesTec(List<TechAbilityRequest> habilidades) throws SQLServerException {
    SQLServerDataTable tvpHabilidades = new SQLServerDataTable();

    tvpHabilidades.addColumnMetadata("ID_HABILIDAD", Types.INTEGER);
    tvpHabilidades.addColumnMetadata("HABILIDAD", Types.VARCHAR);
    tvpHabilidades.addColumnMetadata("ANIOS", Types.INTEGER);

    if (habilidades != null && !habilidades.isEmpty()) {
      for (TechAbilityRequest habilidad : habilidades) {
        tvpHabilidades.addRow(
            habilidad.getIdHabilidad(),
            habilidad.getHabilidad(),
            habilidad.getAnios());
      }
    }

    return tvpHabilidades;
  }

  private SQLServerDataTable loadTvpHabilidadesBlan(List<SoftAbilityRequest> habilidades) throws SQLServerException {
    SQLServerDataTable tvpHabilidades = new SQLServerDataTable();

    tvpHabilidades.addColumnMetadata("ID_HABILIDAD", Types.INTEGER);
    tvpHabilidades.addColumnMetadata("HABILIDAD", Types.VARCHAR);

    if (habilidades != null && !habilidades.isEmpty()) {
      for (SoftAbilityRequest habilidad : habilidades) {
        tvpHabilidades.addRow(
            habilidad.getIdHabilidad(),
            habilidad.getHabilidad());
      }
    }

    return tvpHabilidades;
  }

  private SQLServerDataTable loadTvpExperiencia(List<ExperienceRequest> experiencias) throws SQLServerException {
    SQLServerDataTable tvpExperiencia = new SQLServerDataTable();

    tvpExperiencia.addColumnMetadata("EMPRESA", Types.VARCHAR);
    tvpExperiencia.addColumnMetadata("PUESTO", Types.VARCHAR);
    tvpExperiencia.addColumnMetadata("FCH_INICIO", Types.DATE);
    tvpExperiencia.addColumnMetadata("FCH_FIN", Types.DATE);
    tvpExperiencia.addColumnMetadata("FUNCIONES", Types.VARCHAR);
    tvpExperiencia.addColumnMetadata("FL_ACTUALIDAD", Types.INTEGER);

    if (experiencias != null && !experiencias.isEmpty()) {
      for (ExperienceRequest experiencia : experiencias) {
        tvpExperiencia.addRow(
            experiencia.getEmpresa(),
            experiencia.getPuesto(),
            Common.formatDate(experiencia.getFechaInicio()),
            Common.formatDate(experiencia.getFechaFin()),
            experiencia.getFunciones(),
            experiencia.getFlActualidad());
      }
    }

    return tvpExperiencia;
  }

  private SQLServerDataTable loadTvpEducacion(List<EducationRequest> educaciones) throws SQLServerException {
    SQLServerDataTable tvpEducacion = new SQLServerDataTable();

    tvpEducacion.addColumnMetadata("INSTITUCION", Types.VARCHAR);
    tvpEducacion.addColumnMetadata("CARRERA", Types.VARCHAR);
    tvpEducacion.addColumnMetadata("GRADO", Types.VARCHAR);
    tvpEducacion.addColumnMetadata("FCH_INICIO", Types.DATE);
    tvpEducacion.addColumnMetadata("FCH_FIN", Types.DATE);
    tvpEducacion.addColumnMetadata("FL_ACTUALIDAD", Types.INTEGER);
    tvpEducacion.addColumnMetadata("TIPO_FECHA_EDUCACIONES", Types.INTEGER);

    if (educaciones != null && !educaciones.isEmpty()) {
      for (EducationRequest educacion : educaciones) {
        tvpEducacion.addRow(
            educacion.getInstitucion(),
            educacion.getCarrera(),
            educacion.getGrado(),
            Common.formatDate(educacion.getFechaInicio()),
            Common.formatDate(educacion.getFechaFin()),
            educacion.getFlActualidad(),
            educacion.getTipoFechaEducaciones());
      }
    }

    return tvpEducacion;
  }

  private SQLServerDataTable loadTvpIdioma(List<LanguageRequest> idiomas) throws SQLServerException {
    SQLServerDataTable tvpIdioma = new SQLServerDataTable();

    tvpIdioma.addColumnMetadata("ID_IDIOMA", Types.INTEGER);
    tvpIdioma.addColumnMetadata("ID_NIVEL", Types.INTEGER);
    tvpIdioma.addColumnMetadata("ESTRELLAS", Types.INTEGER);

    if (idiomas != null && !idiomas.isEmpty()) {
      for (LanguageRequest idioma : idiomas) {
        tvpIdioma.addRow(
            idioma.getIdIdioma(),
            idioma.getIdNivel(),
            idioma.getEstrellas());
      }
    }

    return tvpIdioma;
  }

  public BaseResponse addTalentToFavourite(BaseRequest baseRequest, TalentToFavRequest favRequest) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_USUARIO_FAVORITOS_TALENTO_INS");
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
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_HABILIDAD_TECNICA_INS");
    BaseResponse baseResponse = new BaseResponse();

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("ID_TALENTO", techAbilityRequest.getIdTalento())
        .addValue("ID_HABILIDAD", techAbilityRequest.getIdHabilidad())
        .addValue("HABILIDAD", techAbilityRequest.getHabilidad())
        .addValue("ANIOS", techAbilityRequest.getAnios())
        .addValue("ID_ROL", baseRequest.getIdRol())
        .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
        .addValue("ID_USUARIO", baseRequest.getIdUsuario())
        .addValue("USERNAME", baseRequest.getUsername());

    return simpleSPCall(simpleJdbcCall, baseResponse, params);
  }

  public BaseResponse addTalentSoftAbility(BaseRequest baseRequest, SoftAbilityRequest softAbilityRequest) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_HABILIDAD_BLANDA_INS");
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
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_TALENTO_EXPERIENCIA_DEL");
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

    System.out.println(educationRequest);

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
        .addValue("USERNAME", baseRequest.getUsername())
        .addValue("TIPO_FECHA_EDUCACIONES", educationRequest.getTipoFechaEducaciones());

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
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_TALENTO_EDUCACION_DEL");
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

  public FeedbackResponse addOrUpdateTalentFeedback(BaseRequest baseRequest, FeedbackRequest feedbackRequest) {
    boolean isUpdate = feedbackRequest.getIdFeedback() != null && feedbackRequest.getIdFeedback() > 0;
    String procedureName = isUpdate ? "SP_BT_TALENTO_FEEDBACK_UPD" : "SP_BT_TALENTO_FEEDBACK_INS";
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName);
    FeedbackResponse feedbackResponse = new FeedbackResponse(1, "", 0);

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
        feedbackResponse.setIdMensaje(1);
        feedbackResponse.setMensaje("El campo idTalento es obligatorio para agregar feedback");

        return feedbackResponse;
      }
      params.addValue("ID_TALENTO", feedbackRequest.getIdTalento());
    }

    Map<String, Object> result = simpleJdbcCall.execute(params);
    List<Map<String, Object>> resultSet1 = (List<Map<String, Object>>) result.get("#result-set-1");

    if (resultSet1 != null && !resultSet1.isEmpty()) {
      Map<String, Object> row = resultSet1.get(0);

      feedbackResponse.setIdMensaje((Integer) row.get("ID_TIPO_MENSAJE"));
      feedbackResponse.setMensaje((String) row.get("MENSAJE"));

      if (feedbackResponse.getIdMensaje() == 2) {
        List<Map<String, Object>> resultSet2 = (List<Map<String, Object>>) result.get("#result-set-2");
        Map<String, Object> rowAvgEstrella = resultSet2.get(0);

        feedbackResponse.setAvgEstrellas((Integer) rowAvgEstrella.get("PROM_ESTRELLAS"));
      }
    }

    return feedbackResponse;
  }

  public FeedbackResponse deleteTalentFeedback(BaseRequest baseRequest, Integer idFeedback) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_TALENTO_FEEDBACK_DEL");
    FeedbackResponse feedbackResponse = new FeedbackResponse(1, "", 0);

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("ID_FEEDBACK", idFeedback)
        .addValue("ID_ROL", baseRequest.getIdRol())
        .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
        .addValue("ID_USUARIO", baseRequest.getIdUsuario())
        .addValue("USERNAME", baseRequest.getUsername());

    Map<String, Object> result = simpleJdbcCall.execute(params);
    List<Map<String, Object>> resultSet1 = (List<Map<String, Object>>) result.get("#result-set-1");

    if (resultSet1 != null && !resultSet1.isEmpty()) {
      Map<String, Object> row = resultSet1.get(0);

      feedbackResponse.setIdMensaje((Integer) row.get("ID_TIPO_MENSAJE"));
      feedbackResponse.setMensaje((String) row.get("MENSAJE"));

      if (feedbackResponse.getIdMensaje() == 2) {
        List<Map<String, Object>> resultSet2 = (List<Map<String, Object>>) result.get("#result-set-2");
        Map<String, Object> rowAvgEstrella = resultSet2.get(0);

        feedbackResponse.setAvgEstrellas((Integer) rowAvgEstrella.get("PROM_ESTRELLAS"));
      }
    }

    return feedbackResponse;
  }

  public BaseResponse uploadTalentFile(BaseRequest baseRequest, UploadTalentFileRequest uploadTalentFileRequest) {
    String ruta = Constante.RUTA_REPOSITORIO_TALENTO_ARCHIVOS + uploadTalentFileRequest.getNombreArchivo() + "."
        + uploadTalentFileRequest.getExtensionArchivo();
    ruta = ruta.replace("[ID]", uploadTalentFileRequest.getIdTalento().toString());
    BaseResponse baseResponse = new BaseResponse();

    boolean archivoGuardado = guardarArchivoAws(uploadTalentFileRequest.getString64(),
        uploadTalentFileRequest.getExtensionArchivo(), ruta, false);

    if (!archivoGuardado) {
      baseResponse.setIdMensaje(1);
      baseResponse.setMensaje("No se pudo guardar el archivo");
      return baseResponse;
    }

    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_TALENTO_ARCHIVOS_INS");

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

    return simpleSPCall(simpleJdbcCall, baseResponse, params);
  }

  /**
   * Persiste en BD un archivo de talento que YA fue subido a S3 mediante una URL
   * pre-firmada (PUT directo). No vuelve a subir el archivo: solo registra la ruta
   * recibida. Si {@code idArchivo} viene informado (> 0) reemplaza el archivo
   * existente (UPD); en caso contrario inserta uno nuevo (INS).
   */
  public BaseResponse confirmTalentFile(BaseRequest baseRequest, TalentConfirmUploadRequest request) {
    boolean isUpdate = request.getIdArchivo() != null && request.getIdArchivo() > 0;

    if (isUpdate) {
      SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
          .withProcedureName("SP_BT_TALENTO_ARCHIVOS_UPD");

      SqlParameterSource params = new MapSqlParameterSource()
          .addValue("ID_TALENTO", request.getIdTalento())
          .addValue("ID_ARCHIVO", request.getIdArchivo())
          .addValue("NOMBRE_ARCHIVO", request.getNombreArchivo())
          .addValue("ID_TIPO_ARCHIVO", request.getIdTipoArchivo())
          .addValue("ID_TIPO_DOCUMENTO", request.getIdTipoDocumento())
          .addValue("RUTA_ARCHIVO", request.getPath())
          .addValue("ID_ROL", baseRequest.getIdRol())
          .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
          .addValue("ID_USUARIO", baseRequest.getIdUsuario())
          .addValue("USERNAME", baseRequest.getUsername());

      Map<String, Object> result = simpleJdbcCall.execute(params);
      List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

      if (resultSet != null && !resultSet.isEmpty()) {
        return getBaseResponse(resultSet);
      }

      return new BaseResponse(3, "Error al conectar a la base de datos");
    }

    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_TALENTO_ARCHIVOS_INS");
    BaseResponse baseResponse = new BaseResponse();

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("ID_TALENTO", request.getIdTalento())
        .addValue("NOMBRE_ARCHIVO", request.getNombreArchivo())
        .addValue("ID_TIPO_ARCHIVO", request.getIdTipoArchivo())
        .addValue("ID_TIPO_DOCUMENTO", request.getIdTipoDocumento())
        .addValue("RUTA_ARCHIVO", request.getPath())
        .addValue("ID_ROL", baseRequest.getIdRol())
        .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
        .addValue("ID_USUARIO", baseRequest.getIdUsuario())
        .addValue("USERNAME", baseRequest.getUsername());

    return simpleSPCall(simpleJdbcCall, baseResponse, params);
  }

  public BaseResponse updateTalentFile(BaseRequest baseRequest, UpdateTalentFileRequest updateTalentFileRequest,
      String ruta) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_TALENTO_ARCHIVOS_UPD");
    BaseResponse baseResponse = new BaseResponse();

    // build file path
    ruta = ruta + updateTalentFileRequest.getNombreArchivo() + "." + updateTalentFileRequest.getExtensionArchivo();
    // assign a talent folder
    ruta = ruta.replace("[ID]", updateTalentFileRequest.getIdTalento().toString());

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("ID_TALENTO", updateTalentFileRequest.getIdTalento())
        .addValue("ID_ARCHIVO", updateTalentFileRequest.getIdArchivo())
        .addValue("NOMBRE_ARCHIVO", updateTalentFileRequest.getNombreArchivo())
        .addValue("ID_TIPO_ARCHIVO", updateTalentFileRequest.getIdTipoArchivo())
        .addValue("ID_TIPO_DOCUMENTO", updateTalentFileRequest.getIdTipoDocumento())
        .addValue("RUTA_ARCHIVO", ruta)
        .addValue("ID_ROL", baseRequest.getIdRol())
        .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
        .addValue("ID_USUARIO", baseRequest.getIdUsuario())
        .addValue("USERNAME", baseRequest.getUsername());

    Map<String, Object> result = simpleJdbcCall.execute(params);
    List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

    if (resultSet != null && !resultSet.isEmpty()) {
      baseResponse = getBaseResponse(resultSet);

      // save new file
      boolean cvGuardado = guardarArchivoAws(updateTalentFileRequest.getString64(),
          updateTalentFileRequest.getExtensionArchivo(), ruta, true);

      if (!cvGuardado) {
        baseResponse.setIdMensaje(1);
        baseResponse.setMensaje("No se pudo guardar el archivo");
      }

      return baseResponse;
    }

    baseResponse.setIdMensaje(3);
    baseResponse.setMensaje("Error al conectar a la base de datos");

    return baseResponse;
  }

  // Espacio solo para migración de archivos
  public void migrateProfilePhoto() {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_TALENTO_MIGRAR_FOTO_LST");
    Map<String, Object> result = simpleJdbcCall.execute();
    List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

    if (resultSet != null && !resultSet.isEmpty()) {
      List<MigrationProfilePhotoDTO> lstMigration = new ArrayList<>();

      for (Map<String, Object> talentRow : resultSet) {
        MigrationProfilePhotoDTO migration = new MigrationProfilePhotoDTO(
            (Integer) talentRow.get("ID_TALENTO"),
            (String) talentRow.get("NUEVA_RUTA_IMAGEN"),
            (String) talentRow.get("FILE_EXTENSION"),
            (String) talentRow.get("IM_IMAGE_VARCHAR"));
        lstMigration.add(migration);
      }

      for (MigrationProfilePhotoDTO objMigration : lstMigration) {
        if (objMigration.getNuevaRutaImagen() != null && !objMigration.getNuevaRutaImagen().equals("")) {
          System.out.println("Cargando objeto de id: " + objMigration.getIdTalento());
          guardarImagenMigracion(objMigration.getFileB64(), objMigration.getFileExtension(),
              objMigration.getNuevaRutaImagen());
          migrateProfilePhotoUpdate(objMigration.getIdTalento(), objMigration.getNuevaRutaImagen());
        }

      }

    }
  }

  public void migrateProfilePhotoUpdate(Integer idTalento, String rutaImagen) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_TALENTO_MIGRAR_FOTO_UPD");
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
            (String) talentRow.get("ARCHIVO_B64"));
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

  public BaseResponse removeTechnicalSkill(BaseRequest baseRequest, Integer targetId) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_HABILIDAD_TECNICA_DEL");

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("ID_HABILIDAD_TECNICA", targetId)
        .addValue("ID_ROL", baseRequest.getIdRol())
        .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
        .addValue("ID_USUARIO", baseRequest.getIdUsuario())
        .addValue("USERNAME", baseRequest.getUsername());

    Map<String, Object> result = simpleJdbcCall.execute(params);
    List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.getOrDefault("#result-set-1",
        Collections.emptyList());

    if (resultSet.isEmpty())
      return new BaseResponse(3, "No hubo respuesta de la base de datos");

    Map<String, Object> baseRs = resultSet.get(0);

    Integer messageId = (Integer) baseRs.getOrDefault("ID_TIPO_MENSAJE", 3);
    String msg = (String) baseRs.getOrDefault("MENSAJE", "Mensaje de respuesta desconocido");

    return new BaseResponse(messageId, msg);
  }

  public BaseResponse removeSoftSkill(BaseRequest baseRequest, Integer targetId) {
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
        .withProcedureName("SP_BT_HABILIDAD_BLANDA_DEL");

    SqlParameterSource params = new MapSqlParameterSource()
        .addValue("ID_HABILIDAD_BLANDA", targetId)
        .addValue("ID_ROL", baseRequest.getIdRol())
        .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
        .addValue("ID_USUARIO", baseRequest.getIdUsuario())
        .addValue("USERNAME", baseRequest.getUsername());

    Map<String, Object> result = simpleJdbcCall.execute(params);
    List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.getOrDefault("#result-set-1",
        Collections.emptyList());

    if (resultSet.isEmpty())
      return new BaseResponse(3, "No hubo respuesta de la base de datos");

    Map<String, Object> baseRs = resultSet.get(0);

    Integer messageId = (Integer) baseRs.getOrDefault("ID_TIPO_MENSAJE", 3);
    String msg = (String) baseRs.getOrDefault("MENSAJE", "Mensaje de respuesta desconocido");

    return new BaseResponse(messageId, msg);
  }

}
