package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.mapper.TalentsMapper;
import com.bdt.bancotalentosbackend.model.dto.*;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.SearchRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TalentResponse;
import com.bdt.bancotalentosbackend.model.response.TalentsListResponse;
import com.bdt.bancotalentosbackend.util.TalentsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            Integer idTipoMensaje = (Integer) row.get("ID_TIPO_MENSAJE");
            String mensaje = (String) row.get("MENSAJE");
            Integer totalTalents = (Integer) row.get("TOTAL_LISTA");

            BaseResponse baseResponse = new BaseResponse(idTipoMensaje, mensaje);
            talentsListResponse.setBaseResponse(baseResponse);
            talentsListResponse.setTotal(totalTalents);

            if (idTipoMensaje == 2) {
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
                .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
                .addValue("ID_TALENTO", talentId);

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            Map<String, Object> row = resultSet.get(0);
            Integer idTipoMensaje = (Integer) row.get("ID_TIPO_MENSAJE");
            String mensaje = (String) row.get("MENSAJE");

            BaseResponse baseResponse = new BaseResponse(idTipoMensaje, mensaje);
            talentResponse.setBaseResponse(baseResponse);

            if (idTipoMensaje == 2) {
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
}
