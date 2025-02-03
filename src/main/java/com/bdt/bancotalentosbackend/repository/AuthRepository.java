package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.dto.UserDTO;
import com.bdt.bancotalentosbackend.model.response.AuthResponse;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.util.JWTHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final JdbcTemplate jdbcTemplate;
    private final JWTHelper jwt;

    public AuthResponse verifyCredentials(String username, String password) {

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("SP_VERIFY_CREDENTIALS");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("USUARIO", username)
                .addValue("CLAVE", password);

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            Map<String, Object> row = resultSet.get(0);
            Integer idTipoMensaje = (Integer) row.get("ID_TIPO_MENSAJE");
            String mensaje = (String) row.get("MENSAJE");

            // Success call
            if (idTipoMensaje == 2) {
                UserDTO user = getUserByUsername(username);
                String token = jwt.generateToken(user);

                return new AuthResponse(new BaseResponse(idTipoMensaje, mensaje), token);
            }
        }

        return new AuthResponse(
                new BaseResponse(3, "Error consultando Base de datos"),
                null
        );
    }

    public UserDTO getUserByUsername(String username) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_USUARIOS_SEL");
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("USUARIO", username);

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            Map<String, Object> row = resultSet.get(0);
            Integer idTipoMensaje = (Integer) row.get("ID_TIPO_MENSAJE");

            if (idTipoMensaje == 2) {
                return  mapToUseDTO(result);
            }
        }

        return new UserDTO();
    }

    private UserDTO mapToUseDTO(Map<String, Object> data) {
        List<Map<String, Object>> resultSet2 = (List<Map<String, Object>>) data.get("#result-set-2");

        if (resultSet2 != null && !resultSet2.isEmpty()) {
            Map<String, Object> userData = resultSet2.get(0);

            // Extract the roles from #result-set-3
            List<Map<String, Object>> resultSet3 = (List<Map<String, Object>>) data.get("#result-set-3");

            List<Integer> roles = new ArrayList<>();
            if (resultSet3 != null && !resultSet3.isEmpty()) {
                for (Map<String, Object> roleData : resultSet3) {
                    // STRING1 contains the roles
                    roles.add((Integer) roleData.get("NUM1"));
                }
            }

            return new UserDTO(
                    (Integer) userData.get("ID_USUARIO"),
                    (Integer) userData.get("ID_EMPRESA"),
                    (String) userData.get("USUARIO"),
                    roles
            );
        }
        return new UserDTO();
    }
}
