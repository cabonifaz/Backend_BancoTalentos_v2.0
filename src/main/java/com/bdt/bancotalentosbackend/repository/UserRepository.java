package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.request.AddFavCollectionRequest;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
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
}
