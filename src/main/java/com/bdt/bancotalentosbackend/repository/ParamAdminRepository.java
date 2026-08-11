package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.dto.ParamItemDTO;
import com.bdt.bancotalentosbackend.model.dto.ParamMasterDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.ParamAdminRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.InsertUpdateResponse;
import com.bdt.bancotalentosbackend.model.response.ParamItemListResponse;
import com.bdt.bancotalentosbackend.model.response.ParamMasterListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bdt.bancotalentosbackend.util.Common.getBaseResponse;
import static com.bdt.bancotalentosbackend.util.Common.getInsertUpdateResponse;

@Repository
@RequiredArgsConstructor
public class ParamAdminRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Modo 1: lista de maestros paginada por maestro. SP_PARAMETROS_SADMIN_LST
     * con @ID_MAESTRO NULL. result-set-1 = mensaje + TOTAL_LISTA, result-set-2 = maestros.
     */
    public ParamMasterListResponse listMasters(BaseRequest baseRequest, String filtro, Integer pagina) {
        ParamMasterListResponse response = new ParamMasterListResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_PARAMETROS_SADMIN_LST");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_MAESTRO", null)
                    .addValue("FILTRO", filtro)
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
                List<ParamMasterDTO> registros = new ArrayList<>();
                if (rows != null) {
                    for (Map<String, Object> row : rows) {
                        registros.add(new ParamMasterDTO(
                                (Integer) row.get("ID_MAESTRO"),
                                (String) row.get("DESCRIPCION"),
                                (Integer) row.get("TOTAL_REGISTROS"),
                                (Integer) row.get("REGISTROS_ACTIVOS"),
                                (Integer) row.get("REGISTROS_INACTIVOS")));
                    }
                }
                response.setRegistros(registros);
            }
            return response;
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY listMasters: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    /**
     * Modo 2: parámetros de un maestro, paginados por parámetro.
     * SP_PARAMETROS_SADMIN_LST con @ID_MAESTRO informado. {@code pagina} nulo devuelve todo.
     */
    public ParamItemListResponse listByMaster(BaseRequest baseRequest, Integer idMaestro, Integer pagina) {
        ParamItemListResponse response = new ParamItemListResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_PARAMETROS_SADMIN_LST");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_MAESTRO", idMaestro)
                    .addValue("FILTRO", null)
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
                List<ParamItemDTO> registros = new ArrayList<>();
                if (rows != null) {
                    for (Map<String, Object> row : rows) {
                        registros.add(new ParamItemDTO(
                                (Integer) row.get("ID_PARAMETRO"),
                                (Integer) row.get("ID_MAESTRO"),
                                (String) row.get("DESCRIPCION"),
                                (Integer) row.get("ID_SUB_MAESTRO"),
                                toDecimal(row.get("NUM1")),
                                toDecimal(row.get("NUM2")),
                                toDecimal(row.get("NUM3")),
                                (String) row.get("STRING1"),
                                (String) row.get("STRING2"),
                                (String) row.get("STRING3"),
                                toStr(row.get("DATE1")),
                                toStr(row.get("DATE2")),
                                toStr(row.get("DATE3")),
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
            System.err.println("Error en REPOSITORY listByMaster: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    /** Alta genérica. SP_PARAMETROS_INS. Devuelve el ID del nuevo parámetro. */
    public InsertUpdateResponse create(BaseRequest baseRequest, ParamAdminRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_PARAMETROS_INS");

            SqlParameterSource params = upsertParams(baseRequest, request);

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                return new InsertUpdateResponse(3, "No hubo respuesta de la base de datos", null);
            }
            return getInsertUpdateResponse(messageSet);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY create parametro: " + e.getMessage());
            return new InsertUpdateResponse(3, e.getMessage(), null);
        }
    }

    /** Edición genérica (incluye ID_MAESTRO). SP_PARAMETROS_UPD. */
    public BaseResponse update(BaseRequest baseRequest, ParamAdminRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_PARAMETROS_UPD");

            SqlParameterSource params = ((MapSqlParameterSource) upsertParams(baseRequest, request))
                    .addValue("ID_PARAMETRO", request.getIdParametro());

            Map<String, Object> result = jdbcCall.execute(params);
            List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");

            if (messageSet == null || messageSet.isEmpty()) {
                return new BaseResponse(3, "No hubo respuesta de la base de datos");
            }
            return getBaseResponse(messageSet);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY update parametro: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Baja lógica. SP_PARAMETROS_DEL. */
    public BaseResponse delete(BaseRequest baseRequest, Integer idParametro) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_PARAMETROS_DEL");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_PARAMETRO", idParametro)
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
            System.err.println("Error en REPOSITORY delete parametro: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Parámetros comunes de INS/UPD (todos los campos editables + auditoría). */
    private SqlParameterSource upsertParams(BaseRequest baseRequest, ParamAdminRequest request) {
        return new MapSqlParameterSource()
                .addValue("ID_MAESTRO", request.getIdMaestro())
                .addValue("DESCRIPCION", request.getDescripcion())
                .addValue("ID_SUB_MAESTRO", request.getIdSubMaestro())
                .addValue("NUM1", request.getNum1())
                .addValue("NUM2", request.getNum2())
                .addValue("NUM3", request.getNum3())
                .addValue("STRING1", request.getString1())
                .addValue("STRING2", request.getString2())
                .addValue("STRING3", request.getString3())
                .addValue("DATE1", request.getDate1())
                .addValue("DATE2", request.getDate2())
                .addValue("DATE3", request.getDate3())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("USERNAME", baseRequest.getUsername());
    }

    private static BigDecimal toDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return BigDecimal.valueOf(((Number) value).doubleValue());
        return new BigDecimal(value.toString());
    }

    private static String toStr(Object value) {
        return value != null ? value.toString() : null;
    }
}
