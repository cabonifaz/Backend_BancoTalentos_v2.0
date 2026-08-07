package com.bdt.bancotalentosbackend.repository;

import com.bdt.bancotalentosbackend.model.dto.TariffDTO;
import com.bdt.bancotalentosbackend.model.request.BaseRequest;
import com.bdt.bancotalentosbackend.model.request.TariffRequest;
import com.bdt.bancotalentosbackend.model.response.BaseResponse;
import com.bdt.bancotalentosbackend.model.response.TariffListResponse;
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

@Repository
@RequiredArgsConstructor
public class TariffRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Listado SUPERADMIN paginado con nombres amigables. SP_TARIFARIO_SADMIN_LST.
     * result-set-1 = mensaje + TOTAL_LISTA, result-set-2 = tarifas.
     */
    public TariffListResponse list(BaseRequest baseRequest, String filtro, Integer pagina) {
        TariffListResponse response = new TariffListResponse();
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_TARIFARIO_SADMIN_LST");

            SqlParameterSource params = new MapSqlParameterSource()
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
                List<TariffDTO> registros = new ArrayList<>();
                if (rows != null) {
                    for (Map<String, Object> row : rows) {
                        registros.add(new TariffDTO(
                                (Integer) row.get("ID_TARIFARIO"),
                                (Integer) row.get("ID_CLIENTE"),
                                (String) row.get("RAZON_SOCIAL"),
                                (Integer) row.get("ID_PERFIL"),
                                (String) row.get("PERFIL"),
                                (Integer) row.get("ID_MONEDA"),
                                (String) row.get("MONEDA"),
                                (Integer) row.get("ID_TIPO_TARIFA"),
                                (String) row.get("TIPO_TARIFA"),
                                (BigDecimal) row.get("TARIFA"),
                                (BigDecimal) row.get("TIPO_CAMBIO")));
                    }
                }
                response.setRegistros(registros);
            }
            return response;
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY list tarifario: " + e.getMessage());
            response.setBaseResponse(new BaseResponse(3, e.getMessage()));
            return response;
        }
    }

    /** Alta. SP_TARIFARIO_INS. */
    public BaseResponse create(BaseRequest baseRequest, TariffRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_TARIFARIO_INS");

            SqlParameterSource params = businessParams(baseRequest, request)
                    .addValue("ID_EMPRESA", baseRequest.getIdEmpresa());

            return callAndMap(jdbcCall, params);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY create tarifa: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Edición. SP_TARIFARIO_UPD. */
    public BaseResponse update(BaseRequest baseRequest, TariffRequest request) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_TARIFARIO_UPD");

            SqlParameterSource params = businessParams(baseRequest, request)
                    .addValue("ID_TARIFARIO", request.getIdTarifario());

            return callAndMap(jdbcCall, params);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY update tarifa: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Baja lógica. SP_TARIFARIO_DEL. */
    public BaseResponse delete(BaseRequest baseRequest, Integer idTarifario) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SP_TARIFARIO_DEL");

            SqlParameterSource params = new MapSqlParameterSource()
                    .addValue("ID_TARIFARIO", idTarifario)
                    .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                    .addValue("ID_ROL", baseRequest.getIdRol())
                    .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                    .addValue("USERNAME", baseRequest.getUsername());

            return callAndMap(jdbcCall, params);
        } catch (Exception e) {
            System.err.println("Error en REPOSITORY delete tarifa: " + e.getMessage());
            return new BaseResponse(3, e.getMessage());
        }
    }

    /** Campos de negocio comunes a INS/UPD + permiso/auditoría (@USERNAME). */
    private MapSqlParameterSource businessParams(BaseRequest baseRequest, TariffRequest request) {
        return new MapSqlParameterSource()
                .addValue("ID_CLIENTE", request.getIdCliente())
                .addValue("ID_PERFIL", request.getIdPerfil())
                .addValue("ID_MONEDA", request.getIdMoneda())
                .addValue("TARIFA", request.getTarifa())
                .addValue("TIPO_CAMBIO", request.getTipoCambio())
                .addValue("ID_TIPO_TARIFA", request.getIdTipoTarifa())
                .addValue("ID_USUARIO", baseRequest.getIdUsuario())
                .addValue("ID_ROL", baseRequest.getIdRol())
                .addValue("ID_FUNCIONALIDADES", baseRequest.getFuncionalidades())
                .addValue("USERNAME", baseRequest.getUsername());
    }

    private BaseResponse callAndMap(SimpleJdbcCall jdbcCall, SqlParameterSource params) {
        Map<String, Object> result = jdbcCall.execute(params);
        List<Map<String, Object>> messageSet = (List<Map<String, Object>>) result.get("#result-set-1");
        if (messageSet == null || messageSet.isEmpty()) {
            return new BaseResponse(3, "No hubo respuesta de la base de datos");
        }
        return getBaseResponse(messageSet);
    }
}
