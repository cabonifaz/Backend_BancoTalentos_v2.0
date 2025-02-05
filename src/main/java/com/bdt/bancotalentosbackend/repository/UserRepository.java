package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.dto.UserFavDTO;
import com.bdt.bancotalentosbackend.model.request.AddFavCollectionRequest;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.UserFavListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bdt.bancotalentosbackend.util.Common.getBaseResponse;
import static com.bdt.bancotalentosbackend.util.Common.simpleSPCall;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public BaseResponse addFavouriteCollection(BaseRequest baseRequest, AddFavCollectionRequest addFavCollectionRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_USUARIO_FAVORITOS_INS");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("NOMBRE_FAVORITOS", addFavCollectionRequest.getCollectionName())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public UserFavListResponse getFavourites(BaseRequest baseRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_USUARIO_FAVORITOS_LST");
        UserFavListResponse userFavListResponse = new UserFavListResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("ID_USUARIO", baseRequest.getIdUsuario());

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-1");

        if (resultSet != null && !resultSet.isEmpty()) {
            userFavListResponse.setBaseResponse(getBaseResponse(resultSet));

            if (userFavListResponse.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> favSet = (List<Map<String, Object>>) result.get("#result-set-2");
                if (favSet != null && !favSet.isEmpty()) {
                    List<UserFavDTO> favourites = new ArrayList<>();

                    for(Map<String, Object> favRow : favSet) {
                        favourites.add(new UserFavDTO(
                                (Integer) favRow.get("ID_USUARIO_FAVORITOS"),
                                (String) favRow.get("NOMBRE_FAVORITOS"))
                        );
                    }

                    userFavListResponse.setUserFavList(favourites);
                }
            }
        }
        return userFavListResponse;
    }
}
