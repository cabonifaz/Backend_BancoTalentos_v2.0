package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.dto.UserAdminDTO;
import com.bdt.bancotalentosbackend.model.dto.UserFavDTO;
import com.bdt.bancotalentosbackend.model.request.FavCollectionRequest;
import com.bdt.bancotalentosbackend.model.request.UpdateUserRequest;
import com.bdt.bancotalentosbackend.model.request.UserAdminRequest;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.UserAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.UserFavListResponse;
import com.bdt.bancotalentosbackend.model.response.UserInfoResponse;

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

    public BaseResponse addFavouriteCollection(BaseRequest baseRequest, FavCollectionRequest favCollectionRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_BT_USUARIO_FAVORITOS_INS");
        BaseResponse baseResponse = new BaseResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("NOMBRE_FAVORITOS", favCollectionRequest.getCollectionName())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("USERNAME", baseRequest.getUsername());

        return simpleSPCall(simpleJdbcCall, baseResponse, params);
    }

    public UserInfoResponse getUserInfo(BaseRequest baseRequest) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_USUARIOS_SEL");
        UserInfoResponse userInfoResponse = new UserInfoResponse();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("USUARIO", baseRequest.getUsername())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario());

        Map<String, Object> result = simpleJdbcCall.execute(params);
        List<Map<String, Object>> resultSet = (List<Map<String, Object>>) result.get("#result-set-2");

        if (resultSet != null && !resultSet.isEmpty()) {
            Map<String, Object> row = resultSet.get(0);
            userInfoResponse.setUsuario((String) row.get("USUARIO"));
            userInfoResponse.setNombres((String) row.get("NOMBRES"));
            userInfoResponse.setApellidos((String) row.get("APELLIDOS"));
            userInfoResponse.setEmail((String) row.get("EMAIL"));
            userInfoResponse.setTelefono((String) row.get("TELEFONO"));
        }
        return userInfoResponse;
    }

    public BaseResponse updateUserInfo(BaseRequest baseRequest, UpdateUserRequest updateUserRequest) {
        
        try {
            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_USUARIOS_UPD");
            BaseResponse baseResponse = new BaseResponse();

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("USERNAME", baseRequest.getUsername())
                    .addValue("TELEFONO", updateUserRequest.getTelefono());

            return simpleSPCall(simpleJdbcCall, baseResponse, params);
        } catch (Exception e) { 

            System.err.println("ERROR EN updateUserInfo:::");
            System.err.println(e.getMessage());
            return new BaseResponse(3, "Error interno::" + e.getMessage());

        }
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

    /* ==================== Administración de usuarios (SUPERADMIN) ==================== */

    /** Listado SUPERADMIN paginado con rol y estado. SP_USUARIOS_LST. */
    public UserAdminListResponse listUsuariosAdmin(BaseRequest baseRequest, String filtro, Integer idEstado, Integer pagina) {
        UserAdminListResponse response = new UserAdminListResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_USUARIOS_LST");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("FILTRO", filtro)
                    .addValue("ID_ESTADO", idEstado)
                    .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
                    .addValue("N_PAG", pagina)
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("USUARIO", baseRequest.getUsername())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                response.setBaseResponse(new BaseResponse(3, "No hubo respuesta de la base de datos"));
                return response;
            }

            response.setBaseResponse(getBaseResponse(messageSet));
            response.setTotal((Integer) messageSet.get(0).get("TOTAL_LISTA"));

            if (response.getBaseResponse().getIdMensaje() == 2) {
                List<Map<String, Object>> rows = (List<Map<String, Object>>) result.get("#result-set-2");
                List<UserAdminDTO> registros = new ArrayList<>();
                if (rows != null) {
                    for (Map<String, Object> row : rows) {
                        registros.add(new UserAdminDTO(
                                (Integer) row.get("ID_USUARIO"),
                                (Integer) row.get("ID_EMPRESA"),
                                (String) row.get("NOMBRES"),
                                (String) row.get("APELLIDOS"),
                                (String) row.get("USUARIO"),
                                (String) row.get("EMAIL"),
                                (String) row.get("CARGO"),
                                (String) row.get("TELEFONO"),
                                (String) row.get("FIRMA"),
                                (Integer) row.get("ID_ESTADO_REGISTRO"),
                                (Integer) row.get("ID_ROL"),
                                (String) row.get("ROL")));
                    }
                }
                response.setRegistros(registros);
            }
            return response;
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY list usuarios: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    /** Edición de datos + cambio de rol. SP_USUARIOS_SADMIN_UPD. */
    public BaseResponse updateUsuarioAdmin(BaseRequest baseRequest, UserAdminRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_USUARIOS_SADMIN_UPD");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_USUARIO_EDITAR", request.getIdUsuario())
                    .addValue("NOMBRES", request.getNombres())
                    .addValue("APELLIDOS", request.getApellidos())
                    .addValue("EMAIL", request.getEmail())
                    .addValue("CARGO", request.getCargo())
                    .addValue("TELEFONO", request.getTelefono())
                    .addValue("FIRMA", request.getFirma())
                    .addValue("ID_TIPO_ROL", request.getIdTipoRol())
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("USERNAME", baseRequest.getUsername());

            return callAndMapUsuarioAdmin(jdbcCall, params);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY update usuario: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Baja lógica. SP_USUARIOS_DEL. */
    public BaseResponse deleteUsuarioAdmin(BaseRequest baseRequest, Integer idUsuarioEditar) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_USUARIOS_DEL");
            return callAndMapUsuarioAdmin(jdbcCall, estadoParamsUsuarioAdmin(baseRequest, idUsuarioEditar));
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY delete usuario: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Reactivación. SP_USUARIOS_ACT. */
    public BaseResponse reactivateUsuarioAdmin(BaseRequest baseRequest, Integer idUsuarioEditar) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_USUARIOS_ACT");
            return callAndMapUsuarioAdmin(jdbcCall, estadoParamsUsuarioAdmin(baseRequest, idUsuarioEditar));
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY reactivate usuario: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    private SqlParameterSource estadoParamsUsuarioAdmin(BaseRequest baseRequest, Integer idUsuarioEditar) {
        return new MapSqlParameterSource()
                .addValue("ID_USUARIO_EDITAR", idUsuarioEditar)
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("USERNAME", baseRequest.getUsername());
    }

    private BaseResponse callAndMapUsuarioAdmin(SimpleJdbcCall jdbcCall, SqlParameterSource params) {
        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");
        if (messageSet == null || messageSet.isEmpty()) {
            return new BaseResponse(3, "No hubo respuesta de la base de datos");
        }
        return getBaseResponse(messageSet);
    }
}
