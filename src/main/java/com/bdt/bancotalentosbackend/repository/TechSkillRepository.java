package com.bdt.bancotalentosbackend.repository;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.NewTechSkillReponse;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
@RequiredArgsConstructor
public class TechSkillRepository {

    private static final Logger logger = LoggerFactory.getLogger(TechSkillRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public NewTechSkillReponse save(BaseRequest baseRequest, String skillName) {

        BaseResponse baseResponse = new BaseResponse();
        NewTechSkillReponse response = new NewTechSkillReponse();

        try {

            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_PARAMETROS_TECHSKILL_INS")
                    .declareParameters(
                            new SqlParameter("SKILL_NAME", Types.VARCHAR),
                            new SqlParameter("USCRE", Types.VARCHAR),
                            new SqlParameter("FCHCRE", Types.TIMESTAMP));

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("SKILL_NAME", skillName)
                    .addValue("USCRE", baseRequest.getUsername())
                    .addValue("FCHCRE", null);

            Map<String, Object> result = simpleJdbcCall.execute(params);
            List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

            Integer idMensaje = (Integer) resultSet.get(0).get("ID_TIPO_MENSAJE");
            String mensaje = (String) resultSet.get(0).get("MENSAJE");
            Integer idSkill = (Integer) resultSet.get(0).get("NUEVO_ID");

            response.setIdSkill(idSkill);
            baseResponse.setIdMensaje(idMensaje);
            baseResponse.setMensaje(mensaje);
            response.setBaseResponse(baseResponse);

            logger.info("Result: {}", result);
            logger.info("Technical skill '{}' saved to DB by user '{}'", skillName, baseRequest.getUsername());

            return response;
        } catch (Exception e) {
            logger.error("Error saving technical skill '{}': {}", skillName, e.getMessage());
            baseResponse.setIdMensaje(3);
            baseResponse.setMensaje("Error inesperado");
            response.setBaseResponse(baseResponse);
            return response;
        }
    }

}