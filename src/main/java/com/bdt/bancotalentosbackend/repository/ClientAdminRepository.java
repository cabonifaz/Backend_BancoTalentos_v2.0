package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.dto.ClientAdminDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.ClientAdminRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.ClientAdminListResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
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
import static com.bdt.bancotalentosbackend.util.Common.getInsertUpdateResponse;

@Repository
@RequiredArgsConstructor
public class ClientAdminRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Listado SUPERADMIN paginado. SP_CLIENTE_SADMIN_LST.
     * result-set-1 = mensaje + TOTAL_LISTA, result-set-2 = clientes.
     */
    public ClientAdminListResponse list(BaseRequest baseRequest, String filtro, Integer idEstado, Integer pagina) {
        ClientAdminListResponse response = new ClientAdminListResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_CLIENTE_SADMIN_LST");

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
                List<ClientAdminDTO> registros = new ArrayList<>();
                if (rows != null) {
                    for (Map<String, Object> row : rows) {
                        registros.add(new ClientAdminDTO(
                                (Integer) row.get("ID_CLIENTE"),
                                (Integer) row.get("ID_EMPRESA"),
                                (String) row.get("RUC"),
                                (String) row.get("RAZON_SOCIAL"),
                                (String) row.get("DIRECCION"),
                                (String) row.get("UBICACION"),
                                (String) row.get("DIRECCION_EXACTA"),
                                (String) row.get("USUCRE"),
                                toStr(row.get("FCHCRE")),
                                (String) row.get("USUMOD"),
                                toStr(row.get("FCHMOD")),
                                (Integer) row.get("ID_ESTADO_REGISTRO")));
                    }
                }
                response.setRegistros(registros);
            }
            return response;
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY list clientes: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    /** Alta. SP_CLIENTE_INS. Devuelve el ID del nuevo cliente. */
    public InsertUpdateResponse create(BaseRequest baseRequest, ClientAdminRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_CLIENTE_INS");

            SqlParameterSource params = businessParams(baseRequest, request);

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                return new InsertUpdateResponse(3, "No hubo respuesta de la base de datos", null);
            }
            return getInsertUpdateResponse(messageSet);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY create cliente: " + e.getMessage());
            return new InsertUpdateResponse(3, e.getMessage(), null);
        }
    }

    /** Edición. SP_CLIENTE_UPD. */
    public BaseResponse update(BaseRequest baseRequest, ClientAdminRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_CLIENTE_UPD");

            SqlParameterSource params = ((MapSqlParameterSource) businessParams(baseRequest, request))
                    .addValue("ID_CLIENTE", request.getIdCliente());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                return new BaseResponse(3, "No hubo respuesta de la base de datos");
            }
            return getBaseResponse(messageSet);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY update cliente: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Baja lógica. SP_CLIENTE_DEL. */
    public BaseResponse delete(BaseRequest baseRequest, Integer idCliente) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_CLIENTE_DEL");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_CLIENTE", idCliente)
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("USERNAME", baseRequest.getUsername());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                return new BaseResponse(3, "No hubo respuesta de la base de datos");
            }
            return getBaseResponse(messageSet);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY delete cliente: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Reactivación. SP_CLIENTE_ACT (revalida RUC único entre activos). */
    public BaseResponse reactivate(BaseRequest baseRequest, Integer idCliente) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_CLIENTE_ACT");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_CLIENTE", idCliente)
                    .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("USERNAME", baseRequest.getUsername());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                return new BaseResponse(3, "No hubo respuesta de la base de datos");
            }
            return getBaseResponse(messageSet);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY reactivate cliente: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Campos de negocio comunes a INS/UPD + empresa + auditoría/permiso. */
    private SqlParameterSource businessParams(BaseRequest baseRequest, ClientAdminRequest request) {
        return new MapSqlParameterSource()
                .addValue("RUC", request.getRuc())
                .addValue("RAZON_SOCIAL", request.getRazonSocial())
                .addValue("DIRECCION", request.getDireccion())
                .addValue("UBICACION", request.getUbicacion())
                .addValue("DIRECCION_EXACTA", request.getDireccionExacta())
                .addValue("ID_EMPRESA", baseRequest.getIdEmpresa())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("USERNAME", baseRequest.getUsername());
    }

    private static String toStr(Object value) {
        return value != null ? value.toString() : null;
    }
}
