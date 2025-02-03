package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.dto.ParamsDTO;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ParamsListResponse;
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
public class ParamsRepository {
    private final JdbcTemplate jdbcTemplate;

    public ParamsListResponse paramsList(String groupIdMaestros) {
        ParamsListResponse paramsListResponse = new ParamsListResponse();
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_PARAMETROS_LST");

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("GRP_ID_MAESTRO", groupIdMaestros);

        Map<String, Object> resultParams = simpleJdbcCall.execute(params);
        List<Map<String, Object>> paramsResultSet1 = (List<Map<String, Object>>) resultParams.get("#result-set-1");

        if (paramsResultSet1 != null && !paramsResultSet1.isEmpty()) {

            Map<String, Object> row = paramsResultSet1.get(0);

            BaseResponse baseResponse = new BaseResponse(
                    (Integer) row.get("ID_TIPO_MENSAJE"),
                    (String) row.get("MENSAJE"));

            paramsListResponse.setResponse(baseResponse);

            if (baseResponse.getIdMensaje() == 2) {
                List<Map<String, Object>> paramsResultSet2 = (List<Map<String, Object>>) resultParams.get("#result-set-2");
                if (paramsResultSet2 != null && !paramsResultSet2.isEmpty()) {
                    List<ParamsDTO> paramsList = new ArrayList<>();

                    for (Map<String, Object> parametroRow : paramsResultSet2) {
                        paramsList.add(mapParametrosDTO(parametroRow));
                    }

                    paramsListResponse.setParamsList(paramsList);
                    return paramsListResponse;
                }
            }
            return paramsListResponse;
        }
        return null;
    }

    private ParamsDTO mapParametrosDTO(Map<String, Object> parametro) {
        return new ParamsDTO(
                (Integer) parametro.get("ID_PARAMETRO"),
                (Integer) parametro.get("ID_MAESTRO"),
                (Integer) parametro.get("NUM1"),
                (Integer) parametro.get("NUM2"),
                (Integer) parametro.get("NUM3"),
                (String) parametro.get("STRING1"),
                (String) parametro.get("STRING2"),
                (String) parametro.get("STRING3")
        );
    }
}
