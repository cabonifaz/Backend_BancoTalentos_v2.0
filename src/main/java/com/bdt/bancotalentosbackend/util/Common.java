package com.bdt.bancotalentosbackend.util;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Common {
    public static BaseRequest createBaseRequest(UserDTO userDTO, String funcionalidades) {
        BaseRequest baseRequest = new BaseRequest();
        if (userDTO != null) {
            if (userDTO.getIdRoles() != null && !userDTO.getIdRoles().isEmpty()) {
                List<Integer> roles = userDTO.getIdRoles();
                Integer idRol = roles.get(0);
                baseRequest.setIdRol(idRol);
            }

            baseRequest.setIdUsuario(userDTO.getIdUsuario());
            baseRequest.setIdEmpresa(userDTO.getIdEmpresa());
            baseRequest.setFuncionalidades(funcionalidades);
            baseRequest.setUsername(userDTO.getUsuario());

            return baseRequest;
        }
        return baseRequest;
    }

    public static LocalDate formatDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (date != null && !date.isEmpty()) {
            return LocalDate.parse(date, formatter);
        }
        return null;
    }

    public static BaseResponse simpleSPCall(SimpleJdbcCall jdbcCall, BaseResponse baseResponse, SqlParameterSource params) {
        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            Map<String, Object> row = resultSet.get(0);

            baseResponse.setIdMensaje((Integer) row.get("ID_TIPO_MENSAJE"));
            baseResponse.setMensaje((String) row.get("MENSAJE"));
        }

        return baseResponse;
    }

    public static BaseResponse getBaseResponse(List<Map<String, Object>> resultSet) {
        Map<String, Object> row = resultSet.get(0);
        return new BaseResponse((Integer) row.get("ID_TIPO_MENSAJE"), (String) row.get("MENSAJE"));
    }
}
